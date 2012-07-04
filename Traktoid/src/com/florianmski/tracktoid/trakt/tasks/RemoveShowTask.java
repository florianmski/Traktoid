package com.florianmski.tracktoid.trakt.tasks;

import android.content.Context;
import android.widget.Toast;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.TvShow;

public class RemoveShowTask extends TraktTask<TvShow>
{
	private TvShow show;

	public RemoveShowTask(Context context, TvShow show) 
	{
		super(context);

		this.show = show;
	}

	@Override
	protected TvShow doTraktStuffInBackground()
	{
		showToast("Removing " + show.title + "...", Toast.LENGTH_SHORT);
		
		//TODO
		//delete only locally
//		tm.showService().unlibrary(Integer.valueOf(show.getTvdbId())).fire();

		DatabaseWrapper dbw = new DatabaseWrapper(context);

		dbw.removeShow(show.url);
		
		dbw.close();
		
		showToast(show.title + " removed!", Toast.LENGTH_SHORT);
		
		return show;
	}
	
	@Override
	protected void sendEvent(TvShow result) 
	{
		TraktTask.traktItemRemoved(show);
	}

}
