package com.florianmski.tracktoid.ui.fragments.season;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.adapters.lists.RecyclerTraktItemAdapter;
import com.florianmski.tracktoid.data.WEpisode;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.rx.observables.TraktFallbackTransformer;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.ui.activities.EpisodeActivity;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemRecyclerViewFragment;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class SeasonFragment extends ItemRecyclerViewFragment<WEpisode> implements RecyclerAdapter.OnItemClickListener
{
    private String showId;
    private String seasonId;
    private int season;

    public static SeasonFragment newInstance(String showId, String seasonId, int season)
    {
        SeasonFragment f = new SeasonFragment();
        Bundle args = new Bundle();
        args.putString(TraktoidConstants.BUNDLE_SHOW_ID, showId);
        args.putString(TraktoidConstants.BUNDLE_ID, seasonId);
        args.putInt(TraktoidConstants.BUNDLE_SEASON, season);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null)
        {
            showId = getArguments().getString(TraktoidConstants.BUNDLE_SHOW_ID);
            seasonId = getArguments().getString(TraktoidConstants.BUNDLE_ID);
            season = getArguments().getInt(TraktoidConstants.BUNDLE_SEASON);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected Observable<List<WEpisode>> createObservable()
    {
        return Observable.create(new CursorObservable<List<WEpisode>>(
                getActivity(),
                ProviderSchematic.Episodes.fromSeason(seasonId),
                null, null, null,
                EpisodeColumns.NUMBER + " ASC")
        {
            @Override
            protected List<WEpisode> toObject(Cursor cursor)
            {
                return WEpisode.unpackList(cursor);
            }
        }).compose(new TraktFallbackTransformer<>(new TraktObservable<List<WEpisode>>()
        {
            @Override
            public List<WEpisode> fire() throws OAuthUnauthorizedException
            {
                List<Episode> episodes = TraktManager.getInstance().seasons().season(String.valueOf(showId), season, Extended.FULLIMAGES);
                List<WEpisode> wEpisodes = new ArrayList<>();
                for (Episode episode : episodes)
                    wEpisodes.add(new WEpisode(episode, showId));
                return wEpisodes;
            }
        }));
    }

    @Override
    protected RecyclerAdapter<WEpisode, ?> createAdapter(List<WEpisode> cursor)
    {
        return new RecyclerTraktItemAdapter<>(getActivity(), cursor, getTheme(), this).imageType(Type.SCREENSHOT).titleVisible(true);
    }

    @Override
    public void onItemClick(View v, int position)
    {
        RecyclerAdapter<WEpisode, ?> adapter = getAdapter();
        WEpisode wEpisode = adapter.getItem2(position);
        EpisodeActivity.launch(getActivity(), wEpisode);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager()
    {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_fanart_columns));
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}