package com.florianmski.tracktoid.ui.fragments.library;

import android.database.Cursor;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.florianmski.tracktoid.rx.observables.CursorObservable;

import java.util.List;

import rx.Observable;

public class LibraryShowFragment extends LibraryFragment<WShow>
{
    public static LibraryShowFragment newInstance()
    {
        return new LibraryShowFragment();
    }

    public LibraryShowFragment() {}

    @Override
    protected Observable<List<WShow>> createObservable()
    {
        return Observable.create(
                new CursorObservable<List<WShow>>(
                        getActivity(),
                        ProviderSchematic.Shows.CONTENT_URI,
                        ProviderSchematic.Shows.PROJECTION,
                        generateSelection(),
                        null,
                        null)
                {
                    @Override
                    protected List<WShow> toObject(Cursor cursor)
                    {
                        return WShow.unpackList(cursor);
                    }
                });
    }

    private String generateSelection()
    {
        String selection = null;
        if(currentFilter == FILTER_PLAYS)
            selection = ShowColumns.EPISODES_WATCHED + ">=1";
        else if(currentFilter == FILTER_COLLECTION)
            selection = ShowColumns.EPISODES_COLLECTED + ">=1";
        else if(currentFilter == FILTER_WATCHLIST)
            selection = ShowColumns.WATCHLISTED + "=1";

        if(filterHideWatched)
        {
            if(selection == null)
                selection = ShowColumns.EPISODES_WATCHED + "<" + ShowColumns.EPISODES_AIRED;
            else
                selection += " AND " + ShowColumns.EPISODES_WATCHED + "<" + ShowColumns.EPISODES_AIRED;

            // if no episodes aired, we do not hide it
            selection += " OR " + ShowColumns.EPISODES_AIRED + "=0";
        }

        return selection;
    }

    // if I use this it mess up the args that will be applied by Schematic for the subrequests
    private String[] generateSelectionArgs()
    {
        if(currentFilter != FILTER_ALL)
        {
            if(filterHideWatched)
                return new String[]{String.valueOf(1), String.valueOf(0)};
            else
                return new String[]{String.valueOf(1)};
        }
        else if(filterHideWatched)
            return new String[]{String.valueOf(0)};
        else
            return null;
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}
