package com.florianmski.tracktoid.ui.fragments.progress;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.lists.RecyclerTraktItemAdapter;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.ui.fragments.base.trakt.TraktItemsFragment;

import java.util.List;

import rx.Observable;

public class ProgressFragment extends TraktItemsFragment<WShow>
{
    protected static final int
            FILTER_WATCHED = R.id.action_bar_filter_watched,
            FILTER_COLLECTION = R.id.action_bar_filter_collection;
    protected boolean filterHideComplete = false;
    protected int currentFilter = FILTER_WATCHED;

    public ProgressFragment() {}

    public static ProgressFragment newInstance()
    {
        return new ProgressFragment();
    }

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

        getActionBar().setSubtitle("Watched");
    }

    @Override
    protected Observable<List<WShow>> createObservable()
    {
        return Observable.create(
                new CursorObservable<List<WShow>>(
                        getActivity(),
                        ProviderSchematic.Shows.CONTENT_URI,
                        ProviderSchematic.Shows.PROJECTION,
                        generateSelection(),
                        null,
                        ShowColumns.LAST_WATCHED_AT + " DESC")
                {
                    @Override
                    protected List<WShow> toObject(Cursor cursor)
                    {
                        return WShow.unpackList(cursor);
                    }
                });
    }

    private String generateSelection()
    {
        String selection = ShowColumns.EPISODES_AIRED + ">0";

        if(filterHideComplete)
            selection += " AND "
                    + (currentFilter == FILTER_WATCHED ? ShowColumns.EPISODES_WATCHED : ShowColumns.EPISODES_COLLECTED)
                    + "<" + ShowColumns.EPISODES_AIRED;

        return selection;
    }

    @Override
    protected Type getImageType()
    {
        return Type.FANART;
    }

    @Override
    protected RecyclerTraktItemAdapter<WShow> createAdapter(List<WShow> items)
    {
        return new RecyclerProgressAdapter(getActivity(), items, getTheme(), this).imageType(getImageType());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        SubMenu filterMenu = menu.addSubMenu(Menu.NONE, R.id.action_bar_filter, Menu.NONE, "Filter");
        filterMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        filterMenu.setIcon(R.drawable.ic_filter_list_white_24dp);
        createGroupItem(filterMenu, Menu.FIRST, FILTER_WATCHED, Menu.NONE, "Watched");
        createGroupItem(filterMenu, Menu.FIRST, FILTER_COLLECTION, Menu.NONE, "Collection");
        filterMenu.setGroupCheckable(Menu.FIRST, true, true);

        menu.add(Menu.NONE, R.id.action_bar_filter_hide_complete, Menu.NONE, "Hide complete")
                .setCheckable(true)
                .setChecked(filterHideComplete)
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
            case R.id.action_bar_filter_watched:
            case R.id.action_bar_filter_collection:
                changeFilter(itemId);
                getActionBar().setSubtitle(item.getTitle());
                return true;
            case R.id.action_bar_filter_hide_complete:
                filterHideComplete = !filterHideComplete;
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
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }

    private class RecyclerProgressAdapter extends RecyclerTraktItemAdapter<WShow>
    {
        public RecyclerProgressAdapter(Context context, List<WShow> list, TraktoidTheme theme, OnItemClickListener listener)
        {
            super(context, list, theme, listener);
        }

        @Override
        public void onBindViewHolder(final TraktItemViewHolder holder, int position)
        {
            super.onBindViewHolder(holder, position);

            WShow wShow = getItem2(position);
            int episodesAired = wShow.episodesAired;
            int episodesToCompare = currentFilter == FILTER_WATCHED ? wShow.episodesWatched : wShow.episodeCollected;
            int percentage = (int)((episodesToCompare * 1f / episodesAired) * 100);

            holder.fv.setSubtitle(String.format("%d%% (%d/%d)", percentage, episodesToCompare, episodesAired));
        }
    }
}
