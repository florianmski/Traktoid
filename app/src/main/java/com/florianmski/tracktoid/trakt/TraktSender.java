package com.florianmski.tracktoid.trakt;

import android.content.ContentValues;
import android.content.Context;

import com.florianmski.tracktoid.utils.CVHelper;
import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.utils.DbHelper;
import com.florianmski.tracktoid.TraktoidPrefs;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.SyncColumns;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.EpisodeIds;
import com.uwetrottmann.trakt.v2.entities.Movie;
import com.uwetrottmann.trakt.v2.entities.MovieIds;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.entities.ShowIds;
import com.uwetrottmann.trakt.v2.entities.SyncEpisode;
import com.uwetrottmann.trakt.v2.entities.SyncErrors;
import com.uwetrottmann.trakt.v2.entities.SyncItems;
import com.uwetrottmann.trakt.v2.entities.SyncMovie;
import com.uwetrottmann.trakt.v2.entities.SyncResponse;
import com.uwetrottmann.trakt.v2.entities.SyncSeason;
import com.uwetrottmann.trakt.v2.entities.SyncShow;
import com.uwetrottmann.trakt.v2.entities.SyncStats;
import com.uwetrottmann.trakt.v2.enums.Rating;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class TraktSender
{
    // TODO should support seasons as well

    private SyncItems2 syncItems = new SyncItems2();

    private List<SyncShow> syncShows;
    private List<SyncSeason> syncSeasons;
    private List<SyncEpisode> syncEpisodes;
    private List<SyncMovie> syncMovies;

    private Context context;

    private TraktSender(Context context, BaseBuilder builder)
    {
        this.context = context.getApplicationContext();

        this.syncShows = builder.syncShows;
        this.syncSeasons = builder.syncSeasons;
        this.syncEpisodes = builder.syncEpisodes;
        this.syncMovies = builder.syncMovies;
    }

    public Observable<SyncResponse> history(final boolean add)
    {
        return getObservable(new TraktSenderObservable(add)
        {
            @Override
            protected SyncResponse addItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().addItemsToWatchedHistory(syncItems);
            }

            @Override
            protected SyncResponse deleteItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().deleteItemsFromWatchedHistory(syncItems);
            }
        }, new Action0()
        {
            @Override
            public void call()
            {
                ContentValues contentValues = new CVHelper()
                        .put(SyncColumns.WATCHED, add)
                        .put(SyncColumns.LAST_WATCHED_AT, DateHelper.now())
                        .put(SyncColumns.WATCHLISTED, false)
                        .putNull(SyncColumns.WATCHLISTED_AT)
                        .get();

                DbHelper.updateMovies(context, contentValues, syncMovies);

                // if seen change, all the episodes change too
                for (SyncShow syncShow : syncShows)
                    updateEpisodesTillNow(syncShow.ids.trakt, contentValues, add);

                DbHelper.updateEpisodes(context, contentValues, syncEpisodes);
            }
        });
    }

    public Observable<SyncResponse> collection(final boolean add)
    {
        return getObservable(new TraktSenderObservable(add)
        {
            @Override
            protected SyncResponse addItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().addItemsToCollection(syncItems);
            }

            @Override
            protected SyncResponse deleteItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().deleteItemsFromCollection(syncItems);
            }
        }, new Action0()
        {
            @Override
            public void call()
            {
                ContentValues contentValues = new CVHelper()
                        .put(SyncColumns.COLLECTED, add)
                        .put(SyncColumns.COLLECTED_AT, DateHelper.now())
                        .get();

                DbHelper.updateMovies(context, contentValues, syncMovies);

                // if collection change, all the episodes change too
                for (SyncShow syncShow : syncShows)
                    updateEpisodesTillNow(syncShow.ids.trakt, contentValues, add);

                DbHelper.updateEpisodes(context, contentValues, syncEpisodes);
            }
        });
    }

    private void updateEpisodesTillNow(int traktShow, ContentValues contentValues, boolean tillNow)
    {
        // if we add a show, we want to collect/watch until today's date (and not include the specials)
        // if we remove a show, we want to remove everything even if user as marked sthg in the future
        if(tillNow)
        {
            DbHelper.update(context,
                    ProviderSchematic.Episodes.fromShow(String.valueOf(traktShow)),
                    contentValues,
                    EpisodeColumns.FIRST_AIRED + "<=? AND " + EpisodeColumns.SEASON + "!=?",
                    String.valueOf(System.currentTimeMillis()), String.valueOf(0));
        }
        else
        {
            DbHelper.update(context,
                    ProviderSchematic.Episodes.fromShow(String.valueOf(traktShow)),
                    contentValues);
        }
    }

    public Observable<SyncResponse> watchlist(final boolean add)
    {
        return getObservable(new TraktSenderObservable(add)
        {
            @Override
            protected SyncResponse addItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().addItemsToWatchlist(syncItems);
            }

            @Override
            protected SyncResponse deleteItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().deleteItemsFromWatchlist(syncItems);
            }
        }, new Action0()
        {
            @Override
            public void call()
            {
                ContentValues contentValues = new CVHelper()
                        .put(SyncColumns.WATCHLISTED, add)
                        .put(SyncColumns.WATCHLISTED_AT, DateHelper.now())
                        .get();

                DbHelper.updateMovies(context, contentValues, syncMovies);
                DbHelper.updateShows(context, contentValues, syncShows);
                DbHelper.updateEpisodes(context, contentValues, syncEpisodes);
            }
        });
    }

    public Observable<SyncResponse> rating(final boolean rate)
    {
        return getObservable(new TraktSenderObservable(rate)
        {
            @Override
            protected SyncResponse addItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().addRatings(syncItems);
            }

            @Override
            protected SyncResponse deleteItems() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().sync().deleteRatings(syncItems);
            }
        }, new Action0()
        {
            @Override
            public void call()
            {
                for(SyncMovie syncMovie : syncMovies)
                    DbHelper.updateMovie(context, getContentValues(syncMovie.rating, syncMovie.rated_at), String.valueOf(syncMovie.ids.trakt));

                for(SyncShow syncShow : syncShows)
                    DbHelper.updateShow(context, getContentValues(syncShow.rating, syncShow.rated_at), String.valueOf(syncShow.ids.trakt));

                for(SyncEpisode syncEpisode : syncEpisodes)
                    DbHelper.updateEpisode(context, getContentValues(syncEpisode.rating, syncEpisode.rated_at), String.valueOf(syncEpisode.ids.trakt));
            }

            private ContentValues getContentValues(Rating rating, DateTime ratedAt)
            {
                CVHelper cvHelper = new CVHelper().put(SyncColumns.RATED_AT, ratedAt);
                if(rating == null)
                    cvHelper.putNull(SyncColumns.RATING);
                else
                    cvHelper.put(SyncColumns.RATING, rating.value);
                return cvHelper.get();
            }
        });
    }

    private Observable<SyncResponse> getObservable(TraktSenderObservable observable, Action0 updateDBAction)
    {
        // if user is logged in, send info to trakt
        if(TraktoidPrefs.INSTANCE.isUserLoggedIn())
        {
            return Observable
                    .create(observable)
                    .doOnNext(new Action1<SyncResponse>()
                    {
                        @Override
                        public void call(SyncResponse syncResponse)
                        {
                            SyncStats syncStatsAdded = syncResponse.added;
                            SyncStats syncStatsDeleted = syncResponse.deleted;
                            SyncStats syncStatsExisting = syncResponse.existing;
                            SyncErrors syncStatsNotFound = syncResponse.not_found;

                            // in case an item hasn't been found, remove it from the list where it belongs so we don't update
                            // the field later
                            if (syncStatsNotFound != null)
                            {
                                for (SyncShow syncShowNotFound : syncStatsNotFound.shows)
                                {
                                    for (Iterator<SyncShow> iterator = syncShows.iterator(); iterator.hasNext(); )
                                    {
                                        SyncShow syncShow = iterator.next();
                                        if (syncShow.ids.trakt.equals(syncShowNotFound.ids.trakt))
                                            iterator.remove();
                                    }
                                }

                                for (SyncEpisode syncEpisodeNotFound : syncStatsNotFound.episodes)
                                {
                                    for (Iterator<SyncEpisode> iterator = syncEpisodes.iterator(); iterator.hasNext(); )
                                    {
                                        SyncEpisode syncEpisode = iterator.next();
                                        if (syncEpisode.ids.trakt.equals(syncEpisodeNotFound.ids.trakt))
                                            iterator.remove();
                                    }
                                }

                                for (SyncMovie syncMovieNotFound : syncStatsNotFound.movies)
                                {
                                    for (Iterator<SyncMovie> iterator = syncMovies.iterator(); iterator.hasNext(); )
                                    {
                                        SyncMovie syncMovie = iterator.next();
                                        if (syncMovie.ids.trakt.equals(syncMovieNotFound.ids.trakt))
                                            iterator.remove();
                                    }
                                }
                            }
                        }
                    }).doOnCompleted(updateDBAction);
        }
        // if not do it just locally
        else
        {
            SyncResponse syncResponse = new SyncResponse();
            SyncStats syncStats = new SyncStats();
            syncStats.shows = syncShows.size();
            syncStats.seasons = syncSeasons.size();
            syncStats.episodes = syncEpisodes.size();
            syncStats.movies = syncMovies.size();
            if(observable.add)
                syncResponse.added = syncStats;
            else
                syncResponse.deleted = syncStats;

            return Observable.just(syncResponse).doOnCompleted(updateDBAction);
        }
    }

    private abstract static class BaseBuilder
    {
        protected Context context;

        protected List<SyncShow> syncShows = new ArrayList<>();
        protected List<SyncSeason> syncSeasons = new ArrayList<>();
        protected List<SyncEpisode> syncEpisodes = new ArrayList<>();
        protected List<SyncMovie> syncMovies = new ArrayList<>();

        public abstract Observable<SyncResponse> getObservable(boolean add);

        public BaseBuilder(Context context)
        {
            this.context = context;
        }

        protected SyncMovie getSyncMovie(MovieIds ids, DateTime dateTime)
        {
            return new SyncMovie().id(ids);
        }

        protected SyncShow getSyncShow(ShowIds ids, DateTime dateTime)
        {
            return new SyncShow().id(ids);
        }

        protected SyncEpisode getSyncEpisode(EpisodeIds ids, DateTime dateTime)
        {
            return new SyncEpisode().id(ids);
        }

        public void clear()
        {
            syncShows.clear();
            syncMovies.clear();
            syncEpisodes.clear();
        }

        protected TraktSender build()
        {
            return new TraktSender(context, this);
        }
    }

    public static abstract class Builder<T extends Builder<T>> extends BaseBuilder
    {
        protected abstract T self();

        public Builder(Context context)
        {
            super(context);
        }

        public T movie(Movie movie, DateTime dateTime)
        {
            syncMovies.add(getSyncMovie(movie.ids, dateTime));
            return self();
        }

        public T movie(Movie movie)
        {
            movie(movie, DateHelper.now());
            return self();
        }

        public T movies(List<Movie> movies, DateTime dateTime)
        {
            for(Movie movie : movies)
                syncMovies.add(getSyncMovie(movie.ids, dateTime));
            return self();
        }

        public T movies(List<Movie> movies)
        {
            movies(movies, DateHelper.now());
            return self();
        }

        public T show(Show show, DateTime dateTime)
        {
            syncShows.add(getSyncShow(show.ids, dateTime));
            return self();
        }

        public T show(Show show)
        {
            show(show, DateHelper.now());
            return self();
        }

        public T shows(List<Show> shows, DateTime dateTime)
        {
            for(Show show : shows)
                syncShows.add(getSyncShow(show.ids, dateTime));
            return self();
        }

        public T shows(List<Show> shows)
        {
            shows(shows, DateHelper.now());
            return self();
        }

        public T episode(Episode episode, DateTime dateTime)
        {
            syncEpisodes.add(getSyncEpisode(episode.ids, dateTime));
            return self();
        }

        public T episode(Episode episode)
        {
            episode(episode, DateHelper.now());
            return self();
        }

        public T episodes(List<Episode> episodes, DateTime dateTime)
        {
            for(Episode episode : episodes)
                syncEpisodes.add(getSyncEpisode(episode.ids, dateTime));
            return self();
        }

        public T episodes(List<Episode> episodes)
        {
            episodes(episodes, DateHelper.now());
            return self();
        }
    }

    public static class HistoryBuilder extends Builder<HistoryBuilder>
    {
        public HistoryBuilder(Context context)
        {
            super(context);
        }

        @Override
        protected HistoryBuilder self()
        {
            return this;
        }

        @Override
        protected SyncMovie getSyncMovie(MovieIds ids, DateTime dateTime)
        {
            return super.getSyncMovie(ids, dateTime).watchedAt(dateTime);
        }

        @Override
        protected SyncShow getSyncShow(ShowIds ids, DateTime dateTime)
        {
            return super.getSyncShow(ids, dateTime).watchedAt(dateTime);
        }

        @Override
        protected SyncEpisode getSyncEpisode(EpisodeIds ids, DateTime dateTime)
        {
            return super.getSyncEpisode(ids, dateTime).watchedAt(dateTime);
        }

        @Override
        public Observable<SyncResponse> getObservable(boolean add)
        {
            return build().history(add);
        }
    }

    public static class CollectionBuilder extends Builder<CollectionBuilder>
    {
        public CollectionBuilder(Context context)
        {
            super(context);
        }

        @Override
        protected CollectionBuilder self()
        {
            return this;
        }

        @Override
        protected SyncMovie getSyncMovie(MovieIds ids, DateTime dateTime)
        {
            return super.getSyncMovie(ids, dateTime).collectedAt(dateTime);
        }

        @Override
        protected SyncShow getSyncShow(ShowIds ids, DateTime dateTime)
        {
            return super.getSyncShow(ids, dateTime).collectedAt(dateTime);
        }

        @Override
        protected SyncEpisode getSyncEpisode(EpisodeIds ids, DateTime dateTime)
        {
            return super.getSyncEpisode(ids, dateTime).collectedAt(dateTime);
        }

        @Override
        public Observable<SyncResponse> getObservable(boolean add)
        {
            return build().collection(add);
        }
    }

    public static class WatchlistBuilder extends Builder<WatchlistBuilder>
    {
        public WatchlistBuilder(Context context)
        {
            super(context);
        }

        @Override
        protected WatchlistBuilder self()
        {
            return this;
        }

        @Override
        public Observable<SyncResponse> getObservable(boolean add)
        {
            return build().watchlist(add);
        }
    }

    public static class RatingBuilder extends BaseBuilder
    {
        public RatingBuilder(Context context)
        {
            super(context);
        }

        @Override
        protected SyncMovie getSyncMovie(MovieIds ids, DateTime dateTime)
        {
            return super.getSyncMovie(ids, dateTime).ratedAt(dateTime);
        }

        @Override
        protected SyncShow getSyncShow(ShowIds ids, DateTime dateTime)
        {
            return super.getSyncShow(ids, dateTime).ratedAt(dateTime);
        }

        @Override
        protected SyncEpisode getSyncEpisode(EpisodeIds ids, DateTime dateTime)
        {
            return super.getSyncEpisode(ids, dateTime).ratedAt(dateTime);
        }

        public RatingBuilder movie(Movie movie, Rating rating, DateTime dateTime)
        {
            syncMovies.add(getSyncMovie(movie.ids, dateTime).ratedAt(dateTime).rating(rating));
            return this;
        }

        public RatingBuilder movie(Movie movie, Rating rating)
        {
            movie(movie, rating, DateHelper.now());
            return this;
        }

        public RatingBuilder movies(List<Movie> movies, Rating rating, DateTime dateTime)
        {
            for(Movie movie : movies)
                syncMovies.add(getSyncMovie(movie.ids, dateTime).ratedAt(dateTime).rating(rating));
            return this;
        }

        public RatingBuilder movies(List<Movie> movies, Rating rating)
        {
            movies(movies, rating, DateHelper.now());
            return this;
        }

        public RatingBuilder show(Show show, Rating rating, DateTime dateTime)
        {
            syncShows.add(getSyncShow(show.ids, dateTime).ratedAt(dateTime).rating(rating));
            return this;
        }

        public RatingBuilder show(Show show, Rating rating)
        {
            show(show, rating, DateHelper.now());
            return this;
        }

        public RatingBuilder shows(List<Show> shows, Rating rating, DateTime dateTime)
        {
            for(Show show : shows)
                syncShows.add(getSyncShow(show.ids, dateTime).ratedAt(dateTime).rating(rating));
            return this;
        }

        public RatingBuilder shows(List<Show> shows, Rating rating)
        {
            shows(shows, rating, DateHelper.now());
            return this;
        }

        public RatingBuilder episode(Episode episode, Rating rating, DateTime dateTime)
        {
            syncEpisodes.add(getSyncEpisode(episode.ids, dateTime).ratedAt(dateTime).rating(rating));
            return this;
        }

        public RatingBuilder episode(Episode episode, Rating rating)
        {
            episode(episode, rating, DateHelper.now());
            return this;
        }

        public RatingBuilder episodes(List<Episode> episodes, Rating rating, DateTime dateTime)
        {
            for(Episode episode : episodes)
                syncEpisodes.add(getSyncEpisode(episode.ids, dateTime).ratedAt(dateTime).rating(rating));
            return this;
        }

        public RatingBuilder episodes(List<Episode> episodes, Rating rating)
        {
            episodes(episodes, rating, DateHelper.now());
            return this;
        }

        @Override
        public Observable<SyncResponse> getObservable(boolean add)
        {
            return build().rating(add);
        }
    }

    private abstract class TraktSenderObservable extends TraktObservable<SyncResponse>
    {
        public boolean add;

        protected abstract SyncResponse addItems() throws OAuthUnauthorizedException;
        protected abstract SyncResponse deleteItems() throws OAuthUnauthorizedException;

        public TraktSenderObservable(boolean add)
        {
            this.add = add;
        }

        @Override
        public SyncResponse fire() throws OAuthUnauthorizedException
        {
            if(!syncShows.isEmpty())
                syncItems.shows(syncShows);
            if(!syncSeasons.isEmpty())
                syncItems.seasons(syncSeasons);
            if(!syncEpisodes.isEmpty())
                syncItems.episodes(syncEpisodes);
            if(!syncMovies.isEmpty())
                syncItems.movies(syncMovies);

            return add ? addItems() : deleteItems();
        }
    }

    private class SyncItems2 extends SyncItems
    {
        public List<SyncSeason> seasons;
        public List<SyncEpisode> episodes;

        public SyncItems seasons(SyncSeason season) {
            LinkedList<SyncSeason> list = new LinkedList<>();
            list.add(season);
            return seasons(list);
        }

        public SyncItems seasons(List<SyncSeason> seasons) {
            this.seasons = seasons;
            return this;
        }

        public SyncItems episodes(SyncEpisode episode) {
            LinkedList<SyncEpisode> list = new LinkedList<>();
            list.add(episode);
            return episodes(list);
        }

        public SyncItems episodes(List<SyncEpisode> episodes) {
            this.episodes = episodes;
            return this;
        }
    }
}
