package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidquery.service.MarketService;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.trakt.tasks.get.SynchronizationTask;

public class StartActivity extends Activity
{
	public final static int RESULT_LOGIN = 42;
	
	//TODO do something if there is the popup to rate the app
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
				
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//if we don't have user pass or username, go to login activity
		if(prefs.getString(TraktoidConstants.PREF_PASSWORD, null) == null || prefs.getString(TraktoidConstants.PREF_USERNAME, null) == null)
			launchActivityForResult(LoginActivity.class, 1337);
		else
			doStuffIfUserIsLogged();
		
		Log.e("test","onCreate");
	}
	
	public void doStuffIfUserIsLogged()
	{
		//TODO make something smart
		//check if db need an upgrade
		//		DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
		//		dbw.open();

		//check if a new version of Traktoid is available and display a dialog if so
		MarketService ms = new MarketService(this);
		ms.checkVersion();

		//show sometimes a dialog to rate the app on the market 
		//TODO dialog is shown but replace by the library activity
//		AppRater.app_launched(this);

		//sync with trakt
		new SynchronizationTask(this).silentConnectionError(true).fire();

		//Trying to set high definition image on high resolution
		//does not seem to be a great idea, it's slow and I sometimes get an outOfMemoryError :/
		//		Image.smallSize = !Utils.isTabletDevice(getActivity());
		
//		launchActivityForResult(LibraryActivity.class, 117);
		launchActivity(LibraryActivity.class);
		finish();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_LOGIN)
			doStuffIfUserIsLogged();
		else
			finish();
	}
	
	public void launchActivity(Class<?> activityToLaunch, Bundle args)
	{
		Intent i = new Intent(this, activityToLaunch);
		if(args != null)
			i.putExtras(args);
		startActivity(i);
	}
	
	public void launchActivityForResult(Class<?> activityToLaunch, int requestCode, Bundle args)
	{
		Intent i = new Intent(this, activityToLaunch);
		if(args != null)
			i.putExtras(args);
		startActivityForResult(i, requestCode);
	}
	
	public void launchActivity(Class<?> activityToLaunch)
	{
		launchActivity(activityToLaunch, null);
	}
	
	public void launchActivityForResult(Class<?> activityToLaunch, int requestCode)
	{
		launchActivityForResult(activityToLaunch, requestCode, null);
	}
}
