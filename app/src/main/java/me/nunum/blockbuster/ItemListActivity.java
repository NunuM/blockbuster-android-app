package me.nunum.blockbuster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.nunum.blockbuster.adapters.MovieAdapter;
import me.nunum.blockbuster.dialogue.FiltersDialogue;
import me.nunum.blockbuster.model.MovieFilters;
import me.nunum.blockbuster.model.MovieViewModel;
import me.nunum.blockbuster.services.AppDatabase;
import me.nunum.blockbuster.services.ApplicationConstants;
import me.nunum.blockbuster.services.MovieRemoteSource;


public class ItemListActivity

        extends AppCompatActivity
        implements FiltersDialogue.FilterDialogueListener, MovieAdapter.MovieAdapterListener {

    private static final String TAG = ItemListActivity.class.getSimpleName();

    private RecyclerView listView;
    private MovieAdapter adapter = null;
    private MovieViewModel viewModel;
    private AppDatabase db = null;

    private final MovieFilters movieFilters = new MovieFilters();
    private final LruCache<String, Bitmap> bitmapLruCache = new LruCache<>(100);
    private final Set<String> genres = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        final RequestQueue queue = Volley.newRequestQueue(this);

        this.db = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class, ApplicationConstants.DATABASE_NAME)
                .build();

        this.adapter = new MovieAdapter(this,
                queue,
                bitmapLruCache);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.list_swipe);
        refreshLayout.setRefreshing(true);

        this.listView = findViewById(R.id.item_list);

        setupRecyclerView();

        final Context context = this.getApplicationContext();

        MovieRemoteSource remoteSource = new MovieRemoteSource(context, queue, db.movieDao());
        remoteSource.start();
        try {
            remoteSource.join();
            refreshLayout.setRefreshing(false);
            refreshLayout.setEnabled(false);
            if (remoteSource.numberOfInsertedMovies() > 0) {
                Toast.makeText(ItemListActivity.this, getString(R.string.inserted_movies, remoteSource.numberOfInsertedMovies()), Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Error on movieRemoteSource thread", e);
        }

        loadGenres();
    }


    public void loadGenres() {
        AsyncTask.execute(() -> {
            for (String allGenre : db.movieDao().allGenres()) {
                if (allGenre != null) {
                    for (String p : allGenre.split(",")) {
                        genres.add(p.trim());
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem filter = menu.findItem(R.id.app_bar_filter);

        filter.setOnMenuItemClickListener(item -> {

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(new FiltersDialogue(ItemListActivity.this), "dialogue")
                    .commitAllowingStateLoss();

            return false;
        });

        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchItem = (SearchView) menuItem.getActionView();
        searchItem.setQueryHint("Search");

        searchItem.setOnCloseListener(() -> false);

        searchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                setSearchTitle(newText);
                return false;
            }
        });

        return true;
    }

    private void setupRecyclerView() {

        viewModel = new MovieViewModel(db.movieDao());

        viewModel.getTextFilterTransformation().observe(this, movies -> {
            adapter.submitList(movies);
        });

        listView.setAdapter(adapter);

        viewModel.getMovieFilters().setValue(movieFilters);
    }

    @Override
    public List<CharSequence> genres() {

        List<CharSequence> sortedGenreCollection = new ArrayList<>(genres.size());

        for (String g : genres) {
            sortedGenreCollection.add(capitalize(g));
        }

        Collections.sort(sortedGenreCollection, (o1, o2) -> o1.toString().compareTo(o2.toString()));

        return sortedGenreCollection;
    }

    @Override
    public void applyFilters() {
        viewModel.getMovieFilters().postValue(movieFilters);
    }

    public void setSearchTitle(String title) {
        movieFilters.setTitle(title);

        viewModel.getMovieFilters().setValue(movieFilters);
    }

    @Override
    public MovieFilters getMovieFilters() {
        return movieFilters;
    }

    @Override
    public void launchMovieDetail(String movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intent);
    }

    public static String capitalize(String input) {
        if (input == null || input.length() <= 0) {
            return input;
        }
        char[] chars = new char[1];
        input.getChars(0, 1, chars, 0);
        if (Character.isUpperCase(chars[0])) {
            return input;
        } else {
            return Character.toUpperCase(chars[0]) +
                    String.valueOf(input.toCharArray(), 1, input.length() - 1);
        }
    }
}