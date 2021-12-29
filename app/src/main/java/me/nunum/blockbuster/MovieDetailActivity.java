package me.nunum.blockbuster;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import me.nunum.blockbuster.model.Movie;
import me.nunum.blockbuster.services.AppDatabase;
import me.nunum.blockbuster.services.ApplicationConstants;

public class MovieDetailActivity extends AppCompatActivity {

    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        final String movieJSON = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        final Movie movie = gson.fromJson(movieJSON, Movie.class);

        toolBarLayout.setTitle(movie.getDisplayTitle());

        final AppDatabase database = Room.databaseBuilder(this.getApplicationContext(),
                AppDatabase.class,
                ApplicationConstants.DATABASE_NAME)
                .build();

        final CheckBox userSeen = (CheckBox) findViewById(R.id.user_seen);
        final RatingBar userRating = (RatingBar) findViewById(R.id.user_rating);
        final TextView directors = (TextView) findViewById(R.id.directors);
        final TextView plot = (TextView) findViewById(R.id.plot);
        final TextView actors = (TextView) findViewById(R.id.actors);
        final TextView quality = (TextView) findViewById(R.id.quality);
        final WebView webView = (WebView) findViewById(R.id.movie_trailer);

        userSeen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AsyncTask.execute(() -> database.movieDao().updateWasSeen(isChecked, movie.getId()));
        });

        userRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                AsyncTask.execute(() -> database.movieDao().updateUserRating((rating * 10.0f) / 5.0f, movie.getId()));
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setDatabaseEnabled(false);
        webView.getSettings().setGeolocationEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setBuiltInZoomControls(true);


        webView.loadUrl(String.format(ApplicationConstants.MOVIE_PREVIEW_URL, movie.getDisplayTitle(), "trailer"));

        plot.setText(Html.fromHtml(movie.getPlot()));
        actors.setText(Html.fromHtml(movie.getActors()));
        directors.setText(Html.fromHtml(movie.getDirector()));

        AsyncTask.execute(() -> {
            final Movie movieById = database.movieDao().findById(movie.getId());

            if (movieById != null) {
                userSeen.setChecked(movieById.getWasSeen());
                userRating.setRating(movieById.getUserRating());
            }

            final List<String> qualityForMovie = database.movieDao().getQualityForMovie(movie.getId());

            StringBuilder qualityText = new StringBuilder();

            if (!qualityForMovie.isEmpty()) {
                qualityText = new StringBuilder(qualityForMovie.get(0));
            }

            for (int i = 1; i < qualityForMovie.size(); i++) {
                qualityText.append(" | ").append(qualityForMovie.get(0));
            }

            quality.setText(qualityText.toString());
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, movie.getTitleKey());

            if (null == movie.getImdbid()) {

                intent.putExtra(Intent.EXTRA_TEXT, String.format("https://www.imdb.com/find?q=%s", movie.getDisplayTitle()));
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, String.format("https://www.imdb.com/title/%s", movie.getImdbid()));
            }

            startActivity(Intent.createChooser(intent, "Share Movie"));
        });
    }
}