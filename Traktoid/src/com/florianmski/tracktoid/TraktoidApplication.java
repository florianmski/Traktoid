package com.florianmski.tracktoid;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.bugsense.trace.BugSenseHandler;
import com.florianmski.tracktoid.trakt.TraktManager;

public class TraktoidApplication extends Application
{
	@Override
	public void onCreate() 
	{
		//TODO add bugsense
		//TODO do lint corrections

		// If you want to use BugSense for your fork, register with
		// them and place your API key in /assets/bugsense.txt
		// (This prevents me receiving reports of crashes from forked 
		// versions which is somewhat confusing!)      
		try 
		{
			InputStream inputStream = getAssets().open("bugsense.txt");
			String key = Utils.readInputStream(inputStream).trim();
			BugSenseHandler.setup(this, key);
		} 
		catch (IOException e) 
		{
			Log.d("TAG", "No bugsense keyfile found");
		}

		TraktManager.create(this);

		//if extern media is mounted, use it for cache, else use default cache
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			File ext = new File(Utils.getExtFolderPath(this));
			File cacheDir = new File(ext, "cache"); 
			AQUtility.setCacheDir(cacheDir);
		}

		super.onCreate();
	}

	@Override
	public void onLowMemory()
	{  
		//clear all memory cached images when system is in low memory
		//note that you can configure the max image cache count, see CONFIGURATION
		BitmapAjaxCallback.clearCache();
	}

}
