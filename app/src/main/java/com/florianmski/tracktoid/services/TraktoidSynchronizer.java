package com.florianmski.tracktoid.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.florianmski.tracktoid.utils.CVHelper;
import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.utils.DbHelper;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidPrefs;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.SyncColumns;
import com.florianmski.tracktoid.data.database.utils.CVUtils;
import com.florianmski.tracktoid.errors.ErrorHandler;
import com.florianmski.tracktoid.errors.RetrofitComportment;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.uwetrottmann.trakt.v2.entities.BaseEpisode;
import com.uwetrottmann.trakt.v2.entities.BaseMovie;
import com.uwetrottmann.trakt.v2.entities.BaseRatedEntity;
import com.uwetrottmann.trakt.v2.entities.BaseSeason;
import com.uwetrottmann.trakt.v2.entities.BaseShow;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.LastActivities;
import com.uwetrottmann.trakt.v2.entities.LastActivity;
import com.uwetrottmann.trakt.v2.entities.LastActivityMore;
import com.uwetrottmann.trakt.v2.entities.Movie;
import com.uwetrottmann.trakt.v2.entities.RatedEpisode;
import com.uwetrottmann.trakt.v2.entities.RatedMovie;
import com.uwetrottmann.trakt.v2.entities.RatedSeason;
import com.uwetrottmann.trakt.v2.entities.RatedShow;
import com.uwetrottmann.trakt.v2.entities.Season;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.entities.WatchlistedEpisode;
import com.uwetrottmann.trakt.v2.entities.WatchlistedSeason;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.enums.Rating;
import com.uwetrottmann.trakt.v2.enums.RatingsFilter;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import timber.log.Timber;

public class TraktoidSynchronizer
{
    public final static int NOTIFICATION_ID = 42;

    private Context context;
    private DateTime startSyncTime;
    private DateTime lastLocalSyncTime;

    private ErrorHandler errorHandler;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public TraktoidSynchronizer(Context context)
    {
        this.context = context;
        errorHandler = new ErrorHandler(context)
                .putComportment(new RetrofitComportment())
                .reportToUser(false);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        cancelNotification();

        notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Trakt sync in progress...")
                .setColor(TraktoidTheme.DEFAULT.getColorDark(context))
                .setSmallIcon(R.drawable.ic_sync_white_24dp)
                .setOngoing(true)
                .setProgress(0, 0, true);
        updateNotification();
    }

    // have to do this because we do not have the trakt id for an episode in /sync/watched/shows and collected
    private static class EpisodeKey
    {
        private final static String SEPARATOR = ",";

        public String traktShow;
        public int season, number;
        public String key;

        public EpisodeKey(String key)
        {
            this.key = key;

            String[] data = key.split(SEPARATOR);
            this.traktShow = data[0];
            this.season = Integer.valueOf(data[1]);
            this.number = Integer.valueOf(data[2]);
        }

        public static String get(String traktShow, int season, int number)
        {
            return String.format("%s%s%d%s%d", traktShow, SEPARATOR, season, SEPARATOR, number);
        }
    }

    private <T> CVHelper getCVHelper(T key, Map<T, ContentValues> map)
    {
        ContentValues contentValues = map.get(key);
        if(contentValues == null)
            contentValues = new ContentValues();

        return new CVHelper(contentValues);
    }

    private static class BaseSync
    {
        public DateTime last_collected_at;
        public DateTime listed_at;
        public DateTime rated_at;
        public DateTime last_watched_at;

        public Integer plays;
        public Rating rating;

        public String key;

        protected BaseSync() {}

        public BaseSync (BaseRatedEntity baseRatedEntity)
        {
            this.rated_at = baseRatedEntity.rated_at;
            this.rating = baseRatedEntity.rating;
        }

        protected ContentValues pack()
        {
            return new ContentValues();
        }
    }

    private static class ShowSync extends BaseSync
    {
        public ShowSync(RatedShow ratedShow)
        {
            super(ratedShow);
            setKey(ratedShow.show);
        }

        public ShowSync(BaseShow baseShow)
        {
            setKey(baseShow.show);

            this.last_collected_at = baseShow.last_collected_at;
            this.last_watched_at = baseShow.last_watched_at;
            this.listed_at = baseShow.listed_at;
            this.plays = baseShow.plays;
        }

