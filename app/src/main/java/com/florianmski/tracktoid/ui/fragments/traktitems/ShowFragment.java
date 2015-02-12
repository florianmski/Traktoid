package com.florianmski.tracktoid.ui.fragments.traktitems;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.utils.DbHelper;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.utils.Utils;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.adapters.lists.RecyclerTraktItemAdapter;
import com.florianmski.tracktoid.data.WEpisode;
import com.florianmski.tracktoid.data.WSeason;
import com.florianmski.tracktoid.data.WShow;
import com.florianmski.tracktoid.data.database.ProviderSchematic;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.SeasonColumns;
import com.florianmski.tracktoid.data.database.utils.CVUtils;
import com.florianmski.tracktoid.image.ImagesTest;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.rx.observables.TraktFallbackTransformer;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.TraktSender;
import com.florianmski.tracktoid.ui.activities.CommentsActivity;
import com.florianmski.tracktoid.ui.activities.EpisodeActivity;
import com.florianmski.tracktoid.ui.activities.PagerSeasonActivity;
import com.florianmski.tracktoid.ui.widgets.FlagsView;
import com.florianmski.tracktoid.ui.widgets.Placeholder;
import com.florianmski.tracktoid.ui.widgets.TraktActionView;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.trakt.v2.entities.Airs;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.Season;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShowFragment extends MediaBaseFragment<WShow>
{
    private FrameLayout flNextEpisode;
    private TraktImageView tivNextEpisode;
    private FlagsView fvNextEpisode;
    private RecyclerView rvSeasons;

    private final Observer<WEpisode> observerNextEpisode = new Observer<WEpisode>()
    {
        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable throwable)
        {
            throwable.printStackTrace();
        }

        @Override
        public void onNext(final WEpisode wEpisode)
        {
            if(wEpisode == null)
                flNextEpisode.setVisibility(View.GONE);
            else
            {
                flNextEpisode.setVisibility(View.VISIBLE);
                final Episode e = wEpisode.getTraktItem();
                fvNextEpisode.init(new FlagsView.Flags.Builder(wEpisode.getTraktBase())
                        .title("Next episode")
                        .subtitle(Utils.getSeasonEpisodeString(e.season, e.number))
                        .theme(getTheme()).build());
                Picasso.with(getActivity())
                        .load(ImagesTest.getUrl(Type.SCREENSHOT, e.images))
                        .placeholder(new Placeholder(getActivity(), TraktoidTheme.SHOW).getDrawable())
                        .into(tivNextEpisode);
                flNextEpisode.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        EpisodeActivity.launch(getActivity(), wEpisode);
                    }
                });
            }
        }
    };

    private RecyclerAdapter.OnItemClickListener seasonsListener = new RecyclerAdapter.OnItemClickListener()
    {
        @Override
        public void onItemClick(View v, int position)
        {
            RecyclerTraktItemAdapter<WSeason> adapter = (RecyclerTraktItemAdapter<WSeason>)rvSeasons.getAdapter();
            int length = adapter.getItemCount();
            String seasonIds[] = new String[length];
            int seasons[] = new int[length];
            for(int i = 0; i < length; i++)
            {
                // I want the natural order so I reverse the reverse order (clear enough?)
                WSeason season = adapter.getItem2((length-1) - i);
                seasonIds[i] = String.valueOf(season.getIds().trakt);
                seasons[i] = season.getTraktItem().season.number;
            }

            PagerSeasonActivity.launch(getActivity(), getActionBar().getTitle(), id, seasonIds, seasons, (length-1) - position);
        }
    };
    private final Observer<List<WSeason>> observerSeasons = new Observer<List<WSeason>>()
    {
        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable throwable) {}

        @Override
        public void onNext(final List<WSeason> wSeasons)
        {
            if(wSeasons == null)
                rvSeasons.setVisibility(View.GONE);
            else
            {
                rvSeasons.setVisibility(View.VISIBLE);

                RecyclerTraktItemAdapter adapter = new RecyclerTraktItemAdapter<>(getActivity(), wSeasons, getTheme(), seasonsListener)
                        .imageType(Type.POSTER)
                        .titleVisible(true);
                rvSeasons.setAdapter(adapter);
            }
        }
    };

    public ShowFragment() {}

    public static ShowFragment newInstance(String id)
    {
        ShowFragment f = new ShowFragment();
        Bundle args = getBundle(id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Subscription subscriptionNextEpisode =
                createNextEpisodeObservable()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerNextEpisode);
        subscriptions.add(subscriptionNextEpisode);

        Subscription subscriptionSeasons =
                createSeasonsObservable()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerSeasons);
        subscriptions.add(subscriptionSeasons);

        tavCollection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createDialog("collected", v);
            }
        });

        tavSeen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createDialog("watched", v);
            }
        });
    }

    private void createDialog(String action, final View v)
    {
        boolean marked = ((TraktActionView)v).isChecked();
        String futurStatus = String.format("%smark", (marked ? "un" : ""));
        String message = String.format("Do you really want to %s as %s all the aired episodes of this show?", futurStatus, action);
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ((TraktActionView)v).toggle();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {}
                })
                .create()
                .show();
    }

    @Override
    public TraktSender.Builder addItemToBuilder(TraktSender.Builder builder)
    {
        return builder.show(item.getTraktItem());
    }

    @Override
    public Observable<WShow> getDownloadAndInsertItemObservable()
    {
        return Observable.create(new TraktObservable<WShow>()
        {
            @Override
            public WShow fire() throws OAuthUnauthorizedException
            {
                DbHelper.downloadAndInsertShowContent(getActivity(), item.getTraktItem());
                return item;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        flNextEpisode = (FrameLayout) view.findViewById(R.id.frameLayoutNextEpisode);
        tivNextEpisode = (TraktImageView) flNextEpisode.findViewById(R.id.traktImageViewNextEpisode);
        fvNextEpisode = (FlagsView) flNextEpisode.findViewById(R.id.flagsViewNextEpisode);

        rvSeasons = (RecyclerView) view.findViewById(R.id.recyclerViewSeasons);
        rvSeasons.setHasFixedSize(true);
        rvSeasons.setLayoutManager(new GridLayoutManager(getActivity(), 1, OrientationHelper.HORIZONTAL, false));
    }

    private Observable<WEpisode> createNextEpisodeObservable()
    {
        return Observable.create(new CursorObservable<WEpisode>(
                getActivity(),
                ProviderSchematic.Episodes.fromShow(id),
                null,
                EpisodeColumns.WATCHED + "=?" + " AND " + EpisodeColumns.SEASON + "!=?",
                new String[]{String.valueOf(0), String.valueOf(0)},
                EpisodeColumns.SEASON + ", " + EpisodeColumns.NUMBER + " LIMIT 1")
        {
            @Override
            protected WEpisode toObject(Cursor cursor)
            {
                return WEpisode.unpack(cursor);
            }
        });
    }

    private Observable<List<WSeason>> createSeasonsObservable()
    {
        return Observable.create(new CursorObservable<List<WSeason>>(
                getActivity(),
                ProviderSchematic.Seasons.fromShow(id),
                ProviderSchematic.Seasons.PROJECTION,
                null,
                null,
                SeasonColumns.NUMBER + " DESC")
        {
            @Override
            protected List<WSeason> toObject(Cursor cursor)
            {
                return WSeason.unpackList(cursor);
            }
        }).compose(new TraktFallbackTransformer<>(new TraktObservable<List<WSeason>>()
        {
            @Override
            public List<WSeason> fire() throws OAuthUnauthorizedException
            {
                List<Season> seasons = TraktManager.getInstance().seasons().summary(id, Extended.FULLIMAGES);
                List<WSeason> wSeasons = new ArrayList<>();
                for(Season season : seasons)
                    wSeasons.add(new WSeason(new CVUtils.SeasonCV.SeasonEntity(season)));
                Collections.reverse(wSeasons);
                return wSeasons;
            }
        }));
    }

    @Override
    public void refreshView(WShow item)
    {
        this.item = item;

        tlInfos.removeAllViews();
        DateTime firstAired = item.getTraktItem().first_aired;
        if(firstAired != null)
            addInfo("Premiered", DateHelper.getDate(getActivity(), firstAired));
        String country = item.getTraktItem().country;
        if(country != null)
            addInfo("Country", country.toUpperCase());
        Integer runtime = item.getTraktItem().runtime;
        if(runtime != null)
            addInfo("Runtime", DateHelper.getRuntime(runtime));
        Double rating = item.getTraktItem().rating;
        if(rating != null && item.getTraktItem().votes > 0)
            addInfo("Ratings", String.format("%.01f/10", rating));

        refreshGeneralView(item.getTraktBase());

        setTitle(item.getTraktItem().title);
        setSubtitle(String.valueOf(item.getTraktItem().year));
    }

    @Override
    public String getDateText(boolean invalidTime)
    {
        Airs airs = item.getTraktItem().airs;
        String unknownAirTime = "Unknown air time";

        if(invalidTime || airs == null)
            return unknownAirTime;
        else
        {
            StringBuilder builder = new StringBuilder();
            if(airs.day != null)
                builder.append(airs.day).append(" ");
            if(airs.time != null)
                builder.append("at ").append(airs.time).append(" ");
            if(item.getTraktItem().network != null)
                builder.append("on ").append(item.getTraktItem().network);

            String res = builder.toString();
            if(TextUtils.isEmpty(res))
                return unknownAirTime;

            // capitalize first letter
            return res.substring(0, 1).toUpperCase() + res.substring(1);
        }
    }

    @Override
    public WShow getTraktObject()
    {
        Show show = TraktManager.getInstance().shows().summary(id, Extended.FULLIMAGES);
        return new WShow(show);
    }

    @Override
    public CursorObservable<WShow> getCursorObservable()
    {
        return new CursorObservable<WShow>(
                getActivity(),
                ProviderSchematic.Shows.withId(id),
                ProviderSchematic.Shows.PROJECTION,
                null, null, null)
        {
            @Override
            protected WShow toObject(Cursor cursor)
            {
                return WShow.unpack(cursor);
            }
        };
    }

    @Override
    public void launchCommentActivity()
    {
        CommentsActivity.launchShow(getActivity(), id);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.SHOW;
    }
}
