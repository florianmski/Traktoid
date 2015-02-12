package com.florianmski.tracktoid.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

public class TraktoidService extends IntentService
{
    public TraktoidService()
    {
        super("TraktoidService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        new TraktoidSynchronizer(getApplicationContext()).sync();
    }

    public static void start(Context context)
    {
        Intent i = new Intent(context, TraktoidService.class);
        context.startService(i);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);

        // clean notification if app is killed
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TraktoidSynchronizer.NOTIFICATION_ID);
    }
}