        private void setKey(Show show)
        {
            this.key = String.valueOf(show.ids.trakt);
        }

        protected static List<ShowSync> toListFromRated(List<RatedShow> ratedShows)
        {
            List<ShowSync> list = new ArrayList<>();
            for(RatedShow ratedShow : ratedShows)
                list.add(new ShowSync(ratedShow));
            return  list;
        }

        protected static List<ShowSync> toListFromBase(List<BaseShow> baseShows)
        {
            List<ShowSync> list = new ArrayList<>();
            for(BaseShow baseShow : baseShows)
                list.add(new ShowSync(baseShow));
            return list;
        }
    }

    private static class SeasonSync extends BaseSync
    {
        public SeasonSync(RatedSeason ratedSeason)
        {
            super(ratedSeason);
            setKey(ratedSeason.season);
        }

        public SeasonSync(WatchlistedSeason watchlistedSeason)
        {
            setKey(watchlistedSeason.season);

            this.last_collected_at = watchlistedSeason.listed_at;
        }

        private void setKey(Season season)
        {
            this.key = String.valueOf(season.ids.trakt);
        }

        protected static List<SeasonSync> toListFromRated(List<RatedSeason> ratedSeasons)
        {
            List<SeasonSync> list = new ArrayList<>();
            for(RatedSeason ratedSeason : ratedSeasons)
                list.add(new SeasonSync(ratedSeason));
            return  list;
        }

        protected static List<SeasonSync> toListFromWatchlisted(List<WatchlistedSeason> watchlistedSeasons)
        {
            List<SeasonSync> list = new ArrayList<>();
            for(WatchlistedSeason watchlistedSeason : watchlistedSeasons)
                list.add(new SeasonSync(watchlistedSeason));
            return list;
        }
    }

    private static class EpisodeSync extends BaseSync
    {
        public EpisodeSync(RatedEpisode ratedEpisode)
        {
            super(ratedEpisode);
            setKey(String.valueOf(ratedEpisode.show.ids.trakt), ratedEpisode.episode);
        }

        public EpisodeSync(WatchlistedEpisode watchlistedEpisode)
        {
            setKey(String.valueOf(watchlistedEpisode.show.ids.trakt), watchlistedEpisode.episode);

            this.listed_at = watchlistedEpisode.listed_at;
        }

        public EpisodeSync(String traktShow, int season, BaseEpisode baseEpisode)
        {
            setKey(traktShow, season, baseEpisode.number);

            this.last_collected_at = baseEpisode.collected_at;
            this.plays = baseEpisode.plays;
        }

        private void setKey(String traktShow, int season, int number)
        {
            this.key = EpisodeKey.get(traktShow, season, number);
        }

        private void setKey(String traktShow, Episode episode)
        {
            setKey(traktShow, episode.season, episode.number);
        }

        protected static List<EpisodeSync> toListFromRated(List<RatedEpisode> ratedEpisodes)
        {
            List<EpisodeSync> list = new ArrayList<>();
            for(RatedEpisode ratedEpisode : ratedEpisodes)
                list.add(new EpisodeSync(ratedEpisode));
            return  list;
        }

        protected static List<EpisodeSync> toListFromBase(BaseShow baseShow, int season, List<BaseEpisode> baseEpisodes)
        {
            List<EpisodeSync> list = new ArrayList<>();
            for(BaseEpisode baseEpisode : baseEpisodes)
            {
                EpisodeSync episodeSync = new EpisodeSync(String.valueOf(baseShow.show.ids.trakt), season, baseEpisode);
                // transmit the last_watched_at to episodes because they don't have one, bug in the API?
                episodeSync.last_watched_at = baseShow.last_watched_at;
                list.add(episodeSync);
            }
            return list;
        }

        protected static List<EpisodeSync> toListFromShows(List<BaseShow> baseShows)
        {
            List<EpisodeSync> list = new ArrayList<>();
            for(BaseShow baseShow : baseShows)
                for(BaseSeason baseSeason : baseShow.seasons)
                    list.addAll(toListFromBase(baseShow, baseSeason.number, baseSeason.episodes));
            return list;
        }

