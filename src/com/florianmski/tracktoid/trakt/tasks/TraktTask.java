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
import android.widget.Toast;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.TraktManager.TraktListener;
import com.jakewharton.apibuilder.ApiException;
import com.jakewharton.trakt.TraktException;

public class TraktTask extends AsyncTask<Void, String, Boolean>
{
	protected TraktManager tm;
	protected Context context;
	protected TraktListener listener;
	
	public TraktTask(TraktManager tm, Context context)
	{
		this.tm = tm;
		this.context = context;
		listener = (TraktListener)context;
	}
	
	@Override
	protected void onPreExecute()
	{
		tm.onBeforeTraktRequest(listener);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) 
	{
		if(!Utils.isOnline(context))
		{
			tm.onErrorTraktRequest(listener, null, "Internet connection required!");
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
		tm.onAfterTraktRequest(listener, success);
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
		tm.onErrorTraktRequest(listener, e, e.getMessage());
		this.publishProgress("toast", "1", "Error : " + e.getMessage());
	}
}
