package com.florianmski.tracktoid.utils;

import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ScrollView;

import com.florianmski.tracktoid.ui.fragments.base.list.ScrollListenerProvider;
import com.florianmski.tracktoid.ui.widgets.NotifyingScrollView;
import com.florianmski.tracktoid.ui.widgets.NotifyingWebView;

import java.util.ArrayList;
import java.util.List;

public class ScrollHeaderHelper
{
    private View header;
    private int heightToKeep = 0;
    private List<OnHeaderListener> listeners = new ArrayList<OnHeaderListener>();
    private boolean headerLayout = false;
    private float latestTranslationY = 0;

    private int activeScrollView = -1;

    public ScrollHeaderHelper(View header)
    {
        this.header = header;

        header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                ScrollHeaderHelper.this.header.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                headerLayout = true;
                dispatchOnHeaderLayoutEvent();
            }
        });
    }

    private void translateHeader(int key, View v, int top)
    {
        float translationY = Math.max(heightToKeep - v.getPaddingTop(), top - v.getPaddingTop());
        header.setTranslationY(translationY);
        dispatchOnHeaderTranslateEvent(key, translationY);
        latestTranslationY = translationY;
    }

    public void setHeightToKeep(int heightToKeep)
    {
        this.heightToKeep = heightToKeep;
    }

    public int getHeightToKeep()
    {
        return heightToKeep;
    }

    public View getHeader()
    {
        return header;
    }

    public void addHeaderListener(OnHeaderListener listener)
    {
        listeners.add(listener);
        if(headerLayout)
            listener.onHeaderLayout();
    }

    public void removeHeaderListener(OnHeaderListener listener)
    {
        listeners.remove(listener);
    }

    private void dispatchOnHeaderLayoutEvent()
    {
        for(OnHeaderListener listener : listeners)
            listener.onHeaderLayout();
    }

    private void dispatchOnHeaderTranslateEvent(int key, float translationY)
    {
        for(OnHeaderListener listener : listeners)
            listener.onHeaderTranslate(key, translationY);
    }

    private SparseArrayCompat<ScrollViewContainer<?, ?>> map = new SparseArrayCompat<ScrollViewContainer<?, ?>>();

    private void addScrollableView(int key, NotifyingScrollView scrollView, ScrollListenerProvider<NotifyingScrollView.OnScrollChangedListener> scrollListenerProvider)
    {
        map.put(key, new NotifyingScrollViewContainer(key, scrollView, scrollListenerProvider));
    }

    private void addScrollableView(int key, NotifyingWebView scrollView, ScrollListenerProvider<NotifyingWebView.OnScrollChangedListener> scrollListenerProvider)
    {
        map.put(key, new NotifyingWebViewContainer(key, scrollView, scrollListenerProvider));
    }

    private void addScrollableView(int key, AbsListView scrollView, ScrollListenerProvider<AbsListView.OnScrollListener> scrollListenerProvider)
    {
        map.put(key, new AbsListViewContainer(key, scrollView, scrollListenerProvider));
    }

    private void addScrollableView(int key, RecyclerView recyclerView, ScrollListenerProvider<RecyclerView.OnScrollListener> scrollListenerProvider)
    {
        map.put(key, new RecyclerViewContainer(key, recyclerView, scrollListenerProvider));
    }

    private void removeScrollableView(int key)
    {
        ScrollViewContainer<?, ?> container = map.get(key);
        if(container != null)
        {
            container.clean();
            map.remove(key);
        }
    }

    public static void addScrollableView(Fragment f, int key, NotifyingScrollView scrollView, ScrollListenerProvider<NotifyingScrollView.OnScrollChangedListener> scrollListenerProvider)
    {
        ScrollHeaderHelper shh = from(f);
        if(shh != null)
            shh.addScrollableView(key, scrollView, scrollListenerProvider);
    }

    public static void addScrollableView(Fragment f, int key, NotifyingWebView scrollView, ScrollListenerProvider<NotifyingWebView.OnScrollChangedListener> scrollListenerProvider)
    {
        ScrollHeaderHelper shh = from(f);
        if(shh != null)
            shh.addScrollableView(key, scrollView, scrollListenerProvider);
    }

    public static void addScrollableView(Fragment f, int key, AbsListView scrollView, ScrollListenerProvider<AbsListView.OnScrollListener> scrollListenerProvider)
    {
        ScrollHeaderHelper shh = from(f);
        if(shh != null)
            shh.addScrollableView(key, scrollView, scrollListenerProvider);
    }

    public static void addScrollableView(Fragment f, int key, RecyclerView scrollView, ScrollListenerProvider<RecyclerView.OnScrollListener> scrollListenerProvider)
    {
        ScrollHeaderHelper shh = from(f);
        if(shh != null)
            shh.addScrollableView(key, scrollView, scrollListenerProvider);
    }

    public static void removeScrollableView(Fragment f, int key)
    {
        ScrollHeaderHelper shh = from(f);
        if(shh != null)
            shh.removeScrollableView(key);
    }

    public static ScrollHeaderHelper from(Fragment f)
    {
        Fragment parentFragment = f.getParentFragment();
        if(parentFragment != null && parentFragment instanceof ScrollHeaderHelper.Provider)
            return ((ScrollHeaderHelper.Provider)parentFragment).getScrollHeaderHelper();

        return null;
    }

    public void setActiveScrollView(int key)
    {
        this.activeScrollView = key;
    }

    public abstract class ScrollViewContainer<T extends ViewGroup, S> implements OnHeaderListener
    {
        protected int key;
        protected T scrollView;
        protected ScrollListenerProvider<S> scrollListenerProvider;

        public ScrollViewContainer(int key, T scrollView, ScrollListenerProvider<S> scrollListenerProvider)
        {
            this.key = key;
            this.scrollView = scrollView;
            this.scrollListenerProvider = scrollListenerProvider;
        }

        @Override
        public void onHeaderLayout()
        {
            scrollView.setClipToPadding(false);
            scrollView.setPadding(scrollView.getPaddingLeft(), header.getHeight(), scrollView.getPaddingRight(), scrollView.getPaddingBottom());
            scroll(header.getTranslationY());
            scrollListenerProvider.addScrollListener(getScrollListener());

//            scrollView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener()
//            {
//                @Override
//                public void onChildViewAdded(View parent, View child)
//                {
//                    setPaddingBottom();
//                }
//
//                @Override
//                public void onChildViewRemoved(View parent, View child)
//                {
//                    setPaddingBottom();
//                }
//            });
        }

        private void setPaddingBottom()
        {
            if(scrollView.getChildCount() > 0)
            {
                // we would like to make it scroll height of content - size of the scrollview - heightToKeep
                int contentHeight = scrollView.getChildAt(0).getHeight() * Math.round((scrollView.getChildCount() * 1f) / ((scrollView instanceof GridView) ? ((GridView)scrollView).getNumColumns() : 1));
                int paddingBottom = scrollView.getBottom() - contentHeight  - heightToKeep;
//                if(paddingBottom > (header.getHeight() - heightToKeep))
                scrollView.setPadding(0, scrollView.getPaddingTop(), 0, paddingBottom);
            }
        }

        protected void clean()
        {
            scrollListenerProvider.removeScrollListener(getScrollListener());
        }

        protected abstract void scroll(float translationY);
        protected abstract S getScrollListener();
    }

    public class AbsListViewContainer extends ScrollViewContainer<AbsListView, AbsListView.OnScrollListener>
    {
        private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if(key != activeScrollView || visibleItemCount == 0 || firstVisibleItem != 0)
                    return;

                int top = view.getChildAt(0).getTop();
                translateHeader(key, view, top);
            }
        };

        public AbsListViewContainer(int key, AbsListView scrollView, ScrollListenerProvider<AbsListView.OnScrollListener> scrollListenerProvider)
        {
            super(key, scrollView, scrollListenerProvider);
            addHeaderListener(this);
        }

        @Override
        protected void scroll(float translationY)
        {
            if(scrollView.getTranslationY() != 0)
                scrollView.setTranslationY(0);

            // this is the only trick I've found to make it work
            // the commented line doesn't work great, it make the listview jump to an old position...
            scrollView.smoothScrollToPositionFromTop(0, (int) translationY, 0);
//                scrollView.scrollBy(0, (int) (latestTranslationY - translationY));
        }

        @Override
        protected AbsListView.OnScrollListener getScrollListener()
        {
            return scrollListener;
        }

        @Override
        public void onHeaderTranslate(int key, float translationY)
        {
            if(key != this.key)
                scroll(translationY);
        }

        public void clean()
        {
            super.clean();
            removeHeaderListener(this);
        }
    }

    public class NotifyingScrollViewContainer extends ScrollViewContainer<NotifyingScrollView, NotifyingScrollView.OnScrollChangedListener>
    {
        private NotifyingScrollView.OnScrollChangedListener scrollListener = new NotifyingScrollView.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt)
            {
                if(key != activeScrollView || who.getChildCount() == 0)
                    return;

                translateHeader(key, who, who.getChildAt(0).getTop() - t);
            }
        };

        public NotifyingScrollViewContainer(int key, NotifyingScrollView scrollView, ScrollListenerProvider<NotifyingScrollView.OnScrollChangedListener> scrollListenerProvider)
        {
            super(key, scrollView, scrollListenerProvider);
            addHeaderListener(this);
        }

        @Override
        protected void scroll(float translationY)
        {
            if(scrollView.getTranslationY() != 0)
                scrollView.setTranslationY(0);
            scrollView.scrollBy(0, (int) (latestTranslationY - translationY));
        }

        @Override
        protected NotifyingScrollView.OnScrollChangedListener getScrollListener()
        {
            return scrollListener;
        }

        @Override
        public void onHeaderTranslate(int key, float translationY)
        {
            if(key != this.key)
            {
                //                Log.e("test", "scroll ScrollView with key : " + key + " by " + (latestTranslationY - translationY));
                scroll(translationY);
            }
        }

        public void clean()
        {
            super.clean();
            removeHeaderListener(this);
        }
    }

    public class NotifyingWebViewContainer extends ScrollViewContainer<NotifyingWebView, NotifyingWebView.OnScrollChangedListener>
{
    private NotifyingWebView.OnScrollChangedListener scrollListener = new NotifyingWebView.OnScrollChangedListener()
    {
        @Override
        public void onScrollChanged(NotifyingWebView who, int l, int t, int oldl, int oldt)
        {
            if(key != activeScrollView || who.getChildCount() == 0)
                return;

            translateHeader(key, who, who.getChildAt(0).getTop() - t);
        }
    };