        protected static List<EpisodeSync> toListFromWatchlisted(List<WatchlistedEpisode> watchlistedEpisodes)
        {
            List<EpisodeSync> list = new ArrayList<>();
            for(WatchlistedEpisode watchlistedEpisode : watchlistedEpisodes)
                list.add(new EpisodeSync(watchlistedEpisode));
            return list;
        }
    }

    private static class MovieSync extends BaseSync
    {
        public Movie movie;

        public MovieSync(Movie movie)
        {
            this.movie = movie;
            setKey(movie);
        }

        public MovieSync(RatedMovie ratedMovie)
        {
            super(ratedMovie);

            this.movie = ratedMovie.movie;
            setKey(ratedMovie.movie);
        }

        public MovieSync(BaseMovie baseMovie)
        {
            this(baseMovie.movie);

            this.last_collected_at = baseMovie.collected_at;
            this.last_watched_at = baseMovie.last_watched_at;
            this.listed_at = baseMovie.listed_at;
            this.plays = baseMovie.plays;
        }

        private void setKey(Movie movie)
        {
            this.key = String.valueOf(movie.ids.trakt);
        }

        @Override
        protected ContentValues pack()
        {
            return CVUtils.packMovie(movie);
        }

        protected static List<MovieSync> toListFromRated(List<RatedMovie> ratedMovies)
        {
            List<MovieSync> list = new ArrayList<>();
            for(RatedMovie ratedMovie : ratedMovies)
                list.add(new MovieSync(ratedMovie));
            return list;
        }

        protected static List<MovieSync> toListFromBase(List<BaseMovie> baseMovies)
        {
            List<MovieSync> list = new ArrayList<>();
            for(BaseMovie baseMovie : baseMovies)
                list.add(new MovieSync(baseMovie));
            return list;
        }
    }

    private Func2<Integer, Throwable, Boolean> retryFunc = new Func2<Integer, Throwable, Boolean>()
    {
        @Override
        public Boolean call(Integer integer, Throwable throwable)
        {
            // retry 3 times if network or server issue
            boolean retry =
                    integer <= 3
                            && throwable instanceof RetrofitError
                            && (((RetrofitError) throwable).getKind() == RetrofitError.Kind.NETWORK
                            || ((RetrofitError) throwable).getKind() == RetrofitError.Kind.HTTP);

            Timber.d("retry n°%d, continue? %b", integer, retry);

            return retry;
        }
    };

    private boolean isSyncNeeded(DateTime lastServerSyncTime)
    {
        return lastServerSyncTime.isAfter(lastLocalSyncTime);
    }

    private <T extends BaseSync> Observable<Map<String, ContentValues>> getSyncObservable(DateTime lastServerSyncTime, TraktObservable<List<T>> traktObservable, final Func1<T, ContentValues> funcToContentValues, final Func1<T, DateTime> funcGetItemSyncTime)
    {
        // we don't need to run this because there's nothing new on the server
        // just return an empty map
        if(!isSyncNeeded(lastServerSyncTime))
            return Observable.just(Collections.<String, ContentValues>emptyMap());

        return Observable
                .create(traktObservable)
                .retry(retryFunc)
                .flatMap(new Func1<List<T>, Observable<T>>()
                {
                    @Override
                    public Observable<T> call(List<T> ts)
                    {
                        return Observable.from(ts);
                    }
                })
                        // sort the items from the most recent to the oldest
                .toSortedList(new Func2<T, T, Integer>()
                {
                    @Override
                    public Integer call(T t1, T t2)
                    {
                        DateTime syncTime1 = funcGetItemSyncTime.call(t1);
                        DateTime syncTime2 = funcGetItemSyncTime.call(t2);
                        return -syncTime1.compareTo(syncTime2);
                    }
                })
                .flatMap(new Func1<List<T>, Observable<T>>()
                {
                    @Override
                    public Observable<T> call(List<T> ts)
                    {
                        return Observable.from(ts);
                    }
                })
                        // take the items that haven't been sync
                .takeWhile(new Func1<T, Boolean>()
                {
                    @Override
                    public Boolean call(T t)
                    {
                        return funcGetItemSyncTime.call(t).isAfter(lastLocalSyncTime);
                    }
                })
                .collect(new ArrayList<T>(), new Action2<ArrayList<T>, T>()
                {
                    @Override
                    public void call(ArrayList<T> ts, T t)
                    {
                        ts.add(t);
                    }
                })
                .map(new Func1<List<T>, Map<String, ContentValues>>()
                {
                    @Override
                    public Map<String, ContentValues> call(List<T> entities)
                    {
                        return constructMap(entities, funcToContentValues);
                    }
                });
    }

