package com.florianmski.tracktoid.ui.fragments.base.list;

import android.widget.ListView;
import com.florianmski.tracktoid.containers.ViewContainer;

public abstract class ItemListFragment<E> extends ItemAbsListFragment<E, ListView>
{
    public ItemListFragment()
    {
        super(new ViewContainer.AbsListViewContainer.ListViewContainer<E>());
    }
}
