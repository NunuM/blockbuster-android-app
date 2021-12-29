package me.nunum.blockbuster.model;

import androidx.annotation.NonNull;

public class MovieFilters {



    public enum ResultOrder {
        DESC, ASC;

        public static ResultOrder formIdx(int idx) {
            if (idx == 0)
                return ResultOrder.DESC;

            return ResultOrder.ASC;
        }
    }

    public enum OrderByField {
        INSERTED_DATE {
            @NonNull
            @Override
            public String toString() {
                return "inserted";
            }
        }, IMDB_RATING {
            @NonNull
            @Override
            public String toString() {
                return "imdbrating";
            }
        };
    }

    private String title = "";
    private OrderByField orderField = OrderByField.INSERTED_DATE;
    private ResultOrder resultOrder = ResultOrder.DESC;
    private String genre = "";
    private Boolean seen = null;

    public MovieFilters() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrderField() {
        return orderField.toString();
    }

    public void setOrderField(OrderByField orderField) {
        this.orderField = orderField;
    }

    public ResultOrder getResultOrder() {
        return resultOrder;
    }

    public void setResultOrder(ResultOrder resultOrder) {
        this.resultOrder = resultOrder;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if (genre == null) {
            return;
        }

        this.genre = genre;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Boolean getSeen() {
        return this.seen;
    }

    public boolean hasTitleDefined() {
        return !this.getTitle().isEmpty();
    }

    public boolean hasOrderDefined() {
        return this.orderField != OrderByField.INSERTED_DATE;
    }

    public boolean hasGenreDefined() {
        return !this.getGenre().isEmpty();
    }

    public boolean isDescendantOrder() {
        return ResultOrder.DESC == this.resultOrder;
    }

    public boolean hasSeenDefined() { return  this.seen != null; }
}