    private <T extends BaseSync> Map<String, ContentValues> constructMap(List<T> entities, Func1<T, ContentValues> func)
    {
        Map<String, ContentValues> map = new HashMap<>();
        for (T entity : entities)
        {
            String key = entity.key;
            CVHelper cvHelper = getCVHelper(key, map)
                    .putAll(func.call(entity))
                    .putAll(entity.pack());

            map.put(key, cvHelper.get());
        }
        return map;
    }

    private <T extends BaseSync> Observable<Map<String, ContentValues>> getRatingsObservable(DateTime lastServerSyncTime, TraktObservable<List<T>> traktObservable)
    {
        return getSyncObservable(lastServerSyncTime, traktObservable, new Func1<T, ContentValues>()
        {
            @Override
            public ContentValues call(T ratedEntity)
            {
                return new CVHelper()
                        .put(SyncColumns.RATING, ratedEntity.rating.toString())
                        .put(SyncColumns.RATED_AT, ratedEntity.rated_at)
                        .get();
            }
        }, new Func1<T, DateTime>()
        {
            @Override
            public DateTime call(T ratedEntity)
            {
                return ratedEntity.rated_at;
            }
        });
    }

    private Observable<Map<String, ContentValues>> getRatingsShowObservable(DateTime lastServerSyncTime)
    {
        return getRatingsObservable(lastServerSyncTime, new TraktObservable<List<ShowSync>>()
        {
            @Override
            public List<ShowSync> fire() throws OAuthUnauthorizedException
            {
                return ShowSync.toListFromRated(TraktManager.getInstance().sync().ratingsShows(RatingsFilter.ALL, Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getRatingsSeasonObservable(DateTime lastServerSyncTime)
    {
        return getRatingsObservable(lastServerSyncTime, new TraktObservable<List<SeasonSync>>()
        {
            @Override
            public List<SeasonSync> fire() throws OAuthUnauthorizedException
            {
                return SeasonSync.toListFromRated(TraktManager.getInstance().sync().ratingsSeasons(RatingsFilter.ALL, Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getRatingsEpisodeObservable(DateTime lastServerSyncTime)
    {
        return getRatingsObservable(lastServerSyncTime, new TraktObservable<List<EpisodeSync>>()
        {
            @Override
            public List<EpisodeSync> fire() throws OAuthUnauthorizedException
            {
                return EpisodeSync.toListFromRated(TraktManager.getInstance().sync().ratingsEpisodes(RatingsFilter.ALL, Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getRatingsMovieObservable(DateTime lastServerSyncTime)
    {
        return getRatingsObservable(lastServerSyncTime, new TraktObservable<List<MovieSync>>()
        {
            @Override
            public List<MovieSync> fire() throws OAuthUnauthorizedException
            {
                return MovieSync.toListFromRated(TraktManager.getInstance().sync().ratingsMovies(RatingsFilter.ALL, Extended.FULLIMAGES));
            }
        });
    }

    private <T extends BaseSync> Observable<Map<String, ContentValues>> getWatchlistedObservable(DateTime lastServerSyncTime, TraktObservable<List<T>> traktObservable)
    {
        return getSyncObservable(lastServerSyncTime, traktObservable, new Func1<T, ContentValues>()
        {
            @Override
            public ContentValues call(T watchlistedEntity)
            {
                return new CVHelper()
                        .put(SyncColumns.WATCHLISTED, true)
                        .put(SyncColumns.WATCHLISTED_AT, watchlistedEntity.listed_at)
                        .get();
            }
        }, new Func1<T, DateTime>()
        {
            @Override
            public DateTime call(T ratedEntity)
            {
                return ratedEntity.listed_at;
            }
        });
    }

    private Observable<Map<String, ContentValues>> getWatchlistedShowObservable(DateTime lastServerSyncTime)
    {
        return getWatchlistedObservable(lastServerSyncTime, new TraktObservable<List<ShowSync>>()
        {
            @Override
            public List<ShowSync> fire() throws OAuthUnauthorizedException
            {
                return ShowSync.toListFromBase(TraktManager.getInstance().sync().watchlistShows(Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getWatchlistedSeasonObservable(DateTime lastServerSyncTime)
    {
        return getWatchlistedObservable(lastServerSyncTime, new TraktObservable<List<SeasonSync>>()
        {
            @Override
            public List<SeasonSync> fire() throws OAuthUnauthorizedException
            {
                return SeasonSync.toListFromWatchlisted(TraktManager.getInstance().sync().watchlistSeasons(Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getWatchlistedEpisodeObservable(DateTime lastServerSyncTime)
    {
        return getWatchlistedObservable(lastServerSyncTime, new TraktObservable<List<EpisodeSync>>()
        {
            @Override
            public List<EpisodeSync> fire() throws OAuthUnauthorizedException
            {
                return EpisodeSync.toListFromWatchlisted(TraktManager.getInstance().sync().watchlistEpisodes(Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getWatchlistedMovieObservable(DateTime lastServerSyncTime)
    {
        return getWatchlistedObservable(lastServerSyncTime, new TraktObservable<List<MovieSync>>()
        {
            @Override
            public List<MovieSync> fire() throws OAuthUnauthorizedException
            {
                return MovieSync.toListFromBase(TraktManager.getInstance().sync().watchlistMovies(Extended.FULLIMAGES));
            }
        });
    }

    private <T extends BaseSync> Observable<Map<String, ContentValues>> getWatchedObservable(DateTime lastServerSyncTime, TraktObservable<List<T>> traktObservable)
    {
        return getSyncObservable(lastServerSyncTime, traktObservable, new Func1<T, ContentValues>()
        {
            @Override
            public ContentValues call(T watchedEntity)
            {
                return new CVHelper()
                        .put(SyncColumns.WATCHED, true)
                        .put(SyncColumns.PLAYS, watchedEntity.plays)
                        .put(SyncColumns.LAST_WATCHED_AT, watchedEntity.last_watched_at)
                        .get();
            }
        }, new Func1<T, DateTime>()
        {
            @Override
            public DateTime call(T watchedEntity)
            {
                return watchedEntity.last_watched_at;
            }
        });
    }

    private Observable<Map<String, ContentValues>> getWatchedEpisodeObservable(DateTime lastServerSyncTime)
    {
        return getWatchedObservable(lastServerSyncTime, new TraktObservable<List<EpisodeSync>>()
        {
            @Override
            public List<EpisodeSync> fire() throws OAuthUnauthorizedException
            {
                return EpisodeSync.toListFromShows(TraktManager.getInstance().sync().watchedShows(Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getWatchedMovieObservable(DateTime lastServerSyncTime)
    {
        return getWatchedObservable(lastServerSyncTime, new TraktObservable<List<MovieSync>>()
        {
            @Override
            public List<MovieSync> fire() throws OAuthUnauthorizedException
            {
                return MovieSync.toListFromBase(TraktManager.getInstance().sync().watchedMovies(Extended.FULLIMAGES));
            }
        });
    }

    private <T extends BaseSync> Observable<Map<String, ContentValues>> getCollectedObservable(DateTime lastServerSyncTime, TraktObservable<List<T>> traktObservable)
    {
        return getSyncObservable(lastServerSyncTime, traktObservable, new Func1<T, ContentValues>()
        {
            @Override
            public ContentValues call(T watchedEntity)
            {
                return new CVHelper()
                        .put(SyncColumns.COLLECTED, true)
                        .put(SyncColumns.COLLECTED_AT, watchedEntity.last_collected_at)
                        .get();
            }
        }, new Func1<T, DateTime>()
        {
            @Override
            public DateTime call(T ratedEntity)
            {
                return ratedEntity.last_collected_at;
            }
        });
    }

    private Observable<Map<String, ContentValues>> getCollectedEpisodeObservable(DateTime lastServerSyncTime)
    {
        return getCollectedObservable(lastServerSyncTime, new TraktObservable<List<EpisodeSync>>()
        {
            @Override
            public List<EpisodeSync> fire() throws OAuthUnauthorizedException
            {
                return EpisodeSync.toListFromShows(TraktManager.getInstance().sync().collectionShows(Extended.DEFAULT_MIN));
            }
        });
    }

    private Observable<Map<String, ContentValues>> getCollectedMovieObservable(DateTime lastServerSyncTime)
    {
        return getCollectedObservable(lastServerSyncTime, new TraktObservable<List<MovieSync>>()
        {
            @Override
            public List<MovieSync> fire() throws OAuthUnauthorizedException
            {
                return MovieSync.toListFromBase(TraktManager.getInstance().sync().collectionMovies(Extended.FULLIMAGES));
            }
        });
    }

    private Observable<Map.Entry<String, ContentValues>> getMovieObservable(LastActivityMore moviesActivity)
    {
        return Observable.zip(
                getCollectedMovieObservable(moviesActivity.collected_at),
                getRatingsMovieObservable(moviesActivity.rated_at),
                getWatchedMovieObservable(moviesActivity.watched_at),
                getWatchlistedMovieObservable(moviesActivity.watchlisted_at),
                new Func4<Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>>()
                {
                    @Override
                    public Map<String, ContentValues> call(Map<String, ContentValues> mapCollected, Map<String, ContentValues> mapRatings, Map<String, ContentValues> mapWatched, Map<String, ContentValues> mapWatchlisted)
                    {
                        return mergeMaps(mapWatched, mapCollected, mapRatings, mapWatchlisted);
                    }
                })
                .flatMap(new Func1<Map<String, ContentValues>, Observable<Map.Entry<String, ContentValues>>>()
                {
                    @Override
                    public Observable<Map.Entry<String, ContentValues>> call(Map<String, ContentValues> map)
                    {
                        Map<String, Long> localMovieMap = DbHelper.getLastUpdatedMovieMap(context);

                        List<ContentValues> moviesToInsert = new ArrayList<>();
                        for (Map.Entry<String, ContentValues> movieEntry : map.entrySet())
                        {
                            String traktMovie = movieEntry.getKey();
                            ContentValues contentValues = movieEntry.getValue();

                            // if we don't have it locally it means we have to insert it
                            if (!localMovieMap.containsKey(traktMovie))
                                moviesToInsert.add(contentValues);
                                // if we have it, update db
                            else
                                DbHelper.updateMovie(context, contentValues, traktMovie);
                        }

                        // bulkInsert movies
                        // bulkInsert will notify the movie CONTENT_URI
                        DbHelper.bulkInsert(context, moviesToInsert, ProviderSchematic.Movies.CONTENT_URI);

                        return Observable.from(map.entrySet());
                    }
                });
    }

    private Observable<Void> getShowObservable(LastActivities lastActivities)
    {
        return Observable.zip(
                getMergedShowObservable(lastActivities.shows),
                getMergedSeasonObservable(lastActivities.seasons),
                getMergedEpisodeObservable(lastActivities.episodes),
                new Func3<Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>, Void>()
                {
                    @Override
                    public Void call(Map<String, ContentValues> mapShows, Map<String, ContentValues> mapSeasons, Map<String, ContentValues> mapEpisodes)
                    {
                        Map<String, Long> localShowMap = DbHelper.getLastUpdatedShowMap(context);

                        // check if we have episodes that belong to a show we don't have already scheduled for dl
                        for(Map.Entry<String, ContentValues> episodeEntry : mapEpisodes.entrySet())
                        {
                            String showTrakt = new EpisodeKey(episodeEntry.getKey()).traktShow;
                            if (!localShowMap.containsKey(showTrakt) && !mapShows.containsKey(showTrakt))
                                mapShows.put(showTrakt, new ContentValues());
                        }

                        Iterator<Map.Entry<String, ContentValues>> entries = mapShows.entrySet().iterator();
                        while (entries.hasNext())
                        {
                            Map.Entry<String, ContentValues> entry = entries.next();
                            String traktShow = entry.getKey();
                            ContentValues contentValues = entry.getValue();
                            if (!localShowMap.containsKey(traktShow))
                            {
                                List<ContentValues> seasonToInsert = new ArrayList<>();
                                List<ContentValues> episodeToInsert = new ArrayList<>();

                                // download show, seasons and episodes
                                // add the contentvalues to the respective showToInsert, seasonToInsert and episodeToInsert
                                // check in the user data maps if we have additional sync infos for these items
                                // if so add them to our contentvalues and remove them from the map

                                Show show = TraktManager.getInstance().shows().summary(traktShow, Extended.FULLIMAGES);
                                List<Season> seasons = TraktManager.getInstance().seasons().summary(traktShow, Extended.FULLIMAGES);
                                for (Season season : seasons)
                                {
                                    String traktSeason = String.valueOf(season.ids.trakt);

                                    List<Episode> episodes = TraktManager.getInstance().seasons().season(traktShow, season.number, Extended.FULLIMAGES);
                                    for (Episode episode : episodes)
                                    {
                                        String episodeKey = EpisodeKey.get(traktShow, episode.season, episode.number);
                                        // add episode contentvalues and add user data if we have some
                                        ContentValues episodeContentValues = CVUtils.packEpisode(episode, traktSeason, traktShow);
                                        ContentValues episodeUserDataContentValues = mapEpisodes.get(episodeKey);
                                        if (episodeUserDataContentValues != null)
                                        {
                                            episodeContentValues.putAll(episodeUserDataContentValues);
                                            mapEpisodes.remove(episodeKey);
                                        }
                                        episodeToInsert.add(episodeContentValues);
                                    }

                                    // add season contentvalues and add user data if we have some
                                    ContentValues seasonContentValues = CVUtils.packSeason(season, traktShow);
                                    ContentValues seasonUserDataContentValues = mapSeasons.get(traktSeason);
                                    if (seasonUserDataContentValues != null)
                                    {
                                        seasonContentValues.putAll(seasonUserDataContentValues);
                                        mapSeasons.remove(traktSeason);
                                    }
                                    seasonToInsert.add(seasonContentValues);
                                }

                                // add show contentvalues and add user data
                                ContentValues showContentValues = CVUtils.packShow(show);
                                showContentValues.putAll(contentValues);
                                entries.remove();

                                // insert show and bulkInsert seasons, episodes
                                DbHelper.bulkInsert(context, episodeToInsert, ProviderSchematic.Episodes.CONTENT_URI);
                                DbHelper.bulkInsert(context, seasonToInsert, ProviderSchematic.Seasons.CONTENT_URI);
                                DbHelper.insert(context, ProviderSchematic.Shows.CONTENT_URI, showContentValues);
                            }
                            // if we have it, update db
                            else
                                DbHelper.updateShow(context, contentValues, traktShow);
                        }

                        // we still need to update what's left (seasons and episodes)
                        for (Map.Entry<String, ContentValues> seasonEntry : mapSeasons.entrySet())
                            DbHelper.updateSeason(context, seasonEntry.getValue(), seasonEntry.getKey());

                        for (Map.Entry<String, ContentValues> episodeEntry : mapEpisodes.entrySet())
                        {
                            EpisodeKey episodeKey = new EpisodeKey(episodeEntry.getKey());
                            // have to update like this because we don't have the traktId of the episode in the
                            // sync/collection/shows and sync/watched/shows methods
                            DbHelper.updateEpisode(context, episodeEntry.getValue(), episodeKey.traktShow, episodeKey.season, episodeKey.number);
                        }

                        // TODO what are we suppose to return ?
                        return null;
                    }
                });
    }

    private Observable<Map<String, ContentValues>> getMergedShowObservable(LastActivity showActivities)
    {
        return Observable.zip(
                getRatingsShowObservable(showActivities.rated_at),
                getWatchlistedShowObservable(showActivities.watchlisted_at),
                new Func2<Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>>()
                {
                    @Override
                    public Map<String, ContentValues> call(Map<String, ContentValues> mapRatings, Map<String, ContentValues> mapWatchlisted)
                    {
                        return mergeMaps(mapRatings, mapWatchlisted);
                    }
                });
    }

    private Observable<Map<String, ContentValues>> getMergedSeasonObservable(LastActivity seasonActivities)
    {
        return Observable.zip(
                getRatingsSeasonObservable(seasonActivities.rated_at),
                getWatchlistedSeasonObservable(seasonActivities.watchlisted_at),
                new Func2<Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>>()
                {
                    @Override
                    public Map<String, ContentValues> call(Map<String, ContentValues> mapRatings, Map<String, ContentValues> mapWatchlisted)
                    {
                        return mergeMaps(mapRatings, mapWatchlisted);
                    }
                });
    }

    private Observable<Map<String, ContentValues>> getMergedEpisodeObservable(LastActivityMore episodeActivities)
    {
        return Observable.zip(
                getCollectedEpisodeObservable(episodeActivities.collected_at),
                getRatingsEpisodeObservable(episodeActivities.rated_at),
                getWatchedEpisodeObservable(episodeActivities.watched_at),
                getWatchlistedEpisodeObservable(episodeActivities.watchlisted_at),
                new Func4<Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>, Map<String, ContentValues>>()
                {
                    @Override
                    public Map<String, ContentValues> call(Map<String, ContentValues> mapCollected, Map<String, ContentValues> mapRatings, Map<String, ContentValues> mapWatched, Map<String, ContentValues> mapWatchlisted)
                    {
                        return mergeMaps(mapWatched, mapCollected, mapRatings, mapWatchlisted);
                    }
                });
    }

    @SafeVarargs
    private final <T> Map<T, ContentValues> mergeMaps(Map<T, ContentValues>... maps)
    {
        Map<T, ContentValues> map = new HashMap<>();

        if(maps == null || maps.length == 0)
            return map;

        map.putAll(maps[0]);

        for(int i = 1; i < maps.length; i++)
        {
            for(Map.Entry<T, ContentValues> entry : maps[i].entrySet())
            {
                T key = entry.getKey();
                ContentValues contentValues = map.get(key);
                if(contentValues == null)
                    contentValues = new ContentValues();
                contentValues.putAll(entry.getValue());

                map.put(key, contentValues);
            }
        }

        return map;
    }

    public void sync()
    {
        // update existing shows and movies
        Map<String, Long> localShowMap = DbHelper.getLastUpdatedShowMap(context);
        if (localShowMap.size() > 0)
        {
            // TODO
            //            List<TvShow> shows = TraktManager.getInstance().shows().updates();
        }

        Map<String, Long> localMovieMap = DbHelper.getLastUpdatedMovieMap(context);
        if (localMovieMap.size() > 0)
        {
            // TODO
            //                        List<TvShow> shows = TraktManager.getInstance().movies().updates();
        }

        Observable
                .create(new TraktObservable<LastActivities>()
                {
                    @Override
                    public LastActivities fire() throws OAuthUnauthorizedException
                    {
                        return TraktManager.getInstance().sync().lastActivities();
                    }
                })
                .retry(retryFunc)
                .flatMap(new Func1<LastActivities, Observable<?>>()
                {
                    @Override
                    public Observable<?> call(LastActivities lastActivities)
                    {
                        if(!isSyncNeeded(lastActivities.all))
                            return Observable.just(Collections.emptyMap());

                        return Observable.merge(
                                getMovieObservable(lastActivities.movies),
                                getShowObservable(lastActivities)
                        );
                    }
                })
                .doOnSubscribe(new Action0()
                {
                    @Override
                    public void call()
                    {
                        startSyncTime = DateHelper.now();
                        lastLocalSyncTime = TraktoidPrefs.INSTANCE.getLastSyncTime();

                        Timber.d("startSyncTime : %s", startSyncTime);
                        Timber.d("lastLocalSyncTime : %s", lastLocalSyncTime);
                    }
                })
                .doOnCompleted(new Action0()
                {
                    @Override
                    public void call()
                    {
                        TraktoidPrefs.INSTANCE.putLastSyncTime(startSyncTime);
                    }
                })
                .subscribe(new Observer<Object>()
                {
                    @Override
                    public void onCompleted()
                    {
                        Timber.d("Sync completed");
                        cancelNotification();
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        errorHandler.handle(e, "Error during sync");
                        Intent intent = new Intent(context, TraktoidService.class);
                        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
                        notificationBuilder = new NotificationCompat.Builder(context)
                                .setContentTitle("Traktoid")
                                .setContentText("Error during synchronization.")
                                .setSmallIcon(R.drawable.ic_sync_problem_white_24dp)
                                .setColor(TraktoidTheme.DEFAULT.getColorDark(context))
                                .addAction(new NotificationCompat.Action(R.drawable.ic_refresh_grey600_24dp, "Retry", pendingIntent));
                        updateNotification();
                    }

                    @Override
                    public void onNext(Object o)
                    {

                    }
                });
    }

    private void updateNotification()
    {
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void cancelNotification()
    {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
