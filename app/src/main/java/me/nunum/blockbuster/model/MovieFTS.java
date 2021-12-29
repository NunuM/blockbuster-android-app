package me.nunum.blockbuster.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

@Fts4(contentEntity = Movie.class)
@Entity(tableName = "movie_fts")
public class MovieFTS {

    @PrimaryKey
    Long rowid;

    @ColumnInfo(name = "display_title")
    String displayTitle;

}
