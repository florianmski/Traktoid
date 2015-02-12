package com.florianmski.tracktoid.adapters.lists;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.utils.Utils;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.data.TraktBase;
import com.florianmski.tracktoid.image.ImagesTest;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.ui.fragments.calendar.CalendarFragment;
import com.florianmski.tracktoid.ui.widgets.FlagsView;
import com.florianmski.tracktoid.ui.widgets.Placeholder;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;
import com.uwetrottmann.trakt.v2.entities.CalendarEntry;
import com.uwetrottmann.trakt.v2.entities.Episode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class RecyclerCalendarAdapter extends RecyclerAdapter<CalendarFragment.CalendarDay, RecyclerAdapter.ViewHolder>
{
    private final static int ITEM_DATE = 0, ITEM_EPISODE = 1;

    private SparseArray<DateTime> dates = new SparseArray<>();
    private SparseArray<CalendarEntry> calendarEntries = new SparseArray<>();

    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("EEEE d MMMM");
    private TraktoidTheme theme = TraktoidTheme.SHOW;
    private Placeholder placeholder;

    public RecyclerCalendarAdapter(Context context, List<CalendarFragment.CalendarDay> data, OnItemClickListener listener)
    {
        super(context, data, listener);
        setupDataStructures();
        placeholder = new Placeholder(context, theme);
    }

    private void setupDataStructures()
    {
        clearDataStructures();

        int i = 0;
        for(CalendarFragment.CalendarDay calendarDay : data)
        {
            dates.put(i++, calendarDay.dateTime);
            for(CalendarEntry calendarEntry : calendarDay.calendarEntries)
                calendarEntries.put(i++, calendarEntry);
        }
    }

    private void clearDataStructures()
    {
        dates.clear();
        calendarEntries.clear();
    }

    @Override
    public void refresh(List<CalendarFragment.CalendarDay> data)
    {
        super.refresh(data);
        setupDataStructures();
    }

    @Override
    public void reset()
    {
        clearDataStructures();
        super.reset();
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerAdapter.ViewHolder vh;

        switch(viewType)
        {
            case ITEM_DATE:
            default:
                View v = LayoutInflater.from(context).inflate(R.layout.list_item_calendar_date, parent, false);
                vh = new DateViewHolder(v);
                break;
            case ITEM_EPISODE:
                v = LayoutInflater.from(context).inflate(R.layout.list_item_trakt_item, parent, false);
                vh = new EpisodeViewHolder(v, listener);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position)
    {
        if(getItemViewType(position) == ITEM_DATE)
        {
            DateViewHolder castedHolder = (DateViewHolder)holder;
            DateTime dateTime = dates.get(position);
            castedHolder.tvDate.setText(dateTime.toString(dtf));
        }
        else
        {
            EpisodeViewHolder castedHolder = (EpisodeViewHolder)holder;
            CalendarEntry calendarEntry = calendarEntries.get(position);
            Episode episode = calendarEntry.episode;
            TraktBase traktBase = TraktBase.fromEpisode(episode);

            ImagesTest.load(context, castedHolder.iv, calendarEntry.episode.images).placeholder(placeholder.getDrawable()).into(castedHolder.iv);
            castedHolder.fv.init(new FlagsView.Flags
                    .Builder(traktBase)
                    .displayTitle(true)
                    .title(calendarEntry.show.title)
                    .subtitle(Utils.getSeasonEpisodeString(episode.season, episode.number))
                    .theme(theme)
                    .build());
        }
    }

    public boolean isHeader(int position)
    {
        return dates.get(position) != null;
    }

    @Override
    public int getItemViewType(int position)
    {
        return isHeader(position) ? ITEM_DATE : ITEM_EPISODE;
    }

    @Override
    public int getItemCount()
    {
        return dates.size() + calendarEntries.size();
    }

    public CalendarEntry getCalendarEntry(int position)
    {
        return calendarEntries.get(position);
    }

    public static class DateViewHolder extends RecyclerAdapter.ViewHolder
    {
        private TextView tvDate;

        public DateViewHolder(View itemView)
        {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.textViewDate);
        }
    }

    public static class EpisodeViewHolder extends RecyclerAdapter.ViewHolder
    {
        private FlagsView fv;
        private TraktImageView iv;

        public EpisodeViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView, listener);

            fv = (FlagsView) itemView.findViewById(R.id.flagsView);
            iv = (TraktImageView) itemView.findViewById(R.id.imageView);
            iv.setType(Type.SCREENSHOT);
        }
    }
}
