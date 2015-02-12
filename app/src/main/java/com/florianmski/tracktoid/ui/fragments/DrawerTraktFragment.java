package com.florianmski.tracktoid.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.lists.RecyclerDrawerAdapter;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.florianmski.tracktoid.image.ImagesTest;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.ui.fragments.base.DrawerFragment;
import com.florianmski.tracktoid.ui.fragments.calendar.CalendarFragment;
import com.florianmski.tracktoid.ui.fragments.library.LibrarySwitchFragment;
import com.florianmski.tracktoid.ui.fragments.progress.ProgressFragment;
import com.florianmski.tracktoid.ui.fragments.recommendations.RecommendationSwitchFragment;
import com.florianmski.tracktoid.ui.fragments.search.SearchSwitchFragment;
import com.florianmski.tracktoid.ui.fragments.trending.TrendingSwitchFragment;
import com.florianmski.tracktoid.ui.widgets.Placeholder;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;

public class DrawerTraktFragment extends DrawerFragment implements Observer<WShow>
{
    private Subscription subscription;

    public static DrawerTraktFragment newInstance()
    {
        return new DrawerTraktFragment();
    }

    @Override
    protected RecyclerDrawerAdapter createAdapter(RecyclerDrawerAdapter adapter)
    {
        return adapter
                .addItem(R.id.drawer_library,           "Library",          R.drawable.ic_class_white_24dp)
                .addItem(R.id.drawer_progress,          "Progress",         R.drawable.ic_poll_white_24dp)
                .addItem(R.id.drawer_calendar,          "Calendar",         R.drawable.ic_event_white_24dp)
                .addItem(R.id.drawer_recommendations,   "Recommendations",  R.drawable.ic_thumb_up_white_24dp)
                .addSeparator()
                .addSubheader("Community")
                .addItem(R.id.drawer_trending, "Trending",  R.drawable.ic_whatshot_white_24dp)
                .addItem(R.id.drawer_search,   "Search",    R.drawable.ic_search_white_24dp);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        subscription = AndroidObservable.bindFragment(this, Observable.create(new CursorObservable<WShow>(
                getActivity(),
                ProviderSchematic.Shows.CONTENT_URI,
                ProviderSchematic.Shows.PROJECTION,
                null,
                null,
                ShowColumns.LAST_WATCHED_AT + " DESC LIMIT 1"
        )
        {
            @Override
            protected WShow toObject(Cursor cursor)
            {
                return WShow.unpack(cursor);
            }
        }).subscribeOn(Schedulers.io())).subscribe(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        subscription.unsubscribe();
    }

    @Override
    protected void changeFragment(int position)
    {
        // replace the main content by replacing fragments
        Fragment f;
        switch(getAdapter().getItem2(position).id)
        {
            case R.id.drawer_library:
                f = LibrarySwitchFragment.newInstance();
                break;
            //            case Profile:
            //                f = PagerUserFragment.newInstance(TraktoidPrefs.INSTANCE.getUsername());
            //                break;
            case R.id.drawer_progress:
                f = ProgressFragment.newInstance();
                break;
            //            case Network:
            //                f = NetworkFragment.newInstance(TraktoidPrefs.INSTANCE.getUsername());
            //                break;
            case R.id.drawer_calendar:
                f = CalendarFragment.newInstance();
                break;
            case R.id.drawer_trending:
                f = TrendingSwitchFragment.newInstance();
                break;
            case R.id.drawer_recommendations:
                f = RecommendationSwitchFragment.newInstance();
                break;
            case R.id.drawer_search:
                f = SearchSwitchFragment.newInstance();
                break;
            //            case EraseDB:
            //                boolean delete = deleteDatabase(DatabaseSchematic.FILE_NAME);
            //                if(delete)
            //                    Toast.makeText(HomeActivity.this, "Database deleted!", Toast.LENGTH_SHORT).show();
            //                break;
            //            case SaveDB:
            //                Observable<Object> o = Observable.create(new Observable.OnSubscribe<Object>()
            //                {
            //                    @Override
            //                    public void call(Subscriber<? super Object> subscriber)
            //                    {
            //                        try
            //                        {
            //                            Utils.writeDBToSD(HomeActivity.this, DatabaseSchematic.FILE_NAME);
            //                        }
            //                        catch (IOException e)
            //                        {
            //                            subscriber.onError(e);
            //                        }
            //
            //                        subscriber.onCompleted();
            //                    }
            //                });
            //
            //                AndroidObservable
            //                        .bindActivity(this, o.subscribeOn(Schedulers.io()))
            //                        .subscribe(new Observer<Object>()
            //                        {
            //                            @Override
            //                            public void onCompleted()
            //                            {
            //                                Toast.makeText(HomeActivity.this, "Database saved to sdcard!", Toast.LENGTH_SHORT).show();
            //                            }
            //
            //                            @Override
            //                            public void onError(Throwable e)
            //                            {
            //                                e.printStackTrace();
            //                            }
            //
            //                            @Override
            //                            public void onNext(Object o) {}
            //                        });
            //                break;
            default:
                f = EmptyFragment.newInstance();
                break;
        }

        if(f != null)
        {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, f)
                    .commit();
        }
    }

    @Override
    protected int getDefaultId()
    {
        return R.id.drawer_library;
    }

    @Override
    public void onCompleted()
    {

    }

    @Override
    public void onError(Throwable e)
    {

    }

    @Override
    public void onNext(WShow wShow)
    {
        if(wShow != null)
            ImagesTest.load(getActivity(), tivFanart, wShow.getTraktItem().images)
                    .placeholder(new Placeholder(getActivity(), TraktoidTheme.DEFAULT).getDrawable())
                    .into(tivFanart);
    }
}
