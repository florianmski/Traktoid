package com.florianmski.tracktoid.ui.fragments.traktitems;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.data.TraktBase;
import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.rx.observables.CursorObservable;
import com.florianmski.tracktoid.rx.observables.TraktFallbackTransformer;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.trakt.TraktSender;
import com.florianmski.tracktoid.ui.activities.HeaderActivity;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemScrollViewFragment;
import com.florianmski.tracktoid.ui.widgets.ReadMoreView;
import com.florianmski.tracktoid.ui.widgets.TraktActionView;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;
import com.florianmski.tracktoid.utils.Utils;
import com.getbase.floatingactionbutton.FloatingActionButton2;
import com.uwetrottmann.trakt.v2.entities.BaseCheckinResponse;
import com.uwetrottmann.trakt.v2.entities.SyncResponse;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import java.util.ArrayList;
import java.util.Collection;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class TraktItemFragment<T extends TraktoidItem, C extends BaseCheckinResponse> extends ItemScrollViewFragment<T>
{
    // TODO declare it in values.xml
    private final static int NB_COLUMNS = 2;

    protected TraktImageView tivPoster;
    protected LinearLayout ll;
    protected LinearLayout llBasicInfos;

    protected TraktActionView tavSeen;
    protected TraktActionView tavCollection;
    protected TraktActionView tavWatchlist;

    protected ReadMoreView rmvInfos;
    protected TableLayout tlInfos;
    protected TextView tvOverview;
    protected TextView tvTitle;
    protected TextView tvSubtitle;
    protected TextView tvRate;
    protected TextView tvComments;

    protected FloatingActionButton2 fab;

    protected String id;
    protected T item;

    public abstract String getDateText(boolean invalidTime);
    public abstract T getTraktObject();
    public abstract CursorObservable<T> getCursorObservable();
    public abstract void launchCommentActivity();
    public abstract TraktSender.Builder addItemToBuilder(TraktSender.Builder builder);
    public abstract Observable<T> getDownloadAndInsertItemObservable();
    public abstract Observable<C> getCheckinObservable();

    public static Bundle getBundle(String id)
    {
        Bundle args = new Bundle();
        args.putString(TraktoidConstants.BUNDLE_ID, id);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        id = getArguments().getString(TraktoidConstants.BUNDLE_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        rmvInfos.makeItScroll(getGroupView());
        tvRate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO
            }
        });

        tvComments.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchCommentActivity();
            }
        });

        // setup the tavs
        setTavListener(tavSeen, new TraktSender.HistoryBuilder(getActivity()));
        setTavListener(tavCollection, new TraktSender.CollectionBuilder(getActivity()));
        setTavListener(tavWatchlist, new TraktSender.WatchlistBuilder(getActivity()));

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Observable<C> observable = getCheckinObservable();
                Observable<C> finalObservable;

                // make sur we get the item in local if we haven't before checkin
                if(!item.isLocal())
                {
                    finalObservable = getDownloadAndInsertItemObservable().flatMap(new Func1<T, Observable<C>>()
                    {
                        @Override
                        public Observable<C> call(T t)
                        {
                            return observable;
                        }
                    });
                }
                else
                    finalObservable = observable;

                // TODO would be great to display a loading indicator on the fab while the checkin request is made
                Subscription s = finalObservable
                        .subscribe(new Subscriber<BaseCheckinResponse>()
                        {
                            @Override
                            public void onCompleted() {}

                            @Override
                            public void onError(Throwable e)
                            {
                                // TODO
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(BaseCheckinResponse checkinResponse)
                            {
                                // TODO
                                Toast.makeText(getActivity(), "Checked in!", Toast.LENGTH_SHORT).show();

                                // sweet fab animation to the status bar
                                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(fab, View.SCALE_X, 0f);
                                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(fab, View.SCALE_Y, 0f);

                                AnimatorSet set = new AnimatorSet();
                                Collection<Animator> animators = new ArrayList<>();
                                animators.add(scaleXAnimator);
                                animators.add(scaleYAnimator);

                                // if it's not Lollipop or higher, don't do the curved path animation
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                {
                                    int height = getGroupView().getPaddingTop() - getGroupView().getScrollY();

                                    Path path = new Path();
                                    RectF r = new RectF(0f, 2 * (-height - fab.getHeight() / 2), 2 * fab.getX(), 2 * fab.getY());
                                    path.arcTo(r, 90, 90);

                                    ObjectAnimator pathAnimator = ObjectAnimator.ofFloat(fab, View.X, View.Y, path);
                                    animators.add(pathAnimator);
                                }

                                set.playTogether(animators);
                                set.setInterpolator(new AccelerateInterpolator());
                                set.setDuration(800);
                                set.start();
                            }
                        });

                subscriptions.add(s);
            }
        });
    }

    protected void setTavListener(final TraktActionView tav, final TraktSender.Builder builder)
    {
        tav.setOnTAVClickListener(new TraktActionView.OnTAVClickListener()
        {
            @Override
            public void onClick(boolean checked)
            {
                // make sure we don't add the same item multiple time if user tap several times the same tav
                builder.clear();
                final Observable<SyncResponse> sendAndUpdateDbObservable = addItemToBuilder(builder).getObservable(!checked);
                Observable<SyncResponse> finalObservable;

                if (!item.isLocal())
                    finalObservable = getDownloadAndInsertItemObservable()
                            .flatMap(new Func1<T, Observable<SyncResponse>>()
                            {
                                @Override
                                public Observable<SyncResponse> call(T item)
                                {
                                    return sendAndUpdateDbObservable;
                                }
                            });
                else
                    finalObservable = sendAndUpdateDbObservable;

                Subscription subscription = AndroidObservable
                        .bindFragment(TraktItemFragment.this, finalObservable.subscribeOn(Schedulers.io()))
                        .subscribe(new TAVObserver(tav));

                subscriptions.add(subscription);
            }
        });
    }

    public Observable<T> createObservable()
    {
        // trying to get the item from db and if it fails we don't have this item in db and
        // use the network
        return Observable
                .create(getCursorObservable())
                .compose(new TraktFallbackTransformer<>(new TraktObservable<T>()
                {
                    @Override
                    public T fire() throws OAuthUnauthorizedException
                    {
                        return getTraktObject();
                    }
                }));
    }

    @Override
    public int getContentLayoutId()
    {
        return R.layout.fragment_trakt_item;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // init the right visibility for the scrollView
        // the idea is to have the BG of the scrollView always visible but not the content
        getViewToHide().setVisibility(View.INVISIBLE);
        getGroupView().setVisibility(View.VISIBLE);

        // even if we don't use recyclerview, we have to set a layoutmanager or when destroying fragment it will crash
        ((RecyclerView) view.findViewById(R.id.recyclerViewSeasons)).setLayoutManager(new LinearLayoutManager(getActivity()));

        ll = (LinearLayout)view.findViewById(R.id.linearLayout);
        llBasicInfos = (LinearLayout)view.findViewById(R.id.linearLayoutBasicInfos);
        tivPoster = (TraktImageView)view.findViewById(R.id.tivPoster);

        rmvInfos = (ReadMoreView)view.findViewById(R.id.expandableTextViewInfos);
        tlInfos = (TableLayout)view.findViewById(R.id.tableLayoutInfos);
        tvOverview = (TextView)view.findViewById(R.id.textViewOverview);
        tvTitle = (TextView)view.findViewById(R.id.textViewTitle);
        tvSubtitle = (TextView)view.findViewById(R.id.textViewSubtitle);
        tvRate = (TextView)view.findViewById(R.id.textViewRate);
        tvComments = (TextView)view.findViewById(R.id.textViewComments);

        tavSeen =  (TraktActionView)view.findViewById(R.id.traktActionViewSeen);
        tavCollection =  (TraktActionView)view.findViewById(R.id.traktActionViewCollection);
        tavWatchlist =  (TraktActionView)view.findViewById(R.id.traktActionViewWatchlist);

        llBasicInfos.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                llBasicInfos.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ((HeaderActivity)getActivity()).setGroupView(TraktItemFragment.this, llBasicInfos.getTop());
            }
        });


        fab = (FloatingActionButton2) view.findViewById(R.id.fab);
        fab.setAnimation(FloatingActionButton2.ANIM_SCALE);
        // allow the fab animation to work
        Utils.disableClipOnParents(fab);
    }

    protected void refreshGeneralView(TraktBase traktBase)
    {
        tvTitle.setText(traktBase.title);
        String firstAired = getDateText(traktBase.firstAired == null);
        tvSubtitle.setText(firstAired);
        tvOverview.setText(traktBase.overview);
        rmvInfos.setExpanded(false, false);

        tavSeen.setChecked(traktBase.watched);
        tavCollection.setChecked(traktBase.collected);
        tavWatchlist.setChecked(traktBase.watchlisted);

        // TODO revealview should be intelligent, open overview if user has not seen a movie or a show and close otherwise
        // but has to be close if user has not seen an episode to avoid spoiler etc...
    }

    protected void addInfo(String title, String info)
    {
        TableRow tr;
        int chidlCount = tlInfos.getChildCount();
        // create a new tablerow if we reach the nb_columns or if there is none
        if(chidlCount == 0)
            tlInfos.addView(tr = new TableRow(getActivity()));
        else
        {
            tr = (TableRow) tlInfos.getChildAt(chidlCount-1);
            if(tr.getChildCount() >= NB_COLUMNS)
                tlInfos.addView(tr = new TableRow(getActivity()));
        }

        LinearLayout ll = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_grid_layout_info, tr, false);
        TextView tvTitle = (TextView) ll.findViewById(R.id.textViewTitle);
        TextView tvInfo = (TextView) ll.findViewById(R.id.textViewInfo);

        tvTitle.setText(title);
        tvInfo.setText(info);

        // set gravity depending on the position of the view
        int trChildCount = tr.getChildCount();
        if(trChildCount == 0)
        {
            tvTitle.setGravity(Gravity.START);
            tvInfo.setGravity(Gravity.START);
        }
        else if(trChildCount == NB_COLUMNS - 1)
        {
            tvTitle.setGravity(Gravity.END);
            tvInfo.setGravity(Gravity.END);
        }
        else
        {
            tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            tvInfo.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        tr.addView(ll);
    }

    private class TAVObserver implements Observer<SyncResponse>
    {
        private TraktActionView tav;

        public TAVObserver(TraktActionView tav)
        {
            this.tav = tav;
        }

        @Override
        public void onCompleted()
        {

        }

        @Override
        public void onError(Throwable e)
        {
            // TODO
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            tav.error();
        }

        @Override
        public void onNext(SyncResponse syncResponse)
        {
            // TODO
            Toast.makeText(getActivity(), "Sent!", Toast.LENGTH_SHORT).show();
            tav.success();
        }
    }

    @Override
    protected void showProgressBar(Animator.AnimatorListener listener)
    {
        getGroupView().setFillViewport(true);
        show(frameLayoutLoadingWrapper, listener, getViewToHide(), frameLayoutErrorWrapper);
    }

    @Override
    protected void showView()
    {
        show(getViewToHide(), new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // there's a bug if the content of the scrollview is too small, we cant' scroll and see
                // what's after, so setFillViewPort when displaying the view as a workaround
                getGroupView().setFillViewport(false);
            }
        }, frameLayoutErrorWrapper, frameLayoutLoadingWrapper);
    }

    @Override
    protected void showErrorView()
    {
        getGroupView().setFillViewport(true);
        show(frameLayoutErrorWrapper, getViewToHide(), frameLayoutLoadingWrapper);
    }

    private View getViewToHide()
    {
        return getGroupView().findViewById(R.id.linearLayout);
    }

    @Override
    public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(who, l, t, oldl, oldt);

        int dy = t - oldt;

        // in some case some little variation doesn't look good
        if(Math.abs(dy) > 5)
            fab.reactToScroll(dy);
    }
}
