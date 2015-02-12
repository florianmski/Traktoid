package com.florianmski.tracktoid.ui.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.ui.activities.BaseActivity;
import com.florianmski.tracktoid.ui.activities.TranslucentActivity;
import com.florianmski.tracktoid.ui.widgets.DrawInsetsFrameLayout;

public abstract class BaseFragment extends Fragment implements DrawInsetsFrameLayout.OnInsetsCallback
{
    private Rect insets;

    public void launchActivity(Class<?> activityToLaunch, Bundle args)
    {
        Intent i = new Intent(getActivity(), activityToLaunch);
        if(args != null)
            i.putExtras(args);
        startActivity(i);
    }

    public void launchActivity(Class<?> activityToLaunch)
    {
        launchActivity(activityToLaunch, null);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if(getActivity() != null && isVisibleToUser)
            setupActionBar();
    }

    protected void setupActionBar()
    {
//        if(getActionBar().getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD)
//            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Can't retain fragments that are nested in other fragments
//        if(getParentFragment() == null)
//            setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && getActivity() instanceof TranslucentActivity)
            ((TranslucentActivity)getActivity()).addOnInsetsCallback(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        setActivityTheme();
    }

    protected void setActivityTheme()
    {
        ((BaseActivity)getActivity()).setTheme(getTheme());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && getActivity() instanceof TranslucentActivity)
            ((TranslucentActivity)getActivity()).removeOnInsetsCallback(this);
    }

    public ActionBar getActionBar()
    {
        return ((ActionBarActivity)getActivity()).getSupportActionBar();
    }

    public Toolbar getToolbar()
    {
        return ((BaseActivity)getActivity()).getToolbar();
    }

    protected void setTitle(String title)
    {
        getActionBar().setTitle(title);
    }

    protected void setSubtitle(String subtitle)
    {
        getActionBar().setSubtitle(subtitle);
    }

    public abstract TraktoidTheme getTheme();

    @Override
    public void onInsetsChanged(Rect insets)
    {
        this.insets = insets;
    }

    public Rect getInsets()
    {
        return insets;
    }

    // you can't setRetainInstance() on nested fragments so in a nested fragment getRetainInstance()
    // returns always false but the child is retained if its parent is also retained
    // go up to see if the parent is retained and so is the child if that's the case
    protected boolean isInstanceRetained()
    {
        Fragment f = this;
        while(f.getParentFragment() != null)
            f = f.getParentFragment();
        return f.getRetainInstance();
    }
}
