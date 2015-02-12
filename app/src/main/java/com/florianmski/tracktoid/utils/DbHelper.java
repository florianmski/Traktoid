package com.florianmski.tracktoid.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.MovieColumns;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.florianmski.tracktoid.data.database.utils.CVUtils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.Movie;
import com.uwetrottmann.trakt.v2.entities.Season;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.entities.SyncEpisode;
import com.uwetrottmann.trakt.v2.entities.SyncMovie;
import com.uwetrottmann.trakt.v2.entities.SyncShow;
import com.uwetrottmann.trakt.v2.enums.Extended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper
{
    public static boolean isShowInDb(Context context, String showId)
    {
        return isSomethingInDb(context, ProviderSchematic.Shows.CONTENT_URI, ShowColumns.ID_TRAKT, showId);
    }

    public static boolean isMovieInDb(Context context, Movie movie)
    {
        return isSomethingInDb(context, ProviderSchematic.Movies.CONTENT_URI, MovieColumns.ID_TRAKT, String.valueOf(movie.ids.trakt));
    }

    private static boolean isSomethingInDb(Context context, Uri uri, String idColumn, String traktId)
    {
        boolean exists;
        Cursor c =
                context.getContentResolver().query(
                        uri,
                        new String[]{},
                        idColumn + "=?",
                        new String[]{traktId},
                        null);

        exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public static Map<String, Long> getLastUpdatedMap(Context context, Uri uri, String idColumn, String lastUpdatedColumn)
    {
        Cursor c = context.getContentResolver().query(
                uri,
                new String[]{idColumn, lastUpdatedColumn},
                null,
                null,
                null
        );

        Map<String, Long> map = new HashMap<>();

        while(c.moveToNext())
            map.put(c.getString(0), c.getLong(1));

        c.close();

        return map;
    }

    public static Map<String, Long> getLastUpdatedShowMap(Context context)
    {
        return getLastUpdatedMap(context, ProviderSchematic.Shows.CONTENT_URI, ShowColumns.ID_TRAKT, ShowColumns.UPDATED_AT);
    }

    public static Map<String, Long> getLastUpdatedMovieMap(Context context)
    {
        return getLastUpdatedMap(context, ProviderSchematic.Movies.CONTENT_URI, MovieColumns.ID_TRAKT, MovieColumns.UPDATED_AT);
    }

    public static void downloadAndInsertShow(Context context, String showId)
    {
        Show show = TraktManager.getInstance().shows().summary(showId, Extended.FULLIMAGES);
        downloadAndInsertShowContent(context, show);
    }

    public static void downloadAndInsertShowContent(Context context, Show show)
    {
        List<Season> seasons = TraktManager.getInstance().seasons().summary(String.valueOf(show.ids.trakt), Extended.FULLIMAGES);
        List<ContentValues> episodeToInsert = new ArrayList<>();
        List<ContentValues> seasonToInsert = new ArrayList<>();
        for(Season season : seasons)
        {
            seasonToInsert.add(CVUtils.packSeason(season, String.valueOf(show.ids.trakt)));
            List<Episode> episodes = TraktManager.getInstance().seasons().season(String.valueOf(show.ids.trakt), season.number, Extended.FULLIMAGES);
            for(Episode episode : episodes)
                episodeToInsert.add(CVUtils.packEpisode(episode, String.valueOf(season.ids.trakt), String.valueOf(show.ids.trakt)));
        }

        DbHelper.bulkInsert(context, episodeToInsert, ProviderSchematic.Episodes.CONTENT_URI);
        DbHelper.bulkInsert(context, seasonToInsert, ProviderSchematic.Seasons.CONTENT_URI);
        DbHelper.insertShow(context, show);
    }

    public static void insert(Context context, Uri uri, ContentValues contentValues)
    {
        context.getContentResolver().insert(uri, contentValues);
    }

    public static void insertMovie(Context context, Movie movie)
    {
        insert(context, ProviderSchematic.Movies.CONTENT_URI, CVUtils.packMovie(movie));
    }

    public static void insertShow(Context context, Show show)
    {
        insert(context, ProviderSchematic.Shows.CONTENT_URI, CVUtils.packShow(show));
    }

    public static void insertEpisode(Context context, Episode episode, String seasonId, String showId)
    {
        insert(context, ProviderSchematic.Movies.CONTENT_URI, CVUtils.packEpisode(episode, seasonId, showId));
    }

    public static boolean update(Context context, Uri uri, ContentValues cv, String where, String... selectionArgs)
    {
        return context.getContentResolver().update(uri, cv, where, selectionArgs) > 0;
    }

    public static boolean update(Context context, Uri uri, ContentValues cv)
    {
        return update(context, uri, cv, null);
    }

    public static boolean updateMovie(Context context, ContentValues cv, String trakt)
    {
        return update(context, ProviderSchematic.Movies.withId(trakt), cv);
    }

    public static boolean updateMovies(Context context, ContentValues cv, List<SyncMovie> syncMovies)
    {
        if(syncMovies.isEmpty())
            return false;

        if(syncMovies.size() == 1)
            return updateMovie(context, cv, String.valueOf(syncMovies.get(0).ids.trakt));

        List<String> ids = new ArrayList<>();
        for(SyncMovie syncMovie : syncMovies)
            ids.add(String.valueOf(syncMovie.ids.trakt));

        return updateItems(context, ProviderSchematic.Movies.CONTENT_URI, cv, MovieColumns.ID_TRAKT, ids);
    }

    public static boolean updateShow(Context context, ContentValues cv, String trakt)
    {
        return update(context, ProviderSchematic.Shows.withId(trakt), cv);
    }

    public static boolean updateShows(Context context, ContentValues cv, List<SyncShow> syncShows)
    {
        if(syncShows.isEmpty())
            return false;

        if(syncShows.size() == 1)
            return updateShow(context, cv, String.valueOf(syncShows.get(0).ids.trakt));

        List<String> ids = new ArrayList<>();
        for(SyncShow syncShow : syncShows)
            ids.add(String.valueOf(syncShow.ids.trakt));

        return updateItems(context, ProviderSchematic.Shows.CONTENT_URI, cv, ShowColumns.ID_TRAKT, ids);
    }

    public static boolean updateSeason(Context context, ContentValues cv, String trakt)
    {
        return update(context, ProviderSchematic.Seasons.withId(trakt), cv);
    }

    public static boolean updateEpisode(Context context, ContentValues cv, String trakt)
    {
        return update(context, ProviderSchematic.Episodes.withId(trakt), cv);
    }

    public static boolean updateEpisode(Context context, ContentValues cv, String traktShow, int season, int number)
    {
        return update(context, ProviderSchematic.Episodes.fromShow(traktShow), cv,
                EpisodeColumns.SEASON + "=?" + " AND " + EpisodeColumns.NUMBER + "=?",
                String.valueOf(season), String.valueOf(number));
    }

    public static boolean updateEpisodes(Context context, ContentValues cv, List<SyncEpisode> syncEpisodes)
    {
        if(syncEpisodes.isEmpty())
            return false;

        if(syncEpisodes.size() == 1)
            return updateEpisode(context, cv, String.valueOf(syncEpisodes.get(0).ids.trakt));

        List<String> ids = new ArrayList<>();
        for(SyncEpisode syncEpisode : syncEpisodes)
            ids.add(String.valueOf(syncEpisode.ids.trakt));

        return updateItems(context, ProviderSchematic.Episodes.CONTENT_URI, cv, EpisodeColumns.ID_TRAKT, ids);
    }

    public static boolean updateItems(Context context, Uri uri, ContentValues cv, String idColumn, List<String> ids)
    {
        String where = "";
        List<String> whereArgs = new ArrayList<>();

        for(String id : ids)
        {
            where += ",?";
            whereArgs.add(id);
        }

        return update(
                context,
                uri,
                cv,
                idColumn + " IN (" + where.substring(1) + ")",
                whereArgs.toArray(new String[whereArgs.size()]));
    }

    public static void bulkInsert(Context context, List<ContentValues> contentValuesList, Uri uri)
    {
        ContentValues[] cvs = new ContentValues[contentValuesList.size()];
        int i = 0;
        for(ContentValues contentValues : contentValuesList)
            cvs[i++] = contentValues;

        context.getContentResolver().bulkInsert(uri, cvs);
    }

}
