package com.florianmski.tracktoid.ui.fragments.base.list;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.containers.ViewContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemRecyclerViewFragment<E> extends ItemScrollFragment<E, List<E>, RecyclerView, RecyclerView.OnScrollListener, RecyclerAdapter<E, ?>>
{
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            super.onScrollStateChanged(recyclerView, newState);

            for(RecyclerView.OnScrollListener scrollListener : scrollListeners)
                scrollListener.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            super.onScrolled(recyclerView, dx, dy);

            for(RecyclerView.OnScrollListener scrollListener : scrollListeners)
                scrollListener.onScrolled(recyclerView, dx, dy);

            View v = recyclerView.getChildAt(0);
            if(v == null)
                return;

            if(dy > 0 && v.getTop() < 0)
                showActionBar(false);
            else if(dy < 0)
                showActionBar(true);
        }
    };

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    public ItemRecyclerViewFragment()
    {
        super(new ViewContainer.RecyclerViewContainer<E>());
        data = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getGroupView().setHasFixedSize(hasFixedSize());
        getGroupView().setLayoutManager(getLayoutManager());
        getGroupView().setOnScrollListener(onScrollListener);
    }

    @Override
    protected void refreshView(List<E> data)
    {
        getAdapter().refresh(data);
    }

    @Override
    protected boolean isEmpty(List<E> data)
    {
        return data == null || data.isEmpty();
    }

    protected abstract RecyclerAdapter<E, ?> createAdapter(final List<E> items);

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewContainer.setAdapter(createAdapter(data));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public E getItem(int position)
    {
        return data.get(position);
    }

    protected RecyclerAdapter<E, ?> getAdapter()
    {
        return viewContainer.getAdapter();
    }

    protected boolean hasFixedSize()
    {
        return true;
    }
}