    public NotifyingWebViewContainer(int key, NotifyingWebView scrollView, ScrollListenerProvider<NotifyingWebView.OnScrollChangedListener> scrollListenerProvider)
    {
        super(key, scrollView, scrollListenerProvider);
        addHeaderListener(this);
    }

    @Override
    protected void scroll(float translationY)
    {
        if(scrollView.getTranslationY() != 0)
            scrollView.setTranslationY(0);
        scrollView.scrollBy(0, (int) (latestTranslationY - translationY));
    }

    @Override
    protected NotifyingWebView.OnScrollChangedListener getScrollListener()
    {
        return scrollListener;
    }

    @Override
    public void onHeaderTranslate(int key, float translationY)
    {
        if(key != this.key)
        {
            //                Log.e("test", "scroll ScrollView with key : " + key + " by " + (latestTranslationY - translationY));
            scroll(translationY);
        }
    }

    public void clean()
    {
        super.clean();
        removeHeaderListener(this);
    }
}

    public class RecyclerViewContainer extends ScrollViewContainer<RecyclerView, RecyclerView.OnScrollListener>
    {
        private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(key != activeScrollView || recyclerView.getChildCount() == 0)
                    return;

                translateHeader(key, recyclerView, recyclerView.getChildAt(0).getTop() - dy);
            }
        };

        public RecyclerViewContainer(int key, RecyclerView recyclerView, ScrollListenerProvider<RecyclerView.OnScrollListener> scrollListenerProvider)
        {
            super(key, recyclerView, scrollListenerProvider);
            addHeaderListener(this);
        }

        @Override
        protected void scroll(float translationY)
        {
            if(scrollView.getTranslationY() != 0)
                scrollView.setTranslationY(0);
            scrollView.scrollBy(0, (int) (latestTranslationY - translationY));
        }

        @Override
        protected RecyclerView.OnScrollListener getScrollListener()
        {
            return scrollListener;
        }

        @Override
        public void onHeaderTranslate(int key, float translationY)
        {
            if(key != this.key)
            {
                //                Log.e("test", "scroll ScrollView with key : " + key + " by " + (latestTranslationY - translationY));
                scroll(translationY);
            }
        }

        public void clean()
        {
            super.clean();
            removeHeaderListener(this);
        }
    }

    public interface OnHeaderListener
    {
        public void onHeaderLayout();
        public void onHeaderTranslate(int key, float translationY);
    }

    public interface Provider
    {
        public ScrollHeaderHelper getScrollHeaderHelper();
    }
}
