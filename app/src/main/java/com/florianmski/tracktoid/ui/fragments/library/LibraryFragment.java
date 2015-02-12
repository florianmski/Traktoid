package com.florianmski.tracktoid.ui.fragments.library;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.lists.RecyclerTraktItemAdapter;
import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.ui.fragments.base.trakt.TraktItemsFragment;

public abstract class LibraryFragment<E extends TraktoidItem> extends TraktItemsFragment<E>
{
    // TODO add filter to pref so it can persist across app launches

    protected static final int
            FILTER_PLAYS = R.id.action_bar_filter_plays,
            FILTER_COLLECTION = R.id.action_bar_filter_collection,
            FILTER_WATCHLIST = R.id.action_bar_filter_watchlist,
            FILTER_ALL = R.id.action_bar_filter_all;
    protected boolean filterHideWatched = false;
    protected int currentFilter = FILTER_PLAYS;

    public LibraryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setInstantLoad(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ((RecyclerTraktItemAdapter)getAdapter()).titleVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        SubMenu filterMenu = menu.addSubMenu(Menu.NONE, R.id.action_bar_filter, Menu.NONE, "Filter");
        filterMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        filterMenu.setIcon(R.drawable.ic_filter_list_white_24dp);
        createGroupItem(filterMenu, Menu.FIRST, FILTER_PLAYS, Menu.NONE, "Plays");
        createGroupItem(filterMenu, Menu.FIRST, FILTER_COLLECTION, Menu.NONE, "Collection");
        createGroupItem(filterMenu, Menu.FIRST, FILTER_WATCHLIST, Menu.NONE, "Watchlist");
        createGroupItem(filterMenu, Menu.FIRST, FILTER_ALL, Menu.NONE, "All");
        filterMenu.setGroupCheckable(Menu.FIRST, true, true);

        menu.add(Menu.NONE, R.id.action_bar_filter_hide_watched, Menu.NONE, "Hide watched")
                .setCheckable(true)
                .setChecked(filterHideWatched)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    private void createGroupItem(SubMenu subMenu, int groupId, int filterId, int order, CharSequence title)
    {
        subMenu.add(groupId, filterId, order, title).setChecked(currentFilter == filterId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        switch(itemId)
        {
            case R.id.action_bar_filter_plays:
            case R.id.action_bar_filter_collection:
            case R.id.action_bar_filter_watchlist:
            case R.id.action_bar_filter_all:
                changeFilter(itemId);
                return true;
            case R.id.action_bar_filter_hide_watched:
                filterHideWatched = !filterHideWatched;
                refresh(false);
                getActivity().invalidateOptionsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeFilter(int newFilter)
    {
        if(currentFilter == newFilter)
            return;

        currentFilter = newFilter;
        refresh(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    protected Type getImageType()
    {
        return Type.POSTER;
    }
}
