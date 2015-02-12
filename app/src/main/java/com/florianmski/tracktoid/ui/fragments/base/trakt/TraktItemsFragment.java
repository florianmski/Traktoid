package com.florianmski.tracktoid.ui.fragments.base.trakt;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.adapters.lists.RecyclerTraktItemAdapter;
import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.data.WEpisode;
import com.florianmski.tracktoid.data.WMovie;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.ui.activities.EpisodeActivity;
import com.florianmski.tracktoid.ui.activities.MovieActivity;
import com.florianmski.tracktoid.ui.activities.ShowActivity;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemRecyclerViewFragment;
import com.florianmski.tracktoid.ui.fragments.base.switcher.SwitchFragment;

import java.util.List;

public abstract class TraktItemsFragment<E extends TraktoidItem> extends ItemRecyclerViewFragment<E> implements RecyclerAdapter.OnItemClickListener
{
    private RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            super.onScrolled(recyclerView, dx, dy);

            ((SwitchFragment)getParentFragment()).getFAB().reactToScroll(dy);
        }
    };

    private boolean fabInParent = false;

    protected abstract Type getImageType();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fabInParent = getParentFragment() != null && getParentFragment() instanceof SwitchFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(fabInParent)
            addScrollListener(recyclerScrollListener);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(fabInParent)
            removeScrollListener(recyclerScrollListener);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager()
    {
        return new GridLayoutManager(
                getActivity(),
                getResources().getInteger(getImageType() == Type.POSTER ? R.integer.grid_poster_columns : R.integer.grid_fanart_columns));
    }

    @Override
    protected RecyclerAdapter<E, ?> createAdapter(List<E> items)
    {
        return new RecyclerTraktItemAdapter<>(getActivity(), items, getTheme(), this)
                .imageType(getImageType())
                .titleVisible(true);
    }

    @Override
    public void onItemClick(View v, int position)
    {
        E traktoidItem = getAdapter().getItem2(position);

        if(traktoidItem instanceof WShow)
            ShowActivity.launch(getActivity(), (WShow) traktoidItem);
        else if(traktoidItem instanceof WMovie)
            MovieActivity.launch(getActivity(), (WMovie) traktoidItem);
        else if(traktoidItem instanceof WEpisode)
            EpisodeActivity.launch(getActivity(), (WEpisode) traktoidItem);
    }
}