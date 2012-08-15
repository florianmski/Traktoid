package com.florianmski.tracktoid.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.androidquery.service.MarketService;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.trakt.tasks.get.SynchronizationTask;
import com.florianmski.tracktoid.ui.fragments.library.PagerLibraryFragment;

public class LibraryActivity extends SinglePaneActivity
{		
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_single_fragment);
				
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//if we don't have user pass or username, go to login activity
		if(prefs.getString(TraktoidConstants.PREF_PASSWORD, null) == null || prefs.getString(TraktoidConstants.PREF_USERNAME, null) == null)
		{
			launchActivity(LoginActivity.class);
			finish();
//			return;
		}
		else
			doStuffIfUserIsLogged();
		
//		if(savedInstanceState == null)
//			setPrincipalFragment(PagerLibraryFragment.newInstance(getIntent().getExtras()));
		
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setTitle("Library");
	}
	
	public void doStuffIfUserIsLogged()
	{
		//check if a new version of Traktoid is available and display a dialog if so
		MarketService ms = new MarketService(this);
		ms.checkVersion();

		//show sometimes a dialog to rate the app on the market 
		//TODO re-enable this when this update goes to market
//		AppRater.app_launched(this);

		//sync with trakt
		new SynchronizationTask(this).silentConnectionError(true).fire();

		//Trying to set high definition image on high resolution
		//does not seem to be a great idea, it's slow and I sometimes get an outOfMemoryError :/
		//		Image.smallSize = !Utils.isTabletDevice(getActivity());
	}

	@Override
	public Fragment getFragment() 
	{
		return PagerLibraryFragment.newInstance(getIntent().getExtras());
	}
}