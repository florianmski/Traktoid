package com.florianmski.tracktoid.ui.fragments.traktitems;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WMovie;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.trakt.CheckinManager;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.TraktSender;
import com.florianmski.tracktoid.ui.activities.CommentsActivity;
import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.utils.DbHelper;
import com.uwetrottmann.trakt.v2.entities.Movie;
import com.uwetrottmann.trakt.v2.entities.MovieCheckinResponse;
import com.uwetrottmann.trakt.v2.enums.Extended;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MovieFragment extends MediaBaseFragment<WMovie, MovieCheckinResponse>
{
    private TextView tvTagline;

    public MovieFragment() {}

    public static MovieFragment newInstance(String id)
    {
        MovieFragment f = new MovieFragment();
        Bundle args = getBundle(id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tvTagline = (TextView) view.findViewById(R.id.textViewTagline);
    }

    @Override
    public void launchCommentActivity()
    {
        CommentsActivity.launchMovie(getActivity(), id);
    }

    @Override
    public TraktSender.Builder addItemToBuilder(TraktSender.Builder builder)
    {
        return builder.movie(item.getTraktItem());
    }

    @Override
    public Observable<WMovie> getDownloadAndInsertItemObservable()
    {
        return Observable.just(item).doOnNext(new Action1<WMovie>()
        {
            @Override
            public void call(WMovie wMovie)
            {
                DbHelper.insertMovie(getActivity(), wMovie.getTraktItem());
            }
        });
    }

    @Override
    public Observable<MovieCheckinResponse> getCheckinObservable()
    {
        Movie movie = item.getTraktItem();
        final Observable<MovieCheckinResponse> observable = CheckinManager.getInstance().checkinMovie(getActivity(), movie.ids.trakt, movie.title, movie.runtime);
        Observable<MovieCheckinResponse> finalObservable = observable;

        if(!item.isLocal())
        {
            finalObservable = getDownloadAndInsertItemObservable().flatMap(new Func1<WMovie, Observable<MovieCheckinResponse>>()
            {
                @Override
                public Observable<MovieCheckinResponse> call(WMovie wMovie)
                {
                    return observable;
                }
            });
        }

        return finalObservable;
    }

    @Override
    public void refreshView(WMovie item)
    {
        this.item = item;
        final Movie movie = item.getTraktItem();

        setSubtitle(String.valueOf(movie.year));

        if(movie.tagline != null && !movie.tagline.isEmpty())
        {
            tvTagline.setVisibility(View.VISIBLE);
            tvTagline.setText("\"" + movie.tagline + "\"");
        }
        else
            tvTagline.setVisibility(View.GONE);

        tlInfos.removeAllViews();
        addInfo("Runtime", DateHelper.getRuntime(movie.runtime));
        addInfo("Released", DateHelper.getDate(getActivity(), movie.released));
        Double rating = item.getTraktItem().rating;
        if(rating != null && item.getTraktItem().votes > 0)
            addInfo("Ratings", String.format("%.01f/10", rating));
        addInfo("Certification", movie.certification);

        refreshGeneralView(item.getTraktBase());

        getActionBar().setTitle(movie.title);

        if(!CheckinManager.getInstance().checkinInProgress())
        {
            fab.setVisibility(View.VISIBLE);
            fab.show(true);
        }
    }

    @Override
    public String getDateText(boolean invalidTime)
    {
        if(invalidTime)
            return "Unknown release date";
        else
            return DateHelper.getDate(getActivity(), item.getTraktItem().released);
    }

    @Override
    public WMovie getTraktObject()
    {
        Movie movie = TraktManager.getInstance().movies().summary(id, Extended.FULLIMAGES);
        return new WMovie(movie);
    }

    @Override
    public CursorObservable<WMovie> getCursorObservable()
    {
        return new CursorObservable<WMovie>(
                getActivity(),
                ProviderSchematic.Movies.withId(id),
                ProviderSchematic.Movies.PROJECTION, null, null, null)
        {
            @Override
            protected WMovie toObject(Cursor cursor)
            {
                return WMovie.unpack(cursor);
            }
        };
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.MOVIE;
    }
}
