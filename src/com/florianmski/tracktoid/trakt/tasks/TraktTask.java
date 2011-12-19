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

package com.florianmski.tracktoid.trakt.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.TraktManager.TraktListener;
import com.jakewharton.apibuilder.ApiException;
import com.jakewharton.trakt.TraktException;

public class TraktTask extends AsyncTask<Void, String, Boolean>
{
	protected TraktManager tm;
	protected Fragment fragment;
	protected Context context;
	protected TraktListener tListener;
	
	public TraktTask(TraktManager tm, Fragment fragment)
	{
		this.tm = tm;
		this.fragment = fragment;
		this.context = fragment.getActivity();
		tListener = (TraktListener)fragment;
	}
	
	@Override
	protected void onPreExecute()
	{
		if(!Utils.isActivityFinished(fragment.getActivity()))
			tm.onBeforeTraktRequest(tListener);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) 
	{
		if(!Utils.isOnline(context))
		{
			if(!Utils.isActivityFinished(fragment.getActivity()))
				tm.onErrorTraktRequest(tListener, null, "Internet connection required!");
			this.publishProgress("toast", "1", "Internet connection required!");
			return false;
		}
		try
		{
			doTraktStuffInBackground();
		}
		catch (ApiException e) 
		{
			handleException(e);
			return false;
        }
		catch (TraktException e) 
		{
			handleException(e);
			return false;
        }
		catch (IllegalArgumentException e) 
		{
			handleException(e);
			return false;
        }
		return true;
	}
	
	protected void doTraktStuffInBackground() {}

	@Override
	protected void onPostExecute (Boolean success)
	{
		if(!Utils.isActivityFinished(fragment.getActivity()))
			tm.onAfterTraktRequest(tListener, success);
	}

	@Override
	protected void onProgressUpdate(String... values) 
	{
		if(values[0].equals("toast"))
			Toast.makeText(context, values[2], Integer.parseInt(values[1])).show();
	}
	
	private void handleException(Exception e)
	{
		//TODO com.jakewharton.trakt.TraktException: com.jakewharton.apibuilder.ApiException: java.net.SocketTimeoutException
		//TODO onErrorTraktRequest must be executed on UIThread
		e.printStackTrace();
		if(!Utils.isActivityFinished(fragment.getActivity()))
			tm.onErrorTraktRequest(tListener, e, e.getMessage());
		this.publishProgress("toast", "1", "Error : " + e.getMessage());
	}
}
