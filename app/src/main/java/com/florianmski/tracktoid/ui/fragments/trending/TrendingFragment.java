package com.florianmski.tracktoid.ui.fragments.trending;

import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.ui.fragments.base.trakt.TraktItemsFragment;

import java.util.List;

import rx.Observable;

public abstract class TrendingFragment<E extends TraktoidItem> extends TraktItemsFragment<E>
{
	public abstract List<E> getTrending();

    @Override
    protected Observable<List<E>> createObservable()
    {
        return Observable.create(new TraktObservable<List<E>>()
        {
            @Override
            public List<E> fire()
            {
                return getTrending();
            }
        });
    }

    @Override
    protected Type getImageType()
    {
        return Type.FANART;
    }
}