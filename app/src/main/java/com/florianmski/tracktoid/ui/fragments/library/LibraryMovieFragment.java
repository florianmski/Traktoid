package com.florianmski.tracktoid.ui.fragments.library;

import android.database.Cursor;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WMovie;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.MovieColumns;
import com.florianmski.tracktoid.rx.observables.CursorObservable;

import java.util.List;

import rx.Observable;

public class LibraryMovieFragment extends LibraryFragment<WMovie>
{
    public static LibraryMovieFragment newInstance()
    {
        return new LibraryMovieFragment();
    }

    public LibraryMovieFragment() {}

    @Override
    protected Observable<List<WMovie>> createObservable()
    {
        return Observable.create(new CursorObservable<List<WMovie>>(
                getActivity(),
                ProviderSchematic.Movies.CONTENT_URI,
                ProviderSchematic.Movies.PROJECTION,
                generateSelection(),
                generateSelectionArgs(),
                MovieColumns.TITLE + " ASC") {
            @Override
            protected List<WMovie> toObject(Cursor cursor) {
                return WMovie.unpackList(cursor);
            }
        });
    }

    private String generateSelection()
    {
        String selection = null;
        if(currentFilter == FILTER_PLAYS)
            selection = MovieColumns.WATCHED + "=?";
        else if(currentFilter == FILTER_COLLECTION)
            selection = MovieColumns.COLLECTED + "=?";
        else if(currentFilter == FILTER_WATCHLIST)
            selection = MovieColumns.WATCHLISTED + "=?";

        if(filterHideWatched)
        {
            if(selection == null)
                selection = MovieColumns.WATCHED + "=?";
            else
                selection += " AND " + MovieColumns.WATCHED + "=?";
        }

        return selection;
    }

    private String[] generateSelectionArgs()
    {
        if(currentFilter != FILTER_ALL)
        {
            if(filterHideWatched)
                return new String[]{"1", "0"};
            else
                return new String[]{"1"};
        }
        else if(filterHideWatched)
            return new String[]{"0"};
        else
            return null;
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.MOVIE;
    }
}
