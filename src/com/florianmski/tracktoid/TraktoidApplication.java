/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
