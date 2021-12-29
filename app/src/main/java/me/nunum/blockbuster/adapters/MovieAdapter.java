package me.nunum.blockbuster.adapters;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import me.nunum.blockbuster.R;
import me.nunum.blockbuster.model.Movie;

public class MovieAdapter
        extends PagedListAdapter<Movie, MovieAdapter.MovieViewHolder> {

    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.equals(newItem);
        }
    };

    public interface MovieAdapterListener {
        void launchMovieDetail(String movieJson);
    }

    private RequestQueue queue;
    private MovieAdapterListener movieAdapterListener;
    private LruCache<String, Bitmap> bitmapLruCache;
    private Gson gson = new Gson();

    public MovieAdapter(MovieAdapterListener movieAdapterListener, RequestQueue requestQueue, LruCache<String, Bitmap> bitmapLruCache) {
        super(DIFF_CALLBACK);
        this.queue = requestQueue;
        this.bitmapLruCache = bitmapLruCache;
        this.movieAdapterListener = movieAdapterListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {

        final Movie item = getItem(position);

        if (item == null) {
            return;
        }

        holder.mIdView.setText(item.getDisplayTitle());

        holder.mContentView.setText(String.format("%d | IMDB %.1f | %s", item.getYear(), item.getImdbrating(), item.getGenre()));

        ImageLoader imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return bitmapLruCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                bitmapLruCache.put(url, bitmap);
            }
        });


        holder.mImageView.setImageUrl(item.getPoster(), imageLoader);

        holder.itemView.findViewById(R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieAdapterListener.launchMovieDetail(gson.toJson(item));
            }
        });


    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        final TextView mIdView;
        final TextView mContentView;
        final NetworkImageView mImageView;


        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            mIdView = (TextView) itemView.findViewById(R.id.id_text);
            mContentView = (TextView) itemView.findViewById(R.id.content);
            mImageView = (NetworkImageView) itemView.findViewById(R.id.id_photo);
        }
    }
}
