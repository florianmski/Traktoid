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

import com.jakewharton.trakt.entities.Movie;

public class DBMoviesTask extends DBTask
{
	private List<Movie> movies;
	
	public DBMoviesTask(Context context, DBListener listener) 
	{
		super(context, listener);
	}
	
	@Override
	protected void doDBStuff() 
	{
		movies = dbw.getMovies();		
	}
	
	@Override
	protected void onPostExecute (Boolean success)
	{
		listener.onDBMovies(movies);
	}
	
}
