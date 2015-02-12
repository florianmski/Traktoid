package com.florianmski.tracktoid.ui.fragments.base.list;

public interface ScrollListenerProvider<S>
{
    public void addScrollListener(S listener);
    public void removeScrollListener(S listener);
}
