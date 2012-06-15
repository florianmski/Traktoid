package com.florianmski.tracktoid.ui.activities.phone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.androidquery.service.MarketService;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.trakt.tasks.get.SynchronizationTask;
import com.florianmski.tracktoid.widgets.AppRater;

public class StartActivity extends BaseActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//TODO make something smart
		//check if db need an upgrade
		//		DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
		//		dbw.open();

		//check if a new version of Traktoid is available and display a dialog if so
		MarketService ms = new MarketService(this);
		ms.checkVersion();

		//show sometimes a dialog to rate the app on the market 
		AppRater.app_launched(this);

		//sync with trakt
		new SynchronizationTask(this).silentConnectionError(true).fire();

		//Trying to set high definition image on high resolution
		//does not seem to be a great idea, it's slow and I sometimes get an outOfMemoryError :/
		//		Image.smallSize = !Utils.isTabletDevice(getActivity());
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//if we don't have user pass or username, go to login activity
		if(prefs.getString(TraktoidConstants.PREF_PASSWORD, null) == null || prefs.getString(TraktoidConstants.PREF_USERNAME, null) == null)
			launchActivity(LoginActivity.class);
		else
			launchActivity(LibraryActivity.class);
		
		finish();
	}
}
