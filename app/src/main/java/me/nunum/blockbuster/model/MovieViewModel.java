package me.nunum.blockbuster.model;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.sqlite.db.SimpleSQLiteQuery;
import me.nunum.blockbuster.dao.MovieDao;
import me.nunum.blockbuster.services.ApplicationConstants;

public class MovieViewModel extends ViewModel {

    private LiveData<PagedList<Movie>> textFilterTransformation;
    private MutableLiveData<MovieFilters> movieFilters = new MutableLiveData<>();

    public MovieViewModel(final MovieDao movieDao) {
        super();

        final PagedList.Config myPagingConfig = new PagedList.Config.Builder()
                .setPageSize(ApplicationConstants.PAGINATION_PAGE_SIZE)
                .setPrefetchDistance(ApplicationConstants.PAGINATION_PAGE_PREFETCH)
                .setEnablePlaceholders(true)
                .build();

        textFilterTransformation = Transformations.switchMap(movieFilters, (filters) -> {


            if (filters.hasGenreDefined() || filters.hasSeenDefined() || filters.hasTitleDefined() || filters.hasOrderDefined()) {
                List<String> parts = new ArrayList<>();

                StringBuilder sqlQuery = new StringBuilder();

                if (filters.hasTitleDefined()) {
                    sqlQuery.append("SELECT m.* FROM movie_fts JOIN movie m ON id=docid");
                    parts.add(String.format(" movie_fts.display_title MATCH '%s' ", filters.getTitle().toLowerCase()));
                } else {
                    sqlQuery.append("SELECT m.* FROM movie m");
                }

                if (filters.hasGenreDefined()) {
                    parts.add(String.format(" LOWER(m.genre) like LOWER('%%%s%%') ", filters.getGenre().toLowerCase()));
                }

                if (filters.hasSeenDefined()) {
                    if (filters.getSeen()) {
                        parts.add(" m.was_seen=1");
                    } else {
                        parts.add(" m.was_seen=0");
                    }
                }

                if (!parts.isEmpty()) {
                    sqlQuery.append(" WHERE ");
                }

                for (int i = 0; i < parts.size(); i++) {
                    sqlQuery.append(parts.get(i)).append(" AND");
                }

                if (!parts.isEmpty()) {
                    int idx = sqlQuery.lastIndexOf(" AND");

                    sqlQuery.delete(idx, sqlQuery.length());
                }


                if (filters.isDescendantOrder()) {
                    sqlQuery.append(" ORDER BY m.").append(filters.getOrderField()).append(" DESC");
                } else {
                    sqlQuery.append(" ORDER BY m.").append(filters.getOrderField()).append(" ASC");
                }

                return new LivePagedListBuilder<>(
                        movieDao.moviesWithFiltersQuery(new SimpleSQLiteQuery(sqlQuery.toString())),
                        myPagingConfig).build();
            }


            if (filters.isDescendantOrder()) {
                return new LivePagedListBuilder<>(movieDao.moviesOrderByFieldDesc(),
                        myPagingConfig).build();
            } else {
                return new LivePagedListBuilder<>(movieDao.moviesOrderByFieldAsc(),
                        myPagingConfig)
                        .build();
            }

        });
    }

    public LiveData<PagedList<Movie>> getTextFilterTransformation() {
        return textFilterTransformation;
    }

    public MutableLiveData<MovieFilters> getMovieFilters() {
        return movieFilters;
    }
}
