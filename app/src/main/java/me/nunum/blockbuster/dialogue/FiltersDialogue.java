package me.nunum.blockbuster.dialogue;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import me.nunum.blockbuster.R;
import me.nunum.blockbuster.model.MovieFilters;

public class FiltersDialogue extends DialogFragment {

    private List<CharSequence> genres;

    private FilterDialogueListener listener;

    public FiltersDialogue(FilterDialogueListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filter_dialogue, container, false);

        final Spinner seenSpinner = view.findViewById(R.id.seen_spinner);
        ArrayAdapter<CharSequence> seenAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.dialogue_seen,
                android.R.layout.simple_spinner_item);

        seenSpinner.setAdapter(seenAdapter);
        seenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    listener.getMovieFilters().setSeen(false);
                } else if (position == 1) {
                    listener.getMovieFilters().setSeen(true);
                } else {
                    listener.getMovieFilters().setSeen(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //ignore
            }
        });

        if (listener.getMovieFilters().hasSeenDefined()) {
            if (listener.getMovieFilters().getSeen()) {
                seenSpinner.setSelection(1);
            } else {
                seenSpinner.setSelection(0);
            }
        } else {
            seenSpinner.setSelection(2);
        }


        genres = listener.genres();
        genres.add(0, getString(R.string.all_genre_label));

        Spinner searchType = (Spinner) view.findViewById(R.id.type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.dialogue_search_type, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchType.setAdapter(adapter);

        searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    listener.getMovieFilters().setOrderField(MovieFilters.OrderByField.INSERTED_DATE);
                } else {
                    listener.getMovieFilters().setOrderField(MovieFilters.OrderByField.IMDB_RATING);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //ignore
            }
        });

        if (listener.getMovieFilters().hasOrderDefined()) {
            searchType.setSelection(1);
        }


        Spinner sortSpinner = (Spinner) view.findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.dialogue_search_order, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);


        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.getMovieFilters().setResultOrder(MovieFilters.ResultOrder.formIdx(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!listener.getMovieFilters().isDescendantOrder()) {
            sortSpinner.setSelection(1);
        }

        Spinner genresSpinner = (Spinner) view.findViewById(R.id.genre_spinner);
        ArrayAdapter<CharSequence> genresAdapter = new ArrayAdapter<>(inflater.getContext(),
                android.R.layout.simple_spinner_item,
                genres);
        genresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genresSpinner.setAdapter(genresAdapter);

        genresSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    listener.getMovieFilters().setGenre("");
                } else {
                    listener.getMovieFilters().setGenre(genres.get(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (listener.getMovieFilters().hasGenreDefined()) {
            genresSpinner.setSelection(genres.indexOf(listener.getMovieFilters().getGenre()));
        }

        Button submit = (Button) view.findViewById(R.id.apply_filter);
        submit.setOnClickListener(v -> {

            listener.applyFilters();

            dismiss();
        });


        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (FilterDialogueListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement FilterDialogueListener");
        }
    }

    public interface FilterDialogueListener {
        List<CharSequence> genres();

        MovieFilters getMovieFilters();

        void applyFilters();
    }

}
