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

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.TvShow;

public class ShowsTask extends TraktTask
{
	private ArrayList<TvShow> shows = new ArrayList<TvShow>();
	private TraktApiBuilder<?> builder;
	private boolean sort;
	private ShowsListener listener;
	
	public ShowsTask(TraktManager tm, Context context, ShowsListener listener, TraktApiBuilder<?> builder, boolean sort) 
	{
		super(tm, context);
		
		this.builder = builder;
		this.sort = sort;
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doTraktStuffInBackground()
	{
		this.publishProgress("toast", "0", "Retrieving a list of shows...");
		
		shows = (ArrayList<TvShow>) builder.fire();
		
		if(sort)
			Collections.sort(shows);
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success)
			listener.onShows(shows);
	}
	
	public interface ShowsListener
	{
		public void onShows(ArrayList<TvShow> shows);
	}
}
