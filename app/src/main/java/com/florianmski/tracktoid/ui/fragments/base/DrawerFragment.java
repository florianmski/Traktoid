package com.florianmski.tracktoid.ui.fragments.base;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.adapters.lists.RecyclerDrawerAdapter;
import com.florianmski.tracktoid.ui.fragments.BaseFragment;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;

public abstract class DrawerFragment extends BaseFragment implements RecyclerAdapter.OnItemClickListener
{
    private final static String BUNDLE_POSITION = "position";

    protected RecyclerView rv;
    protected TraktImageView tivFanart;
    protected FrameLayout flAccount;

    protected abstract RecyclerDrawerAdapter createAdapter(RecyclerDrawerAdapter adapter);
    protected abstract void changeFragment(int position);
    protected abstract int getDefaultId();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_POSITION, getAdapter().getCheckedPosition());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        flAccount.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                flAccount.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                rv.setPadding(rv.getPaddingLeft(),
                        tivFanart.getHeight(),
                        rv.getPaddingRight(),
                        rv.getPaddingBottom());

                // annoying bug, the list is always scrolled to the top, no idea why
                // this workaround does the trick
                rv.scrollBy(0, Integer.MIN_VALUE);
                rv.setOnScrollListener(new RecyclerView.OnScrollListener()
                {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                    {
                        super.onScrolled(recyclerView, dx, dy);

                        View v = recyclerView.getChildAt(0);
                        if(v == null)
                            return;

                        flAccount.setTranslationY(flAccount.getTranslationY() - dy);
                    }
                });
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(createAdapter(new RecyclerDrawerAdapter(getActivity(), this)));

        if(savedInstanceState != null)
            checkItem(savedInstanceState.getInt(BUNDLE_POSITION));
        else
            onItemClick(null, getAdapter().getItemPosition(getDefaultId()));
    }

    @Override
    public void onItemClick(View v, final int position)
    {
        ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(Gravity.START);
        if(position == getAdapter().getCheckedPosition())
            return;

        checkItem(position);
        changeFragment(position);
    }

    private void checkItem(int position)
    {
        RecyclerDrawerAdapter.DrawerItem drawerItem = getAdapter().getItem2(position);
        getAdapter().setCheckedPosition(position);
        getActionBar().setTitle(drawerItem.title);
        getActionBar().setSubtitle(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        tivFanart = (TraktImageView) view.findViewById(R.id.tivFanart);
        flAccount = (FrameLayout) view.findViewById(R.id.accoundView);
    }

    public RecyclerDrawerAdapter getAdapter()
    {
        return (RecyclerDrawerAdapter) rv.getAdapter();
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.DEFAULT;
    }

    @Override
    public void onInsetsChanged(Rect insets)
    {
        super.onInsetsChanged(insets);

        rv.setPadding(rv.getPaddingLeft(), rv.getPaddingTop(), rv.getPaddingRight(), insets.bottom);
    }
}
