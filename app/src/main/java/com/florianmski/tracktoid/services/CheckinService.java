package com.florianmski.tracktoid.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.database.columns.SyncColumns;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.utils.CVHelper;
import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.utils.DbHelper;
import com.uwetrottmann.trakt.v2.exceptions.CheckinInProgressException;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import java.util.concurrent.TimeUnit;

import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import timber.log.Timber;

public class CheckinService extends IntentService
{
    private final static int NOTIFICATION_ID = 1337;

    private final static String BUNDLE_CHECKIN = "checkin";
    private final static String BUNDLE_DURATION = "duration";
    private final static int BUNDLE_STOP_CHECKIN = 0;
    private final static int BUNDLE_CHECKIN_EPISODE = 1;
    private final static int BUNDLE_CHECKIN_MOVIE = 2;

    private static Subscription subscription;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public CheckinService()
    {
        super("CheckinService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        int checkin = intent.getExtras().getInt(BUNDLE_CHECKIN);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(checkin == BUNDLE_STOP_CHECKIN)
        {
            if(subscription != null)
                subscription.unsubscribe();
        }
        else
        {
            final int id = intent.getExtras().getInt(TraktoidConstants.BUNDLE_ID);
            final String title = intent.getExtras().getString(TraktoidConstants.BUNDLE_TITLE);
            final int duration = intent.getExtras().getInt(BUNDLE_DURATION);

            Intent i = new Intent(this, CheckinService.class);
            i.putExtra(BUNDLE_CHECKIN, BUNDLE_STOP_CHECKIN);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(String.format("Watching %s", title))
                    .setColor(TraktoidTheme.DEFAULT.getColorDark(this))
                    .setSmallIcon(R.drawable.ic_check_white_24dp)
                    .setOngoing(true)
                    .addAction(new NotificationCompat.Action(R.drawable.ic_clear_white_24dp, "Cancel", pendingIntent));

            startTimer(checkin, id, duration);
        }
    }

    private void startTimer(final int checkin, final int id, int duration)
    {
        subscription = Observable
                .interval((duration * 60) / 100, TimeUnit.SECONDS)
                .takeWhile(new Func1<Long, Boolean>()
                {
                    @Override
                    public Boolean call(Long tick)
                    {
                        return tick < 100;
                    }
                })
                .doOnSubscribe(new Action0()
                {
                    @Override
                    public void call()
                    {
                        updateProgress(0);
                    }
                })
                .doOnUnsubscribe(new Action0()
                {
                    @Override
                    public void call()
                    {
                        cancelCheckin();
                    }
                })
                .subscribe(new Subscriber<Long>()
                {
                    @Override
                    public void onCompleted()
                    {
                        cancelNotification();

                        // mark as seen in db
                        CVHelper cv = new CVHelper()
                                .put(SyncColumns.WATCHED, true)
                                .put(SyncColumns.LAST_WATCHED_AT, DateHelper.now());

                        if(checkin == BUNDLE_CHECKIN_MOVIE)
                            DbHelper.updateMovie(CheckinService.this, cv.get(), String.valueOf(id));
                        else if(checkin == BUNDLE_CHECKIN_EPISODE)
                            DbHelper.updateEpisode(CheckinService.this, cv.get(), String.valueOf(id));
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        cancelNotification();
                        Timber.i("Error during checkin", e);
                    }

                    @Override
                    public void onNext(Long ticks)
                    {
                        updateProgress(ticks.intValue()+1);
                    }
                });
    }

    private void cancelNotification()
    {
        notificationManager.cancel(NOTIFICATION_ID);
        subscription = null;
    }

    private void cancelCheckin()
    {
        Observable.create(new TraktObservable<Response>()
        {
            @Override
            public Response fire() throws OAuthUnauthorizedException, CheckinInProgressException
            {
                return TraktManager.getInstance().checkin().deleteActiveCheckin();
            }
        }).doOnCompleted(new Action0()
        {
            @Override
            public void call()
            {
                cancelNotification();
            }
        }).subscribe();
    }

    private void updateProgress(int progress)
    {
        notificationBuilder
                .setProgress(100, progress, false)
                .setContentText(String.format("%d%%", progress));
        updateNotification();
    }

    private void updateNotification()
    {
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void checkinEpisode(Context context, int id, String title, int duration)
    {
        checkin(context, BUNDLE_CHECKIN_EPISODE, id, title, duration);
    }

    public static void checkinMovie(Context context, int id, String title, int duration)
    {
        checkin(context, BUNDLE_CHECKIN_MOVIE, id, title, duration);
    }

    private static void checkin(Context context, int checkin, int id, String title, int duration)
    {
        Intent i = new Intent(context, CheckinService.class);
        i.putExtra(BUNDLE_CHECKIN, checkin);
        i.putExtra(TraktoidConstants.BUNDLE_ID, id);
        i.putExtra(TraktoidConstants.BUNDLE_TITLE, title);
        i.putExtra(BUNDLE_DURATION, duration);
        context.startService(i);
    }

    public static void stopCheckin(Context context)
    {
        Intent i = new Intent(context, CheckinService.class);
        i.putExtra(BUNDLE_CHECKIN, BUNDLE_STOP_CHECKIN);
        context.startService(i);
    }

    public static boolean isCheckinInProgress()
    {
        return subscription != null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);

        // clean notification if app is killed
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}