package com.florianmski.tracktoid.ui.fragments.traktitems;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.WEpisode;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.trakt.CheckinManager;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.TraktSender;
import com.florianmski.tracktoid.ui.activities.CommentsActivity;
import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.utils.DbHelper;
import com.florianmski.tracktoid.utils.Utils;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.EpisodeCheckinResponse;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import rx.Observable;
import rx.functions.Func1;

public class EpisodeFragment extends TraktItemFragment<WEpisode, EpisodeCheckinResponse>
{
    private String showId;
    private int season;
    private int episode;

    public EpisodeFragment() {}

    public static EpisodeFragment newInstance(String showId, int season, String id, int episode)
    {
        EpisodeFragment f = new EpisodeFragment();
        Bundle args = getBundle(id);
        args.putString(TraktoidConstants.BUNDLE_SHOW_ID, showId);
        args.putInt(TraktoidConstants.BUNDLE_SEASON, season);
        args.putInt(TraktoidConstants.BUNDLE_EPISODE, episode);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        showId = getArguments().getString(TraktoidConstants.BUNDLE_SHOW_ID);
        season = getArguments().getInt(TraktoidConstants.BUNDLE_SEASON);
        episode = getArguments().getInt(TraktoidConstants.BUNDLE_EPISODE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public TraktSender.Builder addItemToBuilder(TraktSender.Builder builder)
    {
        return builder.episode(item.getTraktItem());
    }

    @Override
    public Observable<WEpisode> getDownloadAndInsertItemObservable()
    {
        return Observable.create(new TraktObservable<WEpisode>()
        {
            @Override
            public WEpisode fire() throws OAuthUnauthorizedException
            {
                if(!DbHelper.isShowInDb(getActivity(), showId))
                    DbHelper.downloadAndInsertShow(getActivity(), showId);
                else
                {
                    // TODO this should not arrive
                    // if it does arrive, we can't insert the episode because we don't have the season
                    throw new IllegalArgumentException();
                }

                return item;
            }
        });
    }

    @Override
    public Observable<EpisodeCheckinResponse> getCheckinObservable()
    {
        final Episode episode = item.getTraktItem();
        return Observable.create(new CursorObservable<WShow>(
                        getActivity(),
                        ProviderSchematic.Shows.withId(item.showId),
                        ProviderSchematic.Shows.PROJECTION,
                        null, null, null)
        {
            @Override
            protected WShow toObject(Cursor cursor)
            {
                return WShow.unpack(cursor);
            }
        }).flatMap(new Func1<WShow, Observable<EpisodeCheckinResponse>>()
        {
            @Override
            public Observable<EpisodeCheckinResponse> call(WShow wShow)
            {
                Show show = wShow.getTraktItem();
                String title = String.format("%s %s", show.title, Utils.getSeasonEpisodeString(episode.season, episode.number));
                return CheckinManager.getInstance().checkinEpisode(getActivity(), episode.ids.trakt, title, show.runtime);
            }
        });
    }

    @Override
    public void refreshView(WEpisode item)
    {
        this.item = item;

        tlInfos.removeAllViews();
        addInfo("Aired", DateHelper.getDate(getActivity(), item.getTraktItem().first_aired));
        Double rating = item.getTraktItem().rating;
        if(rating != null && item.getTraktItem().votes > 0)
            addInfo("Ratings", String.format("%.01f/10", rating));

        refreshGeneralView(item.getTraktBase());

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
            return "Unknown air date";
        else
        {
            WEpisode episode = item;
            return DateHelper.getDate(getActivity(), episode.getTraktItem().first_aired);
        }
    }

    @Override
    public WEpisode getTraktObject()
    {
        Episode e = TraktManager.getInstance()
                .episodes()
                .summary(showId, season, episode, Extended.FULLIMAGES);

        return new WEpisode(e, showId);
    }

    @Override
    public CursorObservable<WEpisode> getCursorObservable()
    {
        return new CursorObservable<WEpisode>(
                getActivity(),
                ProviderSchematic.Episodes.withId(id),
                ProviderSchematic.Episodes.PROJECTION,
                null, null, null)
        {
            @Override
            protected WEpisode toObject(Cursor cursor)
            {
                return WEpisode.unpack(cursor);
            }
        };
    }

    @Override
    public void launchCommentActivity()
    {
        CommentsActivity.launchEpisode(getActivity(), showId, season, episode);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}
