package com.florianmski.tracktoid.trakt.tasks;

import android.app.Activity;
import android.widget.Toast;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.TvShow;

public class RemoveShowTask extends TraktTask<TvShow>
{
	private TvShow show;

	public RemoveShowTask(Activity context, TvShow show) 
	{
		super(context);

		this.show = show;
	}

	@Override
	protected TvShow doTraktStuffInBackground()
	{
		DatabaseWrapper dbw = new DatabaseWrapper(context);

		dbw.removeShow(show.url);
		
		dbw.close();
		
		return show;
	}
	
	@Override
	protected void sendEvent(TvShow result) 
	{
		TraktTask.traktItemRemoved(show);
	}

}
