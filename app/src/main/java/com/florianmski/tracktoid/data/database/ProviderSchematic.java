package com.florianmski.tracktoid.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.MovieColumns;
import com.florianmski.tracktoid.data.database.columns.SeasonColumns;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import timber.log.Timber;

@ContentProvider(authority = ProviderSchematic.AUTHORITY, database = DatabaseSchematic.class, name = "GeneratedProvider", packageName = "com.florianmski.tracktoid.data.provider")
public final class ProviderSchematic
{
    private static Subject<Uri, Uri> subject = new SerializedSubject<>(PublishSubject.<Uri>create());

    private ProviderSchematic() {}

    public static void init(final Context context)
    {
        ConnectableObservable<Uri> notifyUrisEmitter = subject.publish();

        notifyUrisEmitter.publish(new Func1<Observable<Uri>, Observable<List<Uri>>>()
        {
            @Override
            public Observable<List<Uri>> call(Observable<Uri> stream)
            {
                return stream.buffer(stream.debounce(1, TimeUnit.SECONDS));
            }
        }).flatMap(new Func1<List<Uri>, Observable<Uri>>()
        {
            @Override
            public Observable<Uri> call(List<Uri> uris)
            {
                Timber.d("TIME TO NOTIFY!");
                return Observable.from(uris).distinct();
            }
        }).subscribe(new Action1<Uri>()
        {
            @Override
            public void call(Uri uri)
            {
                Timber.d("notifying : " + uri.toString().replace("content://com.florianmski.tracktoid.data.provider.TraktoidProvider/", ""));
                context.getContentResolver().notifyChange(uri, null);
            }
        });

        notifyUrisEmitter.connect();
    }

    public static final String AUTHORITY = "com.florianmski.tracktoid.data.provider.TraktoidProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path
    {
        String SHOWS = "shows";
        String FROM_SHOW = "fromShow";

        String SEASONS = "seasons";
        String FROM_SEASON = "fromSeason";

        String EPISODES = "episodes";

        String MOVIES = "movies";
    }

    private static Uri buildUri(String... paths)
    {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths)
            builder.appendPath(path);

