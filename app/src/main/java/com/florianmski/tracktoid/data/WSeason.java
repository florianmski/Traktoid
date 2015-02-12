package com.florianmski.tracktoid.data;

import android.database.Cursor;

import com.florianmski.tracktoid.utils.CursorHelper;
import com.florianmski.tracktoid.data.database.columns.SeasonColumns;
import com.florianmski.tracktoid.data.database.utils.CVUtils;
import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.Season;
import com.uwetrottmann.trakt.v2.entities.SeasonIds;

import java.util.ArrayList;
import java.util.List;

public class WSeason extends TraktoidItem<CVUtils.SeasonCV.SeasonEntity, CVUtils.SeasonCV.SeasonEntityIds>
{
    public int episodesAired;
    public int episodeCollected;
    public int episodesWatched;

    public WSeason(CVUtils.SeasonCV.SeasonEntity traktObject)
    {
        super(traktObject, new CVUtils.SeasonCV.SeasonEntityIds(traktObject.season.ids));
    }

    public static WSeason unpack(Cursor cursor)
    {
        CursorHelper ch = new CursorHelper(cursor);

        Season season = new Season();
        season.number = ch.getInt(SeasonColumns.NUMBER);
        season.overview = ch.getString(SeasonColumns.OVERVIEW);
        season.ids = new SeasonIds();
        season.images = new Images();

        CVUtils.SeasonCV.SeasonEntity seasonEntity = new CVUtils.SeasonCV.SeasonEntity(season);
//        unpackEntity(ch, seasonEntity);
//        unpackIds(ch, new CVUtils.SeasonCV.SeasonEntityIds(season.ids));
        unpackImagesPoster(ch, season.images);
        unpackImagesThumb(ch, season.images);

        season.rating         = ch.getDouble(SeasonColumns.PUBLIC_RATING);
                season.votes          = ch.getInt(SeasonColumns.VOTES);

        season.ids.tmdb    = ch.getInt(SeasonColumns.ID_TMDB);
        season.ids.trakt   = ch.getInt(SeasonColumns.ID_TRAKT);
        season.ids.tvdb    = ch.getInt(SeasonColumns.ID_TVDB);
        season.ids.tvrage  = ch.getInt(SeasonColumns.ID_TVRAGE);

        WSeason wSeason = new WSeason(seasonEntity);
        wSeason.syncInfos = new SyncInfos();
        wSeason.episodesAired = ch.getInt(SeasonColumns.EPISODES_AIRED);
        wSeason.episodeCollected = ch.getInt(SeasonColumns.EPISODES_COLLECTED);
        wSeason.episodesWatched = ch.getInt(SeasonColumns.EPISODES_WATCHED);

        wSeason.syncInfos.collectedAt       = ch.getDate(SeasonColumns.LAST_COLLECTED_AT);
        wSeason.syncInfos.ratedAt           = ch.getDate(SeasonColumns.RATED_AT);
        wSeason.syncInfos.watchlistedAt     = ch.getDate(SeasonColumns.WATCHLISTED_AT);
        wSeason.syncInfos.lastWatchedAt     = ch.getDate(SeasonColumns.LAST_WATCHED_AT);
        wSeason.syncInfos.collected         = (wSeason.episodeCollected >= wSeason.episodesAired) && wSeason.episodesAired > 0;
        wSeason.syncInfos.rating            = ch.getRating(SeasonColumns.RATING);
        wSeason.syncInfos.watchlisted       = ch.getBoolean(SeasonColumns.WATCHLISTED);
        wSeason.syncInfos.watched           = (wSeason.episodesWatched >= wSeason.episodesAired) && wSeason.episodesAired > 0;
        wSeason.syncInfos.plays             = ch.getInt(SeasonColumns.PLAYS);

        return wSeason;
    }

    public static List<WSeason> unpackList(Cursor cursor)
    {
        List<WSeason> seasons = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do
                seasons.add(unpack(cursor));
            while(cursor.moveToNext());
        }
        return seasons;
    }

    @Override
    public TraktBase getTraktBase()
    {
        return TraktBase.fromSeason(this);
    }
}
