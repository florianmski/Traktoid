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
import android.widget.Toast;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.entities.Movie;

public class RemoveMovieTask extends TraktTask<Movie>
{
	private Movie movie;

	public RemoveMovieTask(TraktManager tm, Context context, Movie movie) 
	{
		super(context);

		this.movie = movie;
	}

	@Override
	protected Movie doTraktStuffInBackground()
	{
		//TODO
		//delete only locally
		//		tm.showService().unlibrary(Integer.valueOf(show.getTvdbId())).fire();

		DatabaseWrapper dbw = new DatabaseWrapper(context);

		dbw.removeMovie(movie.url);

		dbw.close();

		showToast(movie.title + " removed!", Toast.LENGTH_SHORT);

		return movie;
	}

	@Override
	protected void sendEvent(Movie result) 
	{
		TraktTask.traktItemRemoved(movie);
	}

}
