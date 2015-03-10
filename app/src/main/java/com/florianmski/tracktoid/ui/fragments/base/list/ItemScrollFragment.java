package com.florianmski.tracktoid.ui.fragments.base.list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.AdapterInterface;
import com.florianmski.tracktoid.containers.ContainerInterface;
import com.florianmski.tracktoid.errors.Comportment;
import com.florianmski.tracktoid.errors.ErrorHandler;
import com.florianmski.tracktoid.errors.NoResultException;
import com.florianmski.tracktoid.errors.RetrofitComportment;
import com.florianmski.tracktoid.ui.fragments.BaseFragment;
import com.florianmski.tracktoid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public abstract class ItemScrollFragment<T, E, V extends ViewGroup, S, A extends AdapterInterface<T>> extends BaseFragment implements Observer<E>, ScrollListenerProvider<S>, SwipeRefreshLayout.OnRefreshListener
{
    protected E data;
    protected ContainerInterface.ViewContainerInterface<T, V, A> viewContainer;
    protected List<S> scrollListeners = new ArrayList<>();

    protected RelativeLayout root;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected FrameLayout frameLayoutLoadingWrapper;
    protected ProgressBar progressBar;
    protected FrameLayout frameLayoutErrorWrapper;
    protected TextView errorView;
    protected View.OnClickListener retryListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            refresh(true);
        }
    };

    protected Observable<E> observable;
    protected CompositeSubscription subscriptions = new CompositeSubscription();
    protected Subscription subscription = Subscriptions.empty();
    protected ErrorHandler errorHandler;
    protected Comportment defaultComportment = new Comportment(
            null,
            "Unknown error\nA report has been send to the dev",
            "tap to retry",
            retryListener);

    protected boolean refreshAtStart = true;
    protected boolean instantLoad = false;
    protected AnimatorSet fadeAnim;
    protected View viewBeingAnimated;

    public ItemScrollFragment(ContainerInterface.ViewContainerInterface<T, V, A> viewContainer)
    {
        this.viewContainer = viewContainer;
    }

    protected abstract Observable<E> createObservable();
    protected abstract void refreshView(E data);
    protected abstract boolean isEmpty(E data);

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(fadeAnim != null)
        {
            fadeAnim.removeAllListeners();
            fadeAnim.cancel();
        }

        subscriptions.unsubscribe();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        errorHandler = new ErrorHandler(getActivity(), errorView, defaultComportment)
                .putComportment(new Comportment(NoResultException.class, "No result found :(", "tap to retry", retryListener))
                .putComportment(new RetrofitComportment(retryListener));

        // by default, pull to refresh is not possible
        setPullToRefresh(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getTheme().getColorDark(getActivity()));

        if(refreshAtStart)
            refresh(!instantLoad);
        else
            showView();
    }

    protected View getCustomLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_item_group, container, false);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = getCustomLayout(inflater, container, savedInstanceState);
        ViewStub vs = (ViewStub) view.findViewById(R.id.viewStub);
        vs.setLayoutResource(viewContainer.getLayoutId());
        vs.inflate();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        root = (RelativeLayout) view.findViewById(R.id.root);
        viewContainer.set((V) view.findViewById(android.R.id.list));
        frameLayoutLoadingWrapper = (FrameLayout) view.findViewById(R.id.frameLayoutLoadingWrapper);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        frameLayoutErrorWrapper = (FrameLayout) view.findViewById(R.id.frameLayoutErrorWrapper);
        errorView = (TextView) view.findViewById(R.id.error);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
    }

    protected void refresh(boolean showProgressBar)
    {
        if(showProgressBar)
        {
            showProgressBar(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    refresh();
                }
            });
        }
        else
            refresh();
    }

    private void refresh()
    {
        // unsubscribe the subscription and add the new one
        subscriptions.remove(subscription);
        subscription = AndroidObservable.bindFragment(this, createObservable().subscribeOn(Schedulers.io()))
                .subscribe(ItemScrollFragment.this);
        subscriptions.add(subscription);
    }

    @Override
    public void onInsetsChanged(Rect insets)
    {
        super.onInsetsChanged(insets);

        getGroupView().setClipToPadding(false);
        setGroupViewPadding(
                getGroupView().getPaddingLeft(),
                // don't touch padding top if there is already a custom value
                getGroupView().getPaddingTop() != 0 ? getGroupView().getPaddingTop() : Utils.getActionBarHeight(getActivity()),
                getGroupView().getPaddingRight(),
                insets.bottom);
        swipeRefreshLayout.setProgressViewEndTarget(false, getGroupView().getPaddingTop() * 2);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser)
            showActionBar(true);
    }

    public V getGroupView()
    {
        return viewContainer.get();
    }

    public E getData()
    {
        return data;
    }

    protected void showProgressBar(Animator.AnimatorListener listener)
    {
        show(frameLayoutLoadingWrapper, listener, viewContainer.get(), frameLayoutErrorWrapper);
    }

    protected void showView()
    {
        show(viewContainer.get(), frameLayoutErrorWrapper, frameLayoutLoadingWrapper);
    }

    protected void showErrorView()
    {
        show(frameLayoutErrorWrapper, viewContainer.get(), frameLayoutLoadingWrapper);
    }

    protected void show(View viewToShow, View... viewsToHide)
    {
        show(viewToShow, null, viewsToHide);
    }

    protected void show(View viewToShow, Animator.AnimatorListener listener, View... viewsToHide)
    {
        // if the view is already visible, nothing to do!
        if((viewBeingAnimated != null && viewBeingAnimated.getId() == viewToShow.getId()) || viewToShow.getVisibility() == View.VISIBLE)
            return;

        if(fadeAnim != null)
            fadeAnim.cancel();

        fadeAnim = new AnimatorSet();
        if(viewsToHide.length == 0)
            fadeAnim.play(changeToVisibility(View.VISIBLE, viewToShow));
        else
        {
            AnimatorSet.Builder builder = fadeAnim.play(changeToVisibility(View.GONE, viewsToHide[0]));
            if(viewsToHide.length > 1)
            {
                for(int i = 1; i < viewsToHide.length; i++)
                    builder.with(changeToVisibility(View.GONE, viewsToHide[i]));
            }

            builder.before(changeToVisibility(View.VISIBLE, viewToShow));
        }

        if(listener != null)
            fadeAnim.addListener(listener);

        fadeAnim.start();
    }

    private Animator changeToVisibility(final int toVisibility, final View v)
    {
        float startAlpha = toVisibility == View.VISIBLE ? 0f : 1f;
        float endAlpha = toVisibility == View.VISIBLE ? 1f : 0f;

        float startY = toVisibility == View.VISIBLE ? 50f : 0f;
        float endY = toVisibility == View.VISIBLE ? 0f : -50f;

        PropertyValuesHolder fadeAnimator = PropertyValuesHolder.ofFloat("alpha", startAlpha, endAlpha);
        PropertyValuesHolder translateAnimator = PropertyValuesHolder.ofFloat("y", startY, endY);

        ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, fadeAnimator, translateAnimator);
        animator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                viewBeingAnimated = v;
                if (toVisibility == View.VISIBLE)
                    v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                viewBeingAnimated = null;
                if (toVisibility != View.VISIBLE)
                    v.setVisibility(toVisibility);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        return animator;
    }

    // avoid flickering of the progressbar when we get the results almost immediately
    protected void setInstantLoad(boolean instantLoad)
    {
        this.instantLoad = instantLoad;
    }

    protected void setRefreshOnStart(boolean refreshAtStart)
    {
        this.refreshAtStart = refreshAtStart;
    }

    public void setGroupViewPadding(int left, int top, int right, int bottom)
    {
        getGroupView().setPadding(left, top, right, bottom);

        // set padding on wrappers
        if(getInsets() != null)
        {
            top -= getInsets().bottom;
            frameLayoutLoadingWrapper.setPadding(left, top, right, 0);
            frameLayoutErrorWrapper.setPadding(left, top, right, 0);
        }
    }

    @Override
    public void onNext(E item)
    {
        data = item;

        if(isEmpty(item))
            // this way the observable do not call onCompleted so it is still active.
            // Useful when doing the first sync because the cursor can be reloaded and
            // display the item being synced for the first time
            onError(new NoResultException());
        else
        {
            refreshView(item);
            showView();
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable throwable)
    {
        errorHandler.handle(throwable, "Error while loading stuff");
        showErrorView();
    }

    @Override
    public void addScrollListener(S listener)
    {
        scrollListeners.add(listener);
    }

    @Override
    public void removeScrollListener(S listener)
    {
        scrollListeners.remove(listener);
    }

    @Override
    public void onRefresh()
    {
        refresh(false);
    }

    public void setPullToRefresh(boolean pullToRefresh)
    {
        swipeRefreshLayout.setEnabled(pullToRefresh);
    }
}