        return builder.build();
    }

    static Uri getBaseUri(Uri uri)
    {
        return BASE_CONTENT_URI.buildUpon().appendPath(uri.getPathSegments().get(0)).build();
    }

    @NotifyDelete()
    public static Uri[] defaultNotifyDelete(Uri uri)
    {
        sendUrisToSubject(getBaseUri(uri));
        return new Uri[]{};
    }

    @TableEndpoint(table = DatabaseSchematic.SHOWS)
    public static class Shows
    {
        public static String[] PROJECTION = new String[]{
                ShowColumns.ID,
                ShowColumns.AIR_DAY,
                ShowColumns.AIR_TIME,
                ShowColumns.AIR_TIMEZONE,
                ShowColumns.CERTIFICATION,
                ShowColumns.COUNTRY,
                ShowColumns.EPISODES_AIRED,
                ShowColumns.FIRST_AIRED,
                ShowColumns.GENRES,
                ShowColumns.HOMEPAGE,
                ShowColumns.LANGUAGE,
                ShowColumns.NETWORK,
                ShowColumns.OVERVIEW,
                ShowColumns.RUNTIME,
                ShowColumns.STATUS,
                ShowColumns.TITLE,
                ShowColumns.TRAILER,
                ShowColumns.UPDATED_AT,
                ShowColumns.YEAR,

                ShowColumns.EPISODES_COLLECTED,
                ShowColumns.EPISODES_WATCHED,
                ShowColumns.LAST_COLLECTED_AT,
                ShowColumns.LAST_WATCHED_AT,
                ShowColumns.PLAYS,
                ShowColumns.RATED_AT,
                ShowColumns.RATING,
                ShowColumns.WATCHLISTED,
                ShowColumns.WATCHLISTED_AT,

                ShowColumns.PUBLIC_RATING,
                ShowColumns.VOTES,

                ShowColumns.ID_IMDB,
                ShowColumns.ID_SLUG,
                ShowColumns.ID_TRAKT,
                ShowColumns.ID_TMDB,
                ShowColumns.ID_TVDB,
                ShowColumns.ID_TVRAGE,

                ShowColumns.IMAGE_FANART_FULL,
                ShowColumns.IMAGE_FANART_MEDIUM,
                ShowColumns.IMAGE_FANART_THUMB,
                ShowColumns.IMAGE_POSTER_FULL,
                ShowColumns.IMAGE_POSTER_MEDIUM,
                ShowColumns.IMAGE_POSTER_THUMB,
                ShowColumns.IMAGE_BANNER,
                ShowColumns.IMAGE_CLEARART,
                ShowColumns.IMAGE_LOGO,
                ShowColumns.IMAGE_THUMB
        };

        @MapColumns
        public static Map<String, String> mapColumns()
        {
            Map<String, String> map = new HashMap<>();

            map.put(ShowColumns.EPISODES_AIRED, EPISODES_AIRED_COUNT);
            map.put(ShowColumns.EPISODES_COLLECTED, EPISODES_COLLECTED_COUNT);
            map.put(ShowColumns.EPISODES_WATCHED, EPISODES_WATCHED_COUNT);
            map.put(ShowColumns.LAST_COLLECTED_AT, LAST_COLLECTED_AT);
            map.put(ShowColumns.LAST_WATCHED_AT, LAST_WATCHED_AT);
            map.put(ShowColumns.PLAYS, PLAYS);

            return map;
        }

        private static final String EPISODES_AIRED_COUNT = episodesCount(
                EpisodeColumns.FIRST_AIRED + ">0",
                EpisodeColumns.FIRST_AIRED + "<=" + System.currentTimeMillis()
        );

        // ensure we count the right episodes (user could have mark as watched/collected episodes in the future)
        private static final String EPISODES_COLLECTED_COUNT = episodesCount(
                EpisodeColumns.COLLECTED + "=1",
                EpisodeColumns.FIRST_AIRED + ">0",
                EpisodeColumns.FIRST_AIRED + "<=" + System.currentTimeMillis()
        );

        private static final String EPISODES_WATCHED_COUNT = episodesCount(
                EpisodeColumns.WATCHED + "=1",
                EpisodeColumns.FIRST_AIRED + ">0",
                EpisodeColumns.FIRST_AIRED + "<=" + System.currentTimeMillis()
        );

        private static String episodesCount(String... conditions)
        {
            return createQuery("COUNT(*)", false, conditions);
        }

        private static final String LAST_COLLECTED_AT = createQuery("MAX(" + EpisodeColumns.COLLECTED_AT + ")", true);

        private static final String LAST_WATCHED_AT = createQuery("MAX(" + EpisodeColumns.LAST_WATCHED_AT + ")", true);

        private static final String PLAYS = createQuery("SUM(" + EpisodeColumns.PLAYS + ")", true);

        private static String createQuery(String select, boolean includeSpecials, String... conditions)
        {
            String sqlQuery = "(SELECT " + select + " FROM "
                    + DatabaseSchematic.EPISODES
                    + " WHERE "
                    + DatabaseSchematic.EPISODES + "." + EpisodeColumns.SHOW_ID
                    + "="
                    + DatabaseSchematic.SHOWS + "." + ShowColumns.ID_TRAKT;

            if (!includeSpecials)
                sqlQuery += " AND " + EpisodeColumns.SEASON + "!=0";

            for (String condition : conditions)
                sqlQuery += " AND " + condition;

            return sqlQuery + ")";
        }

        @ContentUri(
                path = Path.SHOWS,
                type = "vnd.android.cursor.dir/show",
                defaultSort = ShowColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.SHOWS);

        @InexactContentUri(
                path = Path.SHOWS + "/#",
                name = "SHOW_ID",
                type = "vnd.android.cursor.item/show",
                whereColumn = ShowColumns.ID_TRAKT,
                pathSegment = 1)
        public static Uri withId(String showId)
        {
            return buildUri(Path.SHOWS, showId);
        }

        @NotifyInsert(paths = Path.SHOWS)
        public static Uri[] notifyInsert(ContentValues values)
        {
            final String showId = values.getAsString(ShowColumns.ID_TRAKT);
            sendUrisToSubject(CONTENT_URI, withId(showId));
            return new Uri[]{};
        }

        @NotifyUpdate(paths = Path.SHOWS + "/#")
        public static Uri[] notifyUpdate(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        @NotifyDelete(paths = Path.SHOWS + "/#")
        public static Uri[] notifyDelete(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        private static Uri[] getUrisToNotify(Context context, Uri uri, String where, String[] whereArgs)
        {
            // Notify every show concerned by the where clause + the CONTENT_URI
            Set<Uri> uris = new HashSet<>();

            Cursor c = context.getContentResolver().query(uri, new String[]{ShowColumns.ID_TRAKT}, where, whereArgs, null);

            while (c.moveToNext())
            {
                final String id = c.getString(0);
                uris.add(withId(id));
            }
            c.close();

            uris.add(CONTENT_URI);

            sendUrisToSubject(uris);
            return new Uri[]{};
        }
    }

    @TableEndpoint(table = DatabaseSchematic.SEASONS)
    public static class Seasons
    {
        public static String[] PROJECTION = new String[]{
                SeasonColumns.ID,
                SeasonColumns.EPISODES_AIRED,
                SeasonColumns.NUMBER,
                SeasonColumns.OVERVIEW,
                SeasonColumns.SHOW_ID,

                SeasonColumns.EPISODES_COLLECTED,
                SeasonColumns.EPISODES_WATCHED,
                SeasonColumns.LAST_COLLECTED_AT,
                SeasonColumns.LAST_WATCHED_AT,
                SeasonColumns.PLAYS,
                SeasonColumns.RATED_AT,
                SeasonColumns.RATING,
                SeasonColumns.WATCHLISTED,
                SeasonColumns.WATCHLISTED_AT,

                SeasonColumns.PUBLIC_RATING,
                SeasonColumns.VOTES,

                SeasonColumns.ID_TRAKT,
                SeasonColumns.ID_TMDB,
                SeasonColumns.ID_TVDB,
                SeasonColumns.ID_TVRAGE,

                SeasonColumns.IMAGE_POSTER_FULL,
                SeasonColumns.IMAGE_POSTER_MEDIUM,
                SeasonColumns.IMAGE_POSTER_THUMB,
                SeasonColumns.IMAGE_THUMB
        };

        @MapColumns
        public static Map<String, String> mapColumns()
        {
            Map<String, String> map = new HashMap<>();

            map.put(SeasonColumns.EPISODES_AIRED, EPISODES_AIRED_COUNT);
            map.put(SeasonColumns.EPISODES_COLLECTED, EPISODES_COLLECTED_COUNT);
            map.put(SeasonColumns.EPISODES_WATCHED, EPISODES_WATCHED_COUNT);
            map.put(SeasonColumns.LAST_COLLECTED_AT, LAST_COLLECTED_AT);
            map.put(SeasonColumns.LAST_WATCHED_AT, LAST_WATCHED_AT);
            map.put(SeasonColumns.PLAYS, PLAYS);

            return map;
        }

        private static final String EPISODES_AIRED_COUNT = episodesCount(
                EpisodeColumns.FIRST_AIRED + ">0",
                EpisodeColumns.FIRST_AIRED + "<=" + System.currentTimeMillis()
        );

        private static final String EPISODES_COLLECTED_COUNT = episodesCount(
                EpisodeColumns.COLLECTED + "=1",
                EpisodeColumns.FIRST_AIRED + ">0",
                EpisodeColumns.FIRST_AIRED + "<=" + System.currentTimeMillis()
        );

        private static final String EPISODES_WATCHED_COUNT = episodesCount(
                EpisodeColumns.WATCHED + "=1",
                EpisodeColumns.FIRST_AIRED + ">0",
                EpisodeColumns.FIRST_AIRED + "<=" + System.currentTimeMillis()
        );

        private static String episodesCount(String... conditions)
        {
            return createQuery("COUNT(*)", conditions);
        }

        private static final String LAST_COLLECTED_AT = createQuery("MAX(" + EpisodeColumns.COLLECTED_AT + ")");

        private static final String LAST_WATCHED_AT = createQuery("MAX(" + EpisodeColumns.LAST_WATCHED_AT + ")");

        private static final String PLAYS = createQuery("SUM(" + EpisodeColumns.PLAYS + ")");

        private static String createQuery(String select, String... conditions)
        {
            String sqlQuery = "(SELECT " + select + " FROM "
                    + DatabaseSchematic.EPISODES
                    + " WHERE "
                    + DatabaseSchematic.EPISODES + "." + EpisodeColumns.SEASON_ID
                    + "="
                    + DatabaseSchematic.SEASONS + "." + SeasonColumns.ID_TRAKT;

            for (String condition : conditions)
                sqlQuery += " AND " + condition;

            return sqlQuery + ")";
        }

        @ContentUri(
                path = Path.SEASONS,
                type = "vnd.android.cursor.dir/season")
        public static final Uri CONTENT_URI = buildUri(Path.SEASONS);

        @InexactContentUri(
                name = "SEASON_ID",
                path = Path.SEASONS + "/#",
                type = "vnd.android.cursor.item/season",
                whereColumn = SeasonColumns.ID_TRAKT,
                pathSegment = 1)
        public static Uri withId(String seasonId)
        {
            return buildUri(Path.SEASONS, seasonId);
        }

        @InexactContentUri(
                name = "SEASONS_FROM_SHOW",
                path = Path.SEASONS + "/" + Path.FROM_SHOW + "/#",
                type = "vnd.android.cursor.dir/season",
                whereColumn = SeasonColumns.SHOW_ID,
                pathSegment = 2)
        public static Uri fromShow(String showId)
        {
            return buildUri(Path.SEASONS, Path.FROM_SHOW, showId);
        }

        @NotifyInsert(paths = Path.SEASONS)
        public static Uri[] notifyInsert(ContentValues values)
        {
            final String showId = values.getAsString(SeasonColumns.SHOW_ID);
            sendUrisToSubject(
                    CONTENT_URI, fromShow(showId), // notify seasons
                    Shows.CONTENT_URI, Shows.withId(showId) // notify shows
            );
            return new Uri[]{};
        }

        @NotifyUpdate(paths = {
                Path.SEASONS + "/#",
                Path.SEASONS + "/" + Path.FROM_SHOW + "/#"})
        public static Uri[] notifyUpdate(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        @NotifyDelete(paths = {
                Path.SEASONS + "/#",
                Path.SEASONS + "/" + Path.FROM_SHOW + "/#"})
        public static Uri[] notifyDelete(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        private static Uri[] getUrisToNotify(Context context, Uri uri, String where, String[] whereArgs)
        {
            Set<Uri> uris = new HashSet<>();

            Cursor c = context.getContentResolver().query(uri, new String[]{
                    SeasonColumns.ID_TRAKT, SeasonColumns.SHOW_ID,
            }, where, whereArgs, null);

            while (c.moveToNext())
            {
                final String id = c.getString(0);
                final String showId = c.getString(1);

                uris.add(withId(id));
                uris.add(fromShow(showId));
                uris.add(Episodes.fromShow(showId));
                uris.add(Episodes.fromSeason(id));
            }
            c.close();

            uris.add(CONTENT_URI);
            uris.add(Seasons.CONTENT_URI);
            uris.add(Shows.CONTENT_URI);

            sendUrisToSubject(uris);
            return new Uri[]{};
        }
    }

    @TableEndpoint(table = DatabaseSchematic.EPISODES)
    public static class Episodes
    {
        public static String[] PROJECTION = new String[]{
                EpisodeColumns.ID,
                EpisodeColumns.FIRST_AIRED,
                EpisodeColumns.NUMBER,
                EpisodeColumns.NUMBER_ABS,
                EpisodeColumns.OVERVIEW,
                EpisodeColumns.SEASON,
                EpisodeColumns.SEASON_ID,
                EpisodeColumns.SHOW_ID,
                EpisodeColumns.TITLE,
                EpisodeColumns.UPDATED_AT,

                EpisodeColumns.COLLECTED,
                EpisodeColumns.COLLECTED_AT,
                EpisodeColumns.LAST_WATCHED_AT,
                EpisodeColumns.PLAYS,
                EpisodeColumns.RATED_AT,
                EpisodeColumns.RATING,
                EpisodeColumns.WATCHED,
                EpisodeColumns.WATCHLISTED,
                EpisodeColumns.WATCHLISTED_AT,

                EpisodeColumns.PUBLIC_RATING,
                EpisodeColumns.VOTES,

                EpisodeColumns.ID_IMDB,
                EpisodeColumns.ID_TRAKT,
                EpisodeColumns.ID_TMDB,
                EpisodeColumns.ID_TVDB,
                EpisodeColumns.ID_TVRAGE,

                EpisodeColumns.IMAGE_SCREENSHOT_FULL,
                EpisodeColumns.IMAGE_SCREENSHOT_MEDIUM,
                EpisodeColumns.IMAGE_SCREENSHOT_THUMB
        };

        @ContentUri(
                path = Path.EPISODES,
                type = "vnd.android.cursor.dir/episode")
        public static final Uri CONTENT_URI = buildUri(Path.EPISODES);

        @InexactContentUri(
                name = "EPISODE_ID",
                path = Path.EPISODES + "/#",
                type = "vnd.android.cursor.item/episode",
                whereColumn = EpisodeColumns.ID_TRAKT,
                pathSegment = 1)
        public static Uri withId(String episodeId)
        {
            return buildUri(Path.EPISODES, episodeId);
        }

        @InexactContentUri(
                name = "EPISODES_FROM_SEASON",
                path = Path.EPISODES + "/" + Path.FROM_SEASON + "/#",
                type = "vnd.android.cursor.dir/episode",
                whereColumn = EpisodeColumns.SEASON_ID,
                pathSegment = 2)
        public static Uri fromSeason(String seasonId)
        {
            return buildUri(Path.EPISODES, Path.FROM_SEASON, seasonId);
        }

        @InexactContentUri(
                name = "EPISODES_FROM_SHOW",
                path = Path.EPISODES + "/" + Path.FROM_SHOW + "/#",
                type = "vnd.android.cursor.dir/episode",
                whereColumn = EpisodeColumns.SHOW_ID,
                pathSegment = 2)
        public static Uri fromShow(String showId)
        {
            return buildUri(Path.EPISODES, Path.FROM_SHOW, showId);
        }

        @NotifyInsert(paths = Path.EPISODES)
        public static Uri[] notifyInsert(ContentValues values)
        {
            final String showId = values.getAsString(EpisodeColumns.SHOW_ID);
            final String seasonId = values.getAsString(EpisodeColumns.SEASON_ID);

            sendUrisToSubject(
                    CONTENT_URI, fromShow(showId), fromSeason(seasonId), // notify episodes
                    Seasons.CONTENT_URI, Seasons.withId(seasonId), Seasons.fromShow(showId), // notify seasons
                    Shows.CONTENT_URI, Shows.withId(showId) // notify shows
            );
            return new Uri[]{};
        }

        @NotifyUpdate(paths = {
                Path.EPISODES + "/#",
                Path.EPISODES + "/" + Path.FROM_SEASON + "/#",
                Path.EPISODES + "/" + Path.FROM_SHOW + "/#"})
        public static Uri[] notifyUpdate(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        @NotifyDelete(paths = {
                Path.EPISODES + "/#",
                Path.EPISODES + "/" + Path.FROM_SEASON + "/#",
                Path.EPISODES + "/" + Path.FROM_SHOW + "/#"})
        public static Uri[] notifyDelete(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        private static Uri[] getUrisToNotify(Context context, Uri uri, String where, String[] whereArgs)
        {
            Set<Uri> uris = new HashSet<>();

            Cursor c = context.getContentResolver().query(uri, new String[]{
                    EpisodeColumns.ID_TRAKT, EpisodeColumns.SHOW_ID, EpisodeColumns.SEASON_ID,
            }, where, whereArgs, null);

            while (c.moveToNext())
            {
                final String id = c.getString(0);
                final String showId = c.getString(1);
                final String seasonId = c.getString(2);

                uris.add(withId(id));
                uris.add(fromShow(showId));
                uris.add(fromSeason(seasonId));
                uris.add(Seasons.withId(seasonId));
                uris.add(Seasons.fromShow(showId));
                uris.add(Shows.withId(showId));
            }
            c.close();

            uris.add(CONTENT_URI);
            uris.add(Seasons.CONTENT_URI);
            uris.add(Shows.CONTENT_URI);

            sendUrisToSubject(uris);
            return new Uri[]{};
        }
    }

    @TableEndpoint(table = DatabaseSchematic.MOVIES)
    public static class Movies
    {
        public static String[] PROJECTION = new String[]{
                MovieColumns.ID,
                MovieColumns.CERTIFICATION,
                MovieColumns.GENRES,
                MovieColumns.HOMEPAGE,
                MovieColumns.LANGUAGE,
                MovieColumns.OVERVIEW,
                MovieColumns.RELEASED,
                MovieColumns.RUNTIME,
                MovieColumns.TAGLINE,
                MovieColumns.TITLE,
                MovieColumns.TRAILER,
                MovieColumns.UPDATED_AT,
                MovieColumns.YEAR,

                MovieColumns.COLLECTED,
                MovieColumns.COLLECTED_AT,
                MovieColumns.LAST_WATCHED_AT,
                MovieColumns.PLAYS,
                MovieColumns.RATED_AT,
                MovieColumns.RATING,
                MovieColumns.WATCHED,
                MovieColumns.WATCHLISTED,
                MovieColumns.WATCHLISTED_AT,

                MovieColumns.PUBLIC_RATING,
                MovieColumns.VOTES,

                MovieColumns.ID_IMDB,
                MovieColumns.ID_SLUG,
                MovieColumns.ID_TRAKT,
                MovieColumns.ID_TMDB,

                MovieColumns.IMAGE_FANART_FULL,
                MovieColumns.IMAGE_FANART_MEDIUM,
                MovieColumns.IMAGE_FANART_THUMB,
                MovieColumns.IMAGE_POSTER_FULL,
                MovieColumns.IMAGE_POSTER_MEDIUM,
                MovieColumns.IMAGE_POSTER_THUMB,
                MovieColumns.IMAGE_BANNER,
                MovieColumns.IMAGE_CLEARART,
                MovieColumns.IMAGE_LOGO,
                MovieColumns.IMAGE_THUMB
        };

        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                path = Path.MOVIES + "/#",
                name = "MOVIE_ID",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns.ID_TRAKT,
                pathSegment = 1)
        public static Uri withId(String traktId)
        {
            return buildUri(Path.MOVIES, traktId);
        }

        @NotifyInsert(paths = Path.MOVIES)
        public static Uri[] notifyInsert(ContentValues values)
        {
            final String movieId = values.getAsString(MovieColumns.ID_TRAKT);
            sendUrisToSubject(CONTENT_URI, withId(movieId));
            return new Uri[]{};
        }

        @NotifyUpdate(paths = Path.MOVIES + "/#")
        public static Uri[] notifyUpdate(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        @NotifyDelete(paths = Path.MOVIES + "/#")
        public static Uri[] notifyDelete(Context context, Uri uri, String where, String[] whereArgs)
        {
            return getUrisToNotify(context, uri, where, whereArgs);
        }

        private static Uri[] getUrisToNotify(Context context, Uri uri, String where, String[] whereArgs)
        {
            // Notify every movie concerned by the where clause + the CONTENT_URI
            Set<Uri> uris = new HashSet<>();

            Cursor c = context.getContentResolver().query(uri, new String[]{MovieColumns.ID_TRAKT}, where, whereArgs, null);

            while (c.moveToNext())
            {
                final String id = c.getString(0);
                uris.add(withId(id));
            }
            c.close();

            uris.add(CONTENT_URI);
            sendUrisToSubject(uris);
            return new Uri[]{};
        }
    }

    private static void sendUrisToSubject(Uri... uris)
    {
        for(Uri uri : uris)
            subject.onNext(uri);
    }

    private static void sendUrisToSubject(Collection<Uri> uris)
    {
        sendUrisToSubject(uris.toArray(new Uri[uris.size()]));
    }
}
