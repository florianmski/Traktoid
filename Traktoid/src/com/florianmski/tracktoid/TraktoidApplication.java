package com.florianmski.tracktoid;

import java.io.File;

import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.os.Environment;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.florianmski.tracktoid.trakt.TraktManager;

@ReportsCrashes(formUri = TraktoidConstants.KEY_BUGSENSE, formKey="") 
public class TraktoidApplication extends Application
{
	@Override
	public void onCreate() 
	{
		//TODO add bugsense
		//TODO do lint corrections
		// The following line triggers the initialization of ACRA
//		ACRA.init(this);

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
