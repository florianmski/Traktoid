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

package com.florianmski.tracktoid.trakt;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.tasks.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.UpdateShowsTask;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;

public class TraktManager extends ServiceManager implements OnSharedPreferenceChangeListener
{	
	private static TraktManager traktManager;
		
	private static String username;
	private static String password;
	
	private ArrayList<TraktTask> tasks = new ArrayList<TraktTask>();
	private ArrayList<TraktListener> listeners = new ArrayList<TraktListener>();
	private Context context;

	public static synchronized TraktManager getInstance()
	{	
		//should not arrive
		if (traktManager == null)
			return null;
		return traktManager;
	}

	private TraktManager(Context context) 
	{		
		this.context = context;
		
		setApiKey(context.getResources().getString(R.string.trakt_key));
		setAccountInformations(context);
	}
	
	public static void create(Context context)
	{
		traktManager = new TraktManager(context);
	}
	
	public void setAccountInformations(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		username = prefs.getString("editTextUsername", "test1").trim();
		password = prefs.getString("editTextPassword", "test1").trim();
		
		setAuthentication(username, Utils.SHA1(password));
		
		prefs.registerOnSharedPreferenceChangeListener(this);
	}
	
	public static String getUsername()
	{
		return username;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		if(key.equals("editTextUsername"))
			username = sharedPreferences.getString("editTextUsername", "test1").trim();
		else if(key.equals("editTextPassword"))
			password = sharedPreferences.getString("editTextPassword", "test1").trim();
		
		setAuthentication(username, Utils.SHA1(password));
	}

	public void addObserver(TraktListener listener)
	{
		listeners.add(listener);
	}

	public void removeObserver(TraktListener listener)
	{
		listeners.remove(listener);
	}
	
	public void onBeforeTraktRequest(TraktListener listener)
	{
		listener.onBeforeTraktRequest();
	}
	
	public void onAfterTraktRequest(TraktListener listener, boolean success)
	{
		if(!tasks.isEmpty())
			tasks.remove(0);

		if(!tasks.isEmpty())
			tasks.get(0).execute();
		
			listener.onAfterTraktRequest(success);
	}
	
	public void onErrorTraktRequest(TraktListener listener, Exception e, String message)
	{
		listener.onErrorTraktRequest(e, message);
	}
	
	public void onShowUpdated(TvShow show)
	{
		for(TraktListener l : listeners)
			l.onShowUpdated(show);
	}
	
	public void onShowRemoved(TvShow show)
	{
		for(TraktListener l : listeners)
			l.onShowRemoved(show);
	}

	public interface TraktListener
	{
		public void onBeforeTraktRequest();
		public void onAfterTraktRequest(boolean success);
		public void onErrorTraktRequest(Exception e, String message);
		public void onShowUpdated(TvShow show);
		public void onShowRemoved(TvShow show);
	}
	
	//add user action in a queue so actions are done one by one
	public synchronized void addToQueue(TraktTask task)
	{
		tasks.add(task);
		
		if(tasks.size() == 1)
			task.execute();
		else
			Toast.makeText(context, "This action will be done later...", Toast.LENGTH_SHORT).show();
	}
	
	//check if a show is currently updating
	public boolean isUpdateTaskRunning()
	{
		return !tasks.isEmpty() && (tasks.get(0) instanceof UpdateShowsTask || tasks.get(0) instanceof ShowsTask);
	}
}
