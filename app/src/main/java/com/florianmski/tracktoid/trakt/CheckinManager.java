package com.florianmski.tracktoid.trakt;

import android.content.Context;

import com.florianmski.tracktoid.BuildConfig;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.services.CheckinService;
import com.uwetrottmann.trakt.v2.entities.BaseCheckinResponse;
import com.uwetrottmann.trakt.v2.entities.EpisodeCheckin;
import com.uwetrottmann.trakt.v2.entities.EpisodeCheckinResponse;
import com.uwetrottmann.trakt.v2.entities.EpisodeIds;
import com.uwetrottmann.trakt.v2.entities.MovieCheckin;
import com.uwetrottmann.trakt.v2.entities.MovieCheckinResponse;
import com.uwetrottmann.trakt.v2.entities.MovieIds;
import com.uwetrottmann.trakt.v2.entities.SyncEpisode;
import com.uwetrottmann.trakt.v2.entities.SyncMovie;
import com.uwetrottmann.trakt.v2.exceptions.CheckinInProgressException;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CheckinManager
{
    private final static String APP_VERSION = String.format("Traktoid %s", BuildConfig.VERSION_NAME);

    private static CheckinManager manager;

    public static synchronized CheckinManager getInstance()
    {
        if (manager == null)
            return new CheckinManager();
        return manager;
    }

    public Observable<MovieCheckinResponse> checkinMovie(final Context context, final int id, final String title, final int duration)
    {
        return checkin(new TraktObservable<MovieCheckinResponse>()
        {
            @Override
            public MovieCheckinResponse fire() throws OAuthUnauthorizedException, CheckinInProgressException
            {
                MovieCheckin checkin = new MovieCheckin.Builder(new SyncMovie().id(MovieIds.trakt(id)), APP_VERSION, null).build();
                return TraktManager.getInstance().checkin().checkin(checkin);
            }
        }, new Action0()
        {
            @Override
            public void call()
            {
                CheckinService.checkinMovie(context, id, title, duration);
            }
        });
    }

    public Observable<EpisodeCheckinResponse> checkinEpisode(final Context context, final int id, final String title, final int duration)
    {
        return checkin(new TraktObservable<EpisodeCheckinResponse>()
        {
            @Override
            public EpisodeCheckinResponse fire() throws OAuthUnauthorizedException, CheckinInProgressException
            {
                EpisodeCheckin checkin = new EpisodeCheckin.Builder(new SyncEpisode().id(EpisodeIds.trakt(id)), APP_VERSION, null).build();
                return TraktManager.getInstance().checkin().checkin(checkin);
            }
        }, new Action0()
        {
            @Override
            public void call()
            {
                CheckinService.checkinEpisode(context, id, title, duration);
            }
        });
    }

    private <T extends BaseCheckinResponse> Observable<T> checkin(TraktObservable<T> traktObservable, Action0 callServiceAction)
    {
        return Observable
                .create(traktObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnCompleted(callServiceAction);
    }

    public void stop(Context context)
    {
        CheckinService.stopCheckin(context);
    }

    public boolean checkinInProgress()
    {
        return CheckinService.isCheckinInProgress();
    }
}
