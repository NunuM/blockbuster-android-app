package me.nunum.blockbuster.dao;

import java.util.List;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import me.nunum.blockbuster.model.Movie;
import me.nunum.blockbuster.model.MovieQuality;

@Dao
public interface MovieDao {

    @Query("UPDATE movie SET user_rating=:rating WHERE id=:id")
    void updateUserRating(Float rating, Long id);

    @Query("UPDATE movie SET was_seen=:wasSeen WHERE id=:id")
    void updateWasSeen(Boolean wasSeen, Long id);

    @Query("SELECT quality FROM movie_quality WHERE movie_id=:movieId")
    List<String> getQualityForMovie(long movieId);

    @Query("SELECT DISTINCT(LOWER(genre)) AS genre FROM movie")
    List<String> allGenres();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Movie... movies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuality(MovieQuality... movieQualities);

    @Query("SELECT count(*) FROM movie_quality WHERE movie_id=:movieId AND quality=:quality")
    int hasQuality(long movieId, String quality);

    @Query("SELECT count(*) FROM movie WHERE LOWER(title_key) LIKE LOWER(:name)")
    int hasTitleName(String name);

    @Query("SELECT * FROM movie WHERE id=:id")
    Movie findById(long id);

    @Query("SELECT * FROM movie WHERE LOWER(title_key)=LOWER(:title)")
    Movie findByTitle(String title);

    @Delete
    void delete(Movie movie);

    @Query("SELECT * FROM movie ORDER BY inserted DESC")
    DataSource.Factory<Integer, Movie> moviesOrderByFieldDesc();

    @Query("SELECT * FROM movie ORDER BY inserted ASC")
    DataSource.Factory<Integer, Movie> moviesOrderByFieldAsc();

    @RawQuery(observedEntities = Movie.class)
    DataSource.Factory<Integer, Movie> moviesWithFiltersQuery(SupportSQLiteQuery sortQuery);

    @Query("INSERT INTO movie_fts(movie_fts) VALUES('rebuild')")
    void rebuild();

}
