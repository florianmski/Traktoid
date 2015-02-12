package com.florianmski.tracktoid.rx.observables;

import rx.Observable;
import rx.functions.Func1;

public class TraktFallbackTransformer<T> implements Observable.Transformer<T, T>
{
    private TraktObservable<T> traktObservable;

    public TraktFallbackTransformer(TraktObservable<T> traktObservable)
    {
        this.traktObservable = traktObservable;
    }

    @Override
    public Observable<T> call(Observable<T> tObservable)
    {
        return tObservable.flatMap(new Func1<T, Observable<T>>()
        {
            @Override
            public Observable<T> call(T t)
            {
                if(t == null)
                    return Observable.create(traktObservable);
                else
                    return Observable.just(t);
            }
        });
    }
}
