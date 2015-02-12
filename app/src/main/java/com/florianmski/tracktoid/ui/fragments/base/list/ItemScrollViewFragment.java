package com.florianmski.tracktoid.ui.fragments.base.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import com.florianmski.tracktoid.adapters.AbstractAdapter;
import com.florianmski.tracktoid.containers.ViewContainer;
import com.florianmski.tracktoid.ui.widgets.NotifyingScrollView;

public abstract class ItemScrollViewFragment<E> extends ItemScrollFragment<E, E, NotifyingScrollView, NotifyingScrollView.OnScrollChangedListener, AbstractAdapter<E>> implements NotifyingScrollView.OnScrollChangedListener
{
    public ItemScrollViewFragment()
    {
        super(new ViewContainer.ScrollViewContainer<E, NotifyingScrollView>());
    }

    protected abstract int getContentLayoutId();

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getGroupView().setOnScrollChangedListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater.from(getActivity()).inflate(getContentLayoutId(), getGroupView(), true);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected boolean isEmpty(E data)
    {
        return data == null;
    }

//    @Override
//    public void onInsetsChanged(Rect insets)
//    {
//        viewContainer.get().setClipToPadding(false);
//        viewContainer.get().setPadding(0, insets.top, 0, insets.bottom);
//    }

    @Override
    public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt)
    {
        for(NotifyingScrollView.OnScrollChangedListener listener : scrollListeners)
            listener.onScrollChanged(who, l, t, oldl, oldt);
    }
}
