package com.florianmski.tracktoid.ui.fragments.calendar;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.adapters.lists.RecyclerCalendarAdapter;
import com.florianmski.tracktoid.data.WEpisode;
import com.florianmski.tracktoid.data.database.DatabaseSchematic;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.ui.activities.EpisodeActivity;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemRecyclerViewFragment;
import com.uwetrottmann.trakt.v2.entities.CalendarEntry;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.entities.ShowIds;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rx.Observable;

public class CalendarFragment extends ItemRecyclerViewFragment<CalendarFragment.CalendarDay> implements RecyclerAdapter.OnItemClickListener
{
    private final static String[] EPISODE_PROJECTION;
    private final static String CURSOR_SHOW_TITLE = "show_title";
    private final static int NUMBER_OF_DAYS_TO_DISPLAY = 7;

    static
    {
        // add the show title as a subquery to the projection
        String[] projection = ProviderSchematic.Episodes.PROJECTION;
        String[] temp = new String[projection.length + 1];
        System.arraycopy(projection, 0, temp, 0, projection.length);
        projection = temp;
        projection[projection.length-1] = "("
                + "SELECT " + ShowColumns.TITLE + " "
                + "FROM " + DatabaseSchematic.SHOWS + " "
                + "WHERE " + ShowColumns.ID_TRAKT + "=" + DatabaseSchematic.EPISODES + "." + EpisodeColumns.SHOW_ID + ") "
                + "AS " + CURSOR_SHOW_TITLE;

        EPISODE_PROJECTION = projection;
    }

    public CalendarFragment() {}

    public static CalendarFragment newInstance()
    {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setInstantLoad(true);
    }

    @Override
    protected RecyclerAdapter<CalendarDay, ?> createAdapter(List<CalendarDay> items)
    {
        return new RecyclerCalendarAdapter(getActivity(), items, this);
    }

    @Override
    protected Observable<List<CalendarDay>> createObservable()
    {
        //        return Observable.create(new TraktObservable<List<CalendarDay>>()
        //        {
        //            @Override
        //            public List<CalendarDay> fire() throws OAuthUnauthorizedException
        //            {
        //                Map<DateTime, List<CalendarEntry>> map = TraktManager.getInstance().calendars().shows(Extended.IMAGES);
        //                return CalendarDay.toList(map);
        //            }
        //        });

        DateTime today = DateHelper.now().withTime(0,0,0,0);

        return Observable.create(new CursorObservable<List<CalendarDay>>(
                getActivity(),
                ProviderSchematic.Episodes.CONTENT_URI,
                EPISODE_PROJECTION,
                EpisodeColumns.FIRST_AIRED + ">=?" + " AND " + EpisodeColumns.FIRST_AIRED + "<=?",
                new String[]{String.valueOf(today.getMillis()), String.valueOf(today.plusDays(NUMBER_OF_DAYS_TO_DISPLAY).getMillis())},
                EpisodeColumns.FIRST_AIRED)
        {
            @Override
            protected List<CalendarDay> toObject(Cursor cursor)
            {
                Map<DateTime, List<CalendarEntry>> map = new TreeMap<>();
                if(cursor.moveToFirst())
                {
                    do
                    {
                        CalendarEntry calendarEntry = new CalendarEntry();
                        WEpisode wEpisode = WEpisode.unpack(cursor);
                        Episode episode = wEpisode.getTraktItem();
                        Show show = new Show();
                        show.ids = new ShowIds();
                        show.ids.trakt = Integer.valueOf(wEpisode.showId);
                        show.title = cursor.getString(cursor.getColumnIndex(CURSOR_SHOW_TITLE));

                        calendarEntry.airs_at = episode.first_aired;
                        calendarEntry.episode = episode;
                        calendarEntry.show = show;

                        DateTime date = calendarEntry.airs_at.withTime(0,0,0,0);
                        List<CalendarEntry> calendarEntries = map.get(date);
                        if(calendarEntries == null)
                            calendarEntries = new ArrayList<>();
                        calendarEntries.add(calendarEntry);
                        map.put(date, calendarEntries);
                    }
                    while(cursor.moveToNext());
                }

                return CalendarDay.toList(map);
            }
        });
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager()
    {
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_fanart_columns));
        // set span to 1 if it's a header so it takes all the width
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int position) {
                return ((RecyclerCalendarAdapter)getAdapter()).isHeader(position) ? manager.getSpanCount() : 1;
            }
        });

        return manager;
    }

    @Override
    public void onItemClick(View v, int position)
    {
        CalendarEntry calendarEntry = ((RecyclerCalendarAdapter)getAdapter()).getCalendarEntry(position);
        Episode episode = calendarEntry.episode;
        WEpisode wEpisode = new WEpisode(episode, String.valueOf(calendarEntry.show.ids.trakt));
        // TODO add showTitle to WEpisode ?
        EpisodeActivity.launch(getActivity(), wEpisode);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }

    public static class CalendarDay
    {
        public DateTime dateTime;
        public List<CalendarEntry> calendarEntries;

        private CalendarDay(DateTime dateTime, List<CalendarEntry> calendarEntries)
        {
            this.dateTime = dateTime;
            this.calendarEntries = calendarEntries;
        }

        public static List<CalendarDay> toList(Map<DateTime, List<CalendarEntry>> map)
        {
            List<CalendarDay> calendarDays = new ArrayList<>();
            for (Map.Entry<DateTime, List<CalendarEntry>> entry : map.entrySet())
                calendarDays.add(new CalendarDay(entry.getKey(), entry.getValue()));
            return calendarDays;
        }
    }
}
