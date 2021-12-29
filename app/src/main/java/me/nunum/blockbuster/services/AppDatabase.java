package me.nunum.blockbuster.services;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import me.nunum.blockbuster.dao.Converters;
import me.nunum.blockbuster.dao.MovieDao;
import me.nunum.blockbuster.model.Movie;
import me.nunum.blockbuster.model.MovieFTS;
import me.nunum.blockbuster.model.MovieQuality;

@Database(entities = {Movie.class, MovieFTS.class, MovieQuality.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();

}
