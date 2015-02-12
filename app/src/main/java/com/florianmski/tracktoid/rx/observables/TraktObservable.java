package com.florianmski.tracktoid.rx.observables;

import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import rx.Observable;
import rx.Subscriber;

public abstract class TraktObservable<T> implements Observable.OnSubscribe<T>
{
    public abstract T fire() throws OAuthUnauthorizedException;

    @Override
    public void call(Subscriber<? super T> subscriber)
    {
        try
        {
            T data = fire();

            subscriber.onNext(data);
            subscriber.onCompleted();
        }
        catch (Exception e)
        {
            subscriber.onError(e);
        }
    }
}
