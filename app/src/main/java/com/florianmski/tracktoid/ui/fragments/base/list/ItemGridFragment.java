package com.florianmski.tracktoid.ui.fragments.base.list;

import android.widget.GridView;
import com.florianmski.tracktoid.containers.ViewContainer;

public abstract class ItemGridFragment<E> extends ItemAbsListFragment<E, GridView>
{
    public ItemGridFragment()
    {
        super(new ViewContainer.AbsListViewContainer.GridViewContainer<E>());
    }
}
