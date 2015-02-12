package com.florianmski.tracktoid;

import android.content.Context;
import android.database.Cursor;

import com.florianmski.tracktoid.data.database.ProviderSchematic;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public enum AppMigration
{
    INSTANCE;

    private final static int PREVIOUS_MIGRATION_VERSION_CODE = TraktoidPrefs.INSTANCE.getPreviousMigrationVersionCode();
    // this is the last case value in the switch in migrate()
    private final static int LAST_MIGRATION_VERSION_CODE = BuildConfig.VERSION_CODE;

    // this is the minimum amount of time (in seconds) the migration should take so the user won't see a flickering
    // progress dialog
    private final static int MIN_RUNNING_TIME = 2;

    public boolean isMigrationNeeded()
    {
        return PREVIOUS_MIGRATION_VERSION_CODE < LAST_MIGRATION_VERSION_CODE;
    }

    public Observable<String> migrate(final Context context)
    {
        return Observable.create(new Observable.OnSubscribe<String>()
        {
            @Override
            public void call(Subscriber<? super String> subscriber)
            {
                final long start = System.currentTimeMillis();

                subscriber.onNext("Preparing for new version...");

                // LAST_MIGRATION_VERSION_CODE should always have the value of the int in the last "if"
//                if(PREVIOUS_MIGRATION_VERSION_CODE < 18)
//                {
//                    // do stuff to migrate from < 18 to 18
//                }
//
//                if(PREVIOUS_MIGRATION_VERSION_CODE < 21)
//                {
//                    // do stuff to migrate from between 18 and 20 to 21
//                }

                // make sure the progress dialog is visible for MIN_RUNNING_TIME seconds
                final long runningTime = System.currentTimeMillis() - start;
                try
                {
                    TimeUnit.MILLISECONDS.sleep(MIN_RUNNING_TIME * 1000 - runningTime);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                subscriber.onCompleted();
            }
        }).doOnCompleted(new Action0()
        {
            @Override
            public void call()
            {
                // if all goes well, put the current version code
                TraktoidPrefs.INSTANCE.refreshPreviousMigrationVersionCode();
            }
        });
    }

    private void upgradeDB(Context context)
    {
        // make a useless query
        // this will call getReadableDatabase() on the SQLiteOpenHelper which will call onUpgrade() if needed
        // not sure there is an official way to just trigger onUpgrade() and do it off the main thread in case it gets heavy
        Cursor c = context.getContentResolver().query(ProviderSchematic.Shows.CONTENT_URI, null, null, null, null);
        c.close();
    }
}
