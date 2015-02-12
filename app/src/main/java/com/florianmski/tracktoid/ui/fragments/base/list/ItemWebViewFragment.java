package com.florianmski.tracktoid.ui.fragments.base.list;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.florianmski.tracktoid.adapters.AbstractAdapter;
import com.florianmski.tracktoid.containers.ViewContainer;
import com.florianmski.tracktoid.ui.widgets.NotifyingWebView;

public abstract class ItemWebViewFragment<E> extends ItemScrollFragment<E, E, NotifyingWebView, NotifyingWebView.OnScrollChangedListener, AbstractAdapter<E>> implements NotifyingWebView.OnScrollChangedListener
{
    private NotifyingWebView.OnScrollChangedListener onScrollListener = new NotifyingWebView.OnScrollChangedListener()
    {
        @Override
        public void onScrollChanged(NotifyingWebView who, int l, int t, int oldl, int oldt)
        {
            for(NotifyingWebView.OnScrollChangedListener scrollListener : scrollListeners)
                scrollListener.onScrollChanged(who, l, t, oldl, oldt);

            int delta = oldt - t;

            if((getToolbar().getHeight() - t) < 0 && delta < 0)
                showActionBar(false);
            else if(delta > 0)
                showActionBar(true);
        }
    };

    public ItemWebViewFragment()
    {
        super(new ViewContainer.WebViewContainer<E, NotifyingWebView>());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getGroupView().setOnScrollChangedListener(onScrollListener);

        getGroupView().setWebViewClient(getWebViewClient());
//        getGroupView().setWebChromeClient(new WebChromeClient()
//        {
//            public void onProgressChanged(WebView view, int progress)
//            {
//                // TODO maybe add some kind of progress
////                ((ActionBarActivity)getActivity()).setSupportProgressBarVisibility(progress > 0 && progress < 100);
////                ((ActionBarActivity)getActivity()).setSupportProgress(progress);
//            }
//        });
    }

    protected RefreshWebViewClient getWebViewClient()
    {
        return new RefreshWebViewClient();
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

    @Override
    public void onScrollChanged(NotifyingWebView who, int l, int t, int oldl, int oldt)
    {
        for(NotifyingWebView.OnScrollChangedListener listener : scrollListeners)
            listener.onScrollChanged(who, l, t, oldl, oldt);
    }

    public class RefreshWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);

            //            showProgressBar(null);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);

            //            showView();
        }
    }
}
