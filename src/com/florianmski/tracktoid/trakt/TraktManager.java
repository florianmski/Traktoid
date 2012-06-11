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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.jakewharton.trakt.ServiceManager;

public class TraktManager extends ServiceManager implements OnSharedPreferenceChangeListener
{	
	private static TraktManager traktManager;

	private static String username;
	private static String password;

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

		//in Traktoid <= 0.6, password was stored non encrypted (I know...)
		//so store it encrypted now!
		if(!prefs.getBoolean("sha1", false))
		{
			password = Utils.SHA1(password);
			prefs.edit().putString("editTextPassword", password);
			prefs.edit().putBoolean("sha1", true);
		}

		setAuthentication(username, password);

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

		setAuthentication(username, password);
	}

//	public void addObserver(TraktListener listener)
//	{
//		listeners.add(listener);
//	}
//
//	public void removeObserver(TraktListener listener)
//	{
//		listeners.remove(listener);
//	}

//	public void onBeforeTraktRequest(TraktListener listener)
//	{
//		if(listeners.contains(listener))
//			listener.onBeforeTraktRequest();
//	}

//	public synchronized void onAfterTraktRequest(TraktListener listener, boolean success, boolean inQueue)
//	{
//		//		if(!tasks.isEmpty())
//		//			tasks.remove(task);
//		//
//		//		if(!tasks.isEmpty())
//		//			tasks.get(0).execute();
//		//		
//		//		if(listeners.contains(listener))
//		//			listener.onAfterTraktRequest(success);
//		
//		//at the end of their execution ALL the task come here, even the ones that weren't queued. Be careful!
//		if(!tasks.isEmpty() && inQueue)
//		{
//			tasks.remove(0);
//
//			if(!tasks.isEmpty())
//				tasks.get(0).inQueue().fire();
//		}
//
//		if(listeners.contains(listener))
//			listener.onAfterTraktRequest(success);
//	}

//	public void onErrorTraktRequest(TraktListener listener, Exception e)
//	{
//		if(listeners.contains(listener))
//			listener.onErrorTraktRequest(e);
//	}
//
//	@Override
//	public void onShowUpdated(TvShow show)
//	{
//		for(TraktListener l : listeners)
//			l.onShowUpdated(show);
//	}
//
//	@Override
//	public void onShowRemoved(TvShow show)
//	{
//		for(TraktListener l : listeners)
//			l.onShowRemoved(show);
//	}
//	
//	@Override
//	public void onMovieUpdated(Movie movie)
//	{
//		for(TraktListener l : listeners)
//			l.onMovieUpdated(movie);
//	}
//
//	@Override
//	public void onMovieRemoved(Movie movie)
//	{
//		for(TraktListener l : listeners)
//			l.onMovieRemoved(movie);
//	}
//	
//
//	@Override
//	public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars) 
//	{
//		for(TraktListener l : listeners)
//			l.onCalendar(calendars);
//	}	
//
//	@Override
//	public void onBeforeTraktRequest() {}
//
//	@Override
//	public void onAfterTraktRequest(boolean success) {}
//
//	@Override
//	public void onErrorTraktRequest(Exception e) {}
}
