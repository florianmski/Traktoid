package com.florianmski.tracktoid.rx.observables;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

// This class has the same goal as CursorLoader but made with RxJava
// -> Refresh automagically the data associated with the cursor when the db has been modified
// I'm quite new to rxJava and I'm pretty sure this class is awful but I couldn't come with another solution
// Basically here is what's wrong
// -> never calling onCompleted() (otherwise the contentObserver is not working)
// -> passing the observer in this class seems plain wrong, I'm pretty sure there is a more "Rx" way to do it
// TODO should try this with a subject
public abstract class CursorObservable<T> implements Observable.OnSubscribe<T>
{
    private final ForceLoadContentObserver contentObserver;

    protected Uri uri;
    protected String[] projection;
    protected String selection;
    protected String[] selectionArgs;
    protected String sortOrder;

    protected Context context;
    protected Cursor cursor;
    protected Subscriber<? super T> subscriber;

    protected abstract T toObject(Cursor cursor);

    public CursorObservable(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        this.contentObserver = new ForceLoadContentObserver();

        this.context = context.getApplicationContext();
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;

        context.getContentResolver().registerContentObserver(uri, false, contentObserver);
    }

    private void closeCursor()
    {
        if(cursor != null && !cursor.isClosed())
            cursor.close();
    }

    @Override
    public void call(Subscriber<? super T> subscriber)
    {
        this.subscriber = subscriber;

        try
        {
            closeCursor();

            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            if (cursor != null)
            {
                // Ensure the cursor window is filled
                cursor.getCount();

                T data = null;
                if(cursor.moveToFirst())
                    data = toObject(cursor);

                subscriber.onNext(data);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            subscriber.onError(e);
        }

        subscriber.add(Subscriptions.create(new Action0()
        {
            @Override
            public void call()
            {
                closeCursor();
                context.getContentResolver().unregisterContentObserver(contentObserver);
            }
        }));
    }

    public final class ForceLoadContentObserver extends ContentObserver
    {
        public ForceLoadContentObserver()
        {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications()
        {
            return true;
        }

        @Override
        public void onChange(boolean selfChange)
        {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri)
        {
            // update cursor only if it the same uri (because framework will trigger a onChange for the detailed uri
            // and then all its descendants
            // if uri is null, it means we are < API 16, so we don't know about the uri, update anyway
            if(uri == null || uri.equals(CursorObservable.this.uri))
            {
                if(uri == null)
                    Timber.d("updating : null");
                else
                    Timber.d("updating : " + uri.toString().replace("content://com.florianmski.tracktoid.data.provider.TraktoidProvider/", ""));

                call(subscriber);
            }
        }
    }
}
