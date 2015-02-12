package com.florianmski.tracktoid;

import android.app.Application;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.trakt.TraktManager;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class TraktoidApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        ProviderSchematic.init(getApplicationContext());
        TraktoidPrefs.create(getApplicationContext());
        TraktManager.create(getApplicationContext());

        if(BuildConfig.DEBUG)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());

            Timber.plant(new Timber.DebugTree());
        }
        else
        {
            Fabric.with(this, new Crashlytics());
            if(TraktoidPrefs.INSTANCE.isUserLoggedIn())
                Crashlytics.setUserName(TraktoidPrefs.INSTANCE.getUsername());

            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.HollowTree
    {
        @Override
        public void i(String message, Object... args)
        {
            Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args)
        {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args)
        {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args)
        {
            e(message, args);
            Crashlytics.logException(t);
        }
    }

    @Override
    public void onLowMemory() {}

}
