package com.florianmski.tracktoid.containers;

import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.AbstractAdapter;
import com.florianmski.tracktoid.adapters.AdapterInterface;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;

public abstract class ViewContainer<E, V, A extends AdapterInterface<E>> extends Container<V> implements ContainerInterface.ViewContainerInterface<E, V, A>
{
    public static class RecyclerViewContainer<E> extends ViewContainer<E, RecyclerView, RecyclerAdapter<E, ?>>
    {
        @Override
        public void setAdapter(RecyclerAdapter<E, ?> adapter)
        {
            data.setAdapter(adapter);
        }

        @Override
        public RecyclerAdapter<E, ?> getAdapter()
        {
            return (RecyclerAdapter<E, ?>) data.getAdapter();
        }

        @Override
        public int getLayoutId()
        {
            return R.layout.view_recycler;
        }
    }

    public static abstract class AbsListViewContainer<E, V extends AbsListView> extends ViewContainer<E, V, AbstractAdapter<E>>
    {
        @Override
        public void setAdapter(AbstractAdapter<E> adapter)
        {
            data.setAdapter(adapter);
        }

        @Override
        public AbstractAdapter<E> getAdapter()
        {
            return (AbstractAdapter<E>) data.getAdapter();
        }

        public static class ListViewContainer<E> extends AbsListViewContainer<E, ListView>
        {
            @Override
            public int getLayoutId()
            {
                return R.layout.view_list;
            }
        }

        public static class GridViewContainer<E> extends AbsListViewContainer<E, GridView>
        {
            @Override
            public int getLayoutId()
            {
                return R.layout.view_grid;
            }
        }
    }

    public static class ScrollViewContainer<E, V extends ScrollView> extends ViewContainer<E, V, AbstractAdapter<E>>
    {
        // TODO something not really clean here, refactor so we don't have those two useless functions
        @Override
        public void setAdapter(AbstractAdapter<E> adapter)
        {
            // nothing to do here
        }

        @Override
        public AbstractAdapter<E> getAdapter()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLayoutId()
        {
            return R.layout.view_scrollview;
        }
    }

    public static class WebViewContainer<E, V extends WebView> extends ViewContainer<E, V, AbstractAdapter<E>>
    {
        // TODO something not really clean here, refactor so we don't have those two useless functions
        @Override
        public void setAdapter(AbstractAdapter<E> adapter)
        {
            // nothing to do here
        }

        @Override
        public AbstractAdapter<E> getAdapter()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLayoutId()
        {
            return R.layout.view_webview;
        }
    }

}
