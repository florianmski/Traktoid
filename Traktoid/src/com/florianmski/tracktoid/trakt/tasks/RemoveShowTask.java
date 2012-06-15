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

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.entities.TvShow;

public class RemoveShowTask extends TraktTask
{
	private TvShow show;

	public RemoveShowTask(TraktManager tm, Fragment fragment, TvShow show) 
	{
		super(tm, fragment);

		this.show = show;
	}

	@Override
	protected boolean doTraktStuffInBackground()
	{
		showToast("Removing " + show.title + "...", Toast.LENGTH_SHORT);
		
		//delete only locally
//		tm.showService().unlibrary(Integer.valueOf(show.getTvdbId())).fire();

		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();

		dbw.removeShow(show.tvdbId);
		
		dbw.close();
		
		showToast(show.title + " removed!", Toast.LENGTH_SHORT);
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success)
			tm.onShowRemoved(show);
	}

}
