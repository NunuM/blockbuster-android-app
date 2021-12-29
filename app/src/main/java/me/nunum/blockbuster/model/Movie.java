package me.nunum.blockbuster.model;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"title_key"}, unique = true)})
public class Movie {

    private static final String NOT_AVAILABLE = "N/A";


    @PrimaryKey(autoGenerate = true)
    public long id;
    // This acts as a key
    @NonNull
    @ColumnInfo(name = "title_key")
    private String titleKey;
    // This is for the GUI
    @ColumnInfo(name = "display_title")
    private String displayTitle;
    // This is the original title from the XML feed, with quality
    @ColumnInfo(name = "original_title")
    private String originalTitle;
    private String magnet;
    private Integer year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;
    private String plot;
    private String language;
    private String country;
    private String awards;
    private String poster;
    private String metascore;
    @ColumnInfo(index = true)
    private Float imdbrating;
    private String imdbvotes;
    private String imdbid;
    private String type;
    private String dvd;
    private String boxoffice;
    private String production;
    private String website;
    @ColumnInfo(index = true)
    private Date inserted;
    @ColumnInfo(index = true, name = "was_seen")
    private Boolean wasSeen = false;

    @ColumnInfo(name = "user_rating")
    private Float userRating = 0.0f;


    public Movie() {
        titleKey = "";
    }

    @Ignore
    public Movie(@NonNull String titleKey,
                 @NonNull String quality,
                 String originalTitle,
                 String magnet,
                 Integer year) {
        this.titleKey = titleKey;
        this.originalTitle = originalTitle;
        this.magnet = magnet;
        this.year = year;
        this.genre = NOT_AVAILABLE;
    }

    @Ignore
    public Movie(@NotNull String titleKey,
                 String displayTitle,
                 String originalTitle,
                 String magnet,
                 Integer year,
                 String rated,
                 String released,
                 String runtime,
                 String genre,
                 String director,
                 String writer,
                 String actors,
                 String plot,
                 String language,
                 String country,
                 String awards,
                 String poster,
                 String metascore,
                 Float imdbrating,
                 String imdbvotes,
                 String imdbid,
                 String type,
                 String dvd,
                 String boxoffice,
                 String production,
                 String website,
                 Date inserted) {
        this.titleKey = titleKey;
        this.displayTitle = displayTitle;
        this.originalTitle = originalTitle;
        this.magnet = magnet;
        this.year = year;
        this.rated = rated;
        this.released = released;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.plot = plot;
        this.language = language;
        this.country = country;
        this.awards = awards;
        this.poster = poster;
        this.metascore = metascore;
        this.imdbrating = imdbrating;
        this.imdbvotes = imdbvotes;
        this.imdbid = imdbid;
        this.type = type;
        this.dvd = dvd;
        this.boxoffice = boxoffice;
        this.production = production;
        this.website = website;
        this.inserted = inserted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Float getImdbrating() {

        if (null == imdbrating) {
            return 0.0f;
        }

        return imdbrating;
    }

    public void setImdbrating(Float imdbrating) {
        this.imdbrating = imdbrating;
    }

    public Boolean getWasSeen() {
        return wasSeen;
    }

    public void setWasSeen(Boolean wasSeen) {
        this.wasSeen = wasSeen;
    }

    public Float getUserRating() {
        return userRating;
    }

    public void setUserRating(Float userRating) {
        this.userRating = userRating;
    }

    @NonNull
    public String getTitleKey() {
        return titleKey;
    }


    public void setTitleKey(@NonNull String titleKey) {
        this.titleKey = titleKey;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        if (null == genre) {
            return NOT_AVAILABLE;
        }
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        if (null == director) {
            return NOT_AVAILABLE;
        }

        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getActors() {

        if (null == actors) {
            return "N/A";
        }

        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {

        if (null == plot) {
            return NOT_AVAILABLE;
        }

        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getMetascore() {
        return metascore;
    }

    public void setMetascore(String metascore) {
        this.metascore = metascore;
    }

    public String getImdbvotes() {
        return imdbvotes;
    }

    public void setImdbvotes(String imdbvotes) {
        this.imdbvotes = imdbvotes;
    }

    public String getImdbid() {
        return imdbid;
    }

    public void setImdbid(String imdbid) {
        this.imdbid = imdbid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDvd() {
        return dvd;
    }

    public void setDvd(String dvd) {
        this.dvd = dvd;
    }

    public String getBoxoffice() {
        return boxoffice;
    }

    public void setBoxoffice(String boxoffice) {
        this.boxoffice = boxoffice;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getInserted() {
        return inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return titleKey.toLowerCase().trim().equalsIgnoreCase(movie.titleKey.toLowerCase().trim());
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleKey);
    }

    @NotNull
    @Override
    public String toString() {
        return "Movie{" +
                "title='" + titleKey + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", magnet='" + magnet + '\'' +
                ", year=" + year +
                ", rated='" + rated + '\'' +
                ", released='" + released + '\'' +
                ", runtime='" + runtime + '\'' +
                ", genre='" + genre + '\'' +
                ", director='" + director + '\'' +
                ", writer='" + writer + '\'' +
                ", actors='" + actors + '\'' +
                ", plot='" + plot + '\'' +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                ", awards='" + awards + '\'' +
                ", poster='" + poster + '\'' +
                ", metascore='" + metascore + '\'' +
                ", imdbrating=" + imdbrating +
                ", imdbvotes='" + imdbvotes + '\'' +
                ", imdbid='" + imdbid + '\'' +
                ", type='" + type + '\'' +
                ", dvd='" + dvd + '\'' +
                ", boxoffice='" + boxoffice + '\'' +
                ", production='" + production + '\'' +
                ", website='" + website + '\'' +
                ", inserted=" + inserted +
                '}';
    }
}
