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
import android.util.Log;
import android.widget.Toast;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.TraktManager.TraktListener;
import com.jakewharton.apibuilder.ApiException;
import com.jakewharton.trakt.TraktException;

public abstract class TraktTask extends AsyncTask<Void, String, Boolean>
{
	protected TraktManager tm;
	protected Fragment fragment;
	protected Context context;
	protected TraktListener tListener;
	//this not will not display toast
	protected boolean silent = false;
	protected boolean silentConnectionError = false;
	protected boolean inQueue = false;
	protected Exception error;
	
	public TraktTask(TraktManager tm, Fragment fragment)
	{
		this.tm = tm;
		this.fragment = fragment;
		this.context = fragment.getActivity();
		
		try
		{
			tListener = (TraktListener)fragment;
		}
		catch(ClassCastException e)
		{
			e.printStackTrace();
		}
	}
	
	public TraktTask inQueue()
	{
		this.inQueue = true;
		return this;
	}
	
	public void reconnect(Fragment fragment)
	{
		this.fragment = fragment;
		this.context = fragment.getActivity();
		tListener = (TraktListener)fragment;
	}
	
	@Override
	protected void onPreExecute()
	{
		Log.i("Traktoid","start a task...");
		if(!Utils.isActivityFinished(fragment.getActivity()))
			tm.onBeforeTraktRequest(tListener);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) 
	{
		if(!Utils.isOnline(context))
		{
			if(!Utils.isActivityFinished(fragment.getActivity()) && !silentConnectionError)
				handleException(new Exception("Internet connection required!"));

//				showToast("Internet connection required!", Toast.LENGTH_LONG);
			return doOfflineTraktStuff();
		}
		try
		{
			if(isCancelled())
				return false;
			else
				return doTraktStuffInBackground();
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
	}
	
	protected abstract boolean doTraktStuffInBackground();
	
	protected boolean doOfflineTraktStuff()
	{
		return false;
	}
	
	protected void showToast(String message, int duration)
	{
		if(!silent)
			this.publishProgress("toast", String.valueOf(duration), message);
	}

	@Override
	protected void onPostExecute (Boolean success)
	{
		//has to be executed otherwise tasks will stay in queue even when finished
		tm.onAfterTraktRequest(tListener, success, inQueue);
		Log.i("Traktoid","task finish!");
	}

	@Override
	protected void onProgressUpdate(String... values) 
	{
		if(values[0].equals("toast"))
			Toast.makeText(context, values[2], Integer.parseInt(values[1])).show();
		else if(values[0].equals("error"))
			if(!Utils.isActivityFinished(fragment.getActivity()))
				tm.onErrorTraktRequest(tListener, error);
	}
	
	private void handleException(Exception e)
	{
		e.printStackTrace();
		this.error = e;
		this.publishProgress("error");
		showToast("Error : " + e.getMessage(), Toast.LENGTH_LONG);
	}

	public TraktTask silent(boolean silent) 
	{
		this.silent = silent;
		return this;
	}
	
	//do nothin special in case of connection error (not even showing a toast)
	public TraktTask silentConnectionError(boolean silentConnectionError) 
	{
		this.silentConnectionError = silentConnectionError;
		return this;
	}
}
