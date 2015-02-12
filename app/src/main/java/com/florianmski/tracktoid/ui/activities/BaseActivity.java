package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.utils.Utils;

public abstract class BaseActivity extends ActionBarActivity
{
    private Toolbar toolbar;

    protected abstract View getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        // setup toolbar after layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        String title = getIntent().getStringExtra(TraktoidConstants.BUNDLE_TITLE);
        String subtitle = getIntent().getStringExtra(TraktoidConstants.BUNDLE_SUBTITLE);

        if(title != null && !title.isEmpty())
            getSupportActionBar().setTitle(title);
        if(subtitle != null && !subtitle.isEmpty())
            getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
    }

	public void setPrincipalFragment(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

    protected static Intent makeIntent(Activity a, Class<?> activityToLaunch, Bundle args)
    {
        Intent i = new Intent(a, activityToLaunch);
        if(args != null)
            i.putExtras(args);
        return i;
    }

    public static void launchActivity(Activity a, Class<?> activityToLaunch)
    {
        launchActivity(a, activityToLaunch, null);
    }

    public static void launchActivity(Activity a, Class<?> activityToLaunch, Bundle args)
    {
        a.startActivity(makeIntent(a, activityToLaunch, args));
    }
	
	public static void launchActivityForResult(FragmentActivity a, Class<?> activityToLaunch, int requestCode, Bundle args)
	{
		a.startActivityForResult(makeIntent(a, activityToLaunch, args), requestCode);
	}

    public void setTheme(TraktoidTheme theme)
    {
        //set actionbar color
        Utils.changeColor(toolbar.getBackground(), theme.getColor(this), toolbar);
    }

    public Toolbar getToolbar()
    {
        return (Toolbar)findViewById(R.id.toolbar);
    }
}
