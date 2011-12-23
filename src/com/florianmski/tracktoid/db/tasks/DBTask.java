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

package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public class DBTask extends AsyncTask<Void, String, Boolean>
{
	protected DBListener listener;
	protected Context context;
	protected DatabaseWrapper dbw;

	public DBTask(Context context, DBListener listener)
	{
		this.listener = listener;
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) 
	{		
		dbw = new DatabaseWrapper(context);
		dbw.open();
		
		try
		{
			doDBStuff();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		dbw.close();
		return true;
	}
	
	protected void doDBStuff() {}
	
	public interface DBListener
	{
		public void onDBShows(List<TvShow> shows);
		public void onDBSeasons(List<TvShowSeason> seasons);
		public void onDBEpisodes(List<TvShowEpisode> episodes);
	}

}
