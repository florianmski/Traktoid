package com.florianmski.tracktoid.trakt.tasks;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class RateTask extends TraktTask
{
	private TvShow show;
	private Rating rating;
	
	public RateTask(TraktManager tm, Fragment fragment, TvShow show, Rating rating) 
	{
		super(tm, fragment);
		
		this.show = show;
		this.rating = rating;
	}
	
	@Override
	protected void doTraktStuffInBackground() 
	{
		this.publishProgress("toast", "0", "Sending...");
		
		tm.rateService().show(show.getTitle(), show.getYear()).rating(rating).fire();
		show.setRating(rating);
		
		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();
		dbw.insertOrUpdateShow(show);
		dbw.close();
		
		this.publishProgress("toast", "0", "You now " + rating.name().toLowerCase() + " " + show.getTitle() +"!");
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success)
			tm.onShowUpdated(show);
	}

}
