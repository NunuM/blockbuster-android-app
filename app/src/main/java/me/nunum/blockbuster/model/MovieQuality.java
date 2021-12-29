package me.nunum.blockbuster.model;


import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie_quality", indices = {@Index(value = {"movie_id", "quality"}, unique = true)})
public class MovieQuality {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    private String quality;

    @NonNull
    @ColumnInfo(name = "movie_id")
    private long movieId;


    protected MovieQuality() {
    }


    public MovieQuality(@NonNull String quality, long movieId) {
        this.quality = quality;
        this.movieId = movieId;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getQuality() {
        return quality;
    }

    public void setQuality(@NonNull String quality) {
        this.quality = quality;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieQuality that = (MovieQuality) o;
        return movieId == that.movieId &&
                quality.equals(that.quality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quality, movieId);
    }


    @Override
    public String toString() {
        return "MovieQuality{" +
                "id=" + id +
                ", quality='" + quality + '\'' +
                ", movieId=" + movieId +
                '}';
    }
}
