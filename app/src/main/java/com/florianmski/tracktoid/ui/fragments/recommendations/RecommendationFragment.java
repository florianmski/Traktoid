package com.florianmski.tracktoid.ui.fragments.recommendations;

import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.ui.fragments.base.trakt.TraktItemsFragment;
import com.uwetrottmann.trakt.v2.entities.Genre;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import java.util.List;

import retrofit.client.Response;
import rx.Observable;

public abstract class RecommendationFragment<E extends TraktoidItem> extends TraktItemsFragment<E>
{
    public RecommendationFragment() {}

    public abstract Response fireDismiss(String id) throws OAuthUnauthorizedException;
    public abstract List<E> fireRecommendations() throws OAuthUnauthorizedException;
    public abstract List<Genre> fireGenres();

    @Override
    protected Observable<List<E>> createObservable()
    {
        return Observable.create(new TraktObservable<List<E>>()
        {
            @Override
            public List<E> fire() throws OAuthUnauthorizedException
            {
                return fireRecommendations();
            }
        });
    }

    @Override
    protected Type getImageType()
    {
        return Type.FANART;
    }
}
