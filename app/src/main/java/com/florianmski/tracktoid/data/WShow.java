package com.florianmski.tracktoid.data;

import android.database.Cursor;

import com.florianmski.tracktoid.utils.CursorHelper;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.uwetrottmann.trakt.v2.entities.Airs;
import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.entities.ShowIds;
import com.uwetrottmann.trakt.v2.enums.Status;

import java.util.ArrayList;
import java.util.List;

public class WShow extends TraktoidItem<Show, ShowIds>
{
    public int episodesAired;
    public int episodeCollected;
    public int episodesWatched;

    public WShow(Show show)
    {
        super(show, show.ids);
    }

    public static WShow unpack(Cursor cursor)
    {
        Show show = new Show();
        show.ids = new ShowIds();
        show.images = new Images();

        CursorHelper ch = new CursorHelper(cursor);
        unpackEntity(ch, show);
        unpackIds(ch, show.ids);
        unpackImagesFanart(ch, show.images);
        unpackImagesPoster(ch, show.images);
        unpackImagesBanner(ch, show.images);
        unpackImagesClearart(ch, show.images);
        unpackImagesLogo(ch, show.images);
        unpackImagesThumb(ch, show.images);

        Airs airs = new Airs();
        airs.day            = ch.getString(ShowColumns.AIR_DAY);
        airs.time           = ch.getString(ShowColumns.AIR_TIME);
        airs.timezone       = ch.getString(ShowColumns.AIR_TIMEZONE);
        show.airs = airs;

        show.certification  = ch.getString(ShowColumns.CERTIFICATION);
        show.country        = ch.getString(ShowColumns.COUNTRY);
        show.first_aired    = ch.getDate(ShowColumns.FIRST_AIRED);
        show.genres         = ch.getStringList(ShowColumns.GENRES);
        show.homepage       = ch.getString(ShowColumns.HOMEPAGE);
        show.language       = ch.getString(ShowColumns.LANGUAGE);
        show.network        = ch.getString(ShowColumns.NETWORK);
        show.overview       = ch.getString(ShowColumns.OVERVIEW);
        show.runtime        = ch.getInt(ShowColumns.RUNTIME);

        String status = ch.getString(ShowColumns.RUNTIME);
        show.status         = status != null ? Status.fromValue(status) : null;

        show.trailer        = ch.getString(ShowColumns.TRAILER);
        show.year           = ch.getInt(ShowColumns.YEAR);

        show.rating         = ch.getDouble(ShowColumns.PUBLIC_RATING);
        show.votes          = ch.getInt(ShowColumns.VOTES);

        show.ids.slug       = ch.getString(ShowColumns.ID_SLUG);
        show.ids.tvdb       = ch.getInt(ShowColumns.ID_TVDB);
        show.ids.tvrage     = ch.getInt(ShowColumns.ID_TVRAGE);

        WShow wShow = new WShow(show);
        wShow.episodesAired     = ch.getInt(ShowColumns.EPISODES_AIRED);
        wShow.episodeCollected  = ch.getInt(ShowColumns.EPISODES_COLLECTED);
        wShow.episodesWatched   = ch.getInt(ShowColumns.EPISODES_WATCHED);

        wShow.syncInfos = new SyncInfos();
        wShow.syncInfos.collectedAt       = ch.getDate(ShowColumns.LAST_COLLECTED_AT);
        wShow.syncInfos.ratedAt           = ch.getDate(ShowColumns.RATED_AT);
        wShow.syncInfos.watchlistedAt     = ch.getDate(ShowColumns.WATCHLISTED_AT);
        wShow.syncInfos.lastWatchedAt     = ch.getDate(ShowColumns.LAST_WATCHED_AT);
        wShow.syncInfos.collected         = (wShow.episodeCollected >= wShow.episodesAired) && wShow.episodesAired > 0;
        wShow.syncInfos.rating            = ch.getRating(ShowColumns.RATING);
        wShow.syncInfos.watchlisted       = ch.getBoolean(ShowColumns.WATCHLISTED);
        wShow.syncInfos.watched           = (wShow.episodesWatched >= wShow.episodesAired) && wShow.episodesAired > 0;
        wShow.syncInfos.plays             = ch.getInt(ShowColumns.PLAYS);

        return wShow;
    }

    public static List<WShow> unpackList(Cursor cursor)
    {
        List<WShow> shows = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do
                shows.add(unpack(cursor));
            while(cursor.moveToNext());
        }
        return shows;
    }

    public static List<WShow> toList(List<Show> items)
    {
        List<WShow> traktObjects = new ArrayList<WShow>();
        for(Show item : items)
            traktObjects.add(new WShow(item));
        return traktObjects;
    }

    @Override
    public TraktBase getTraktBase()
    {
        return TraktBase.fromShow(this);
    }
}
