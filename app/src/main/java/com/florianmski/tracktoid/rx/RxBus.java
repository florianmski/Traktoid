package com.florianmski.tracktoid.rx;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus
{
    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o)
    {
        bus.onNext(o);
    }

    public Observable<Object> toObservable()
    {
        return bus;
    }
} 