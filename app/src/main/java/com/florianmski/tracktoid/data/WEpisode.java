package com.florianmski.tracktoid.data;

import android.database.Cursor;

import com.florianmski.tracktoid.utils.CursorHelper;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.EpisodeIds;
import com.uwetrottmann.trakt.v2.entities.Images;

import java.util.ArrayList;
import java.util.List;

public class WEpisode extends TraktoidItem<Episode, EpisodeIds>
{
    public String showId;

    public WEpisode(Episode episode, String showId)
    {
        super(episode, episode.ids);
        this.showId = showId;
    }

    public static WEpisode unpack(Cursor cursor)
    {
        Episode episode = new Episode();
        episode.ids = new EpisodeIds();
        episode.images = new Images();

        CursorHelper ch = new CursorHelper(cursor);
        unpackEntity(ch, episode);
        unpackIds(ch, episode.ids);
        unpackImagesScreenshot(ch, episode.images);

        episode.first_aired     = ch.getDate(EpisodeColumns.FIRST_AIRED);
        episode.number          = ch.getInt(EpisodeColumns.NUMBER);
        episode.number_abs      = ch.getInt(EpisodeColumns.NUMBER_ABS);
        episode.overview        = ch.getString(EpisodeColumns.OVERVIEW);
        episode.season          = ch.getInt(EpisodeColumns.SEASON);

        episode.rating          = ch.getDouble(EpisodeColumns.PUBLIC_RATING);
        episode.votes           = ch.getInt(EpisodeColumns.VOTES);

        episode.ids.tvdb        = ch.getInt(EpisodeColumns.ID_TVDB);
        episode.ids.tvrage      = ch.getInt(EpisodeColumns.ID_TVRAGE);

        String showId = ch.getString(EpisodeColumns.SHOW_ID);
        WEpisode wEpisode = new WEpisode(episode, showId);

        wEpisode.syncInfos = new SyncInfos();
        wEpisode.syncInfos.collectedAt       = ch.getDate(EpisodeColumns.COLLECTED_AT);
        wEpisode.syncInfos.ratedAt           = ch.getDate(EpisodeColumns.RATED_AT);
        wEpisode.syncInfos.lastWatchedAt     = ch.getDate(EpisodeColumns.LAST_WATCHED_AT);
        wEpisode.syncInfos.watchlistedAt     = ch.getDate(EpisodeColumns.WATCHLISTED_AT);

        wEpisode.syncInfos.collected         = ch.getBoolean(EpisodeColumns.COLLECTED);
        wEpisode.syncInfos.rating            = ch.getRating(EpisodeColumns.RATING);
        wEpisode.syncInfos.watched           = ch.getBoolean(EpisodeColumns.WATCHED);
        wEpisode.syncInfos.watchlisted       = ch.getBoolean(EpisodeColumns.WATCHLISTED);
        wEpisode.syncInfos.plays             = ch.getInt(EpisodeColumns.PLAYS);

        return wEpisode;
    }

    public static List<WEpisode> unpackList(Cursor cursor)
    {
        List<WEpisode> episodes = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do
                episodes.add(unpack(cursor));
            while(cursor.moveToNext());
        }
        return episodes;
    }

    @Override
    public TraktBase getTraktBase()
    {
        return TraktBase.fromEpisode(this);
    }
}
