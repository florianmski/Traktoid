package com.florianmski.tracktoid.ui.fragments.user;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.adapters.lists.RecyclerUserAdapter;
import com.florianmski.tracktoid.ui.activities.UserActivity;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemRecyclerViewFragment;
import com.uwetrottmann.trakt.v2.entities.User;

import java.util.List;

import rx.Observable;

public class NetworkFragment extends ItemRecyclerViewFragment<User> implements ActionBar.OnNavigationListener, RecyclerAdapter.OnItemClickListener
{
    private String userId;

    private int currentSelectedNavigationItem = Title.Friends.ordinal();

    private enum Title
    {
        Friends,
        Followers,
        Following;

        public static String[] titles()
        {
            String[] titles = new String[Title.values().length];
            for(int i = 0; i < titles.length; i++)
                titles[i] = Title.values()[i].name();
            return titles;
        }

        public static Title fromPosition(int position)
        {
            return Title.values()[position];
        }
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager()
    {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_fanart_columns));
    }

    public NetworkFragment() {}

    public static NetworkFragment newInstance(String userId)
    {
        NetworkFragment f = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(TraktoidConstants.BUNDLE_ID, userId);
        f.setArguments(args);
        return f;
    }

    @Override
    protected void setupActionBar()
    {
//        if(getUserVisibleHint() && getActionBar().getNavigationMode() != ActionBar.NAVIGATION_MODE_LIST)
//        {
//            // TODO
////            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
////            getActionBar().setListNavigationCallbacks(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Title.titles()), this);
////            getActionBar().setSelectedNavigationItem(currentSelectedNavigationItem);
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        userId = getArguments().getString(TraktoidConstants.BUNDLE_ID);
        setupActionBar();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.DEFAULT;
    }

    @Override
    public void onItemClick(View v, int position)
    {
        UserActivity.launch(getActivity(), getAdapter().getItem2(position).username);
    }

    @Override
    protected RecyclerAdapter<User, ?> createAdapter(List<User> items)
    {
        return new RecyclerUserAdapter(getActivity(), items, this);
    }

    @Override
    protected Observable<List<User>> createObservable()
    {
        return Observable.create(new TraktObservable<List<User>>()
        {
            @Override
            public List<User> fire()
            {
                // TODO
//                int itemSelected = getActionBar().getSelectedNavigationIndex();
//                Title t = Title.fromPosition(itemSelected == -1 ? currentSelectedNavigationItem : itemSelected);
//
//                switch(t)
//                {
//                    case Friends:
//                        return TraktManager.getInstance().users().friends(userId);
//                    case Followers:
//                        return TraktManager.getInstance().users().followers(userId);
//                    case Following:
//                        return TraktManager.getInstance().users().following(userId);
//                    default:
//                        throw new UnsupportedOperationException();
//                }
                return null;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {
        if(currentSelectedNavigationItem == itemPosition)
            return false;
        else
        {
            refresh(true);
            currentSelectedNavigationItem = itemPosition;
        }
        return true;
    }
}
