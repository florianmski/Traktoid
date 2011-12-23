package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class RateTask extends PostTask
{
	private TvShow show;
	private Rating rating;

	public RateTask(TraktManager tm, Fragment fragment, TvShow show, Rating rating) 
	{
		super(tm, fragment, tm.rateService().show(show.title, show.year).rating(rating), null);

		this.show = show;
		this.rating = rating;
	}

	@Override
	protected void doAfterPostStuff()
	{
		show.rating = rating;

		DatabaseWrapper dbw = new DatabaseWrapper(context);
		dbw.open();
		dbw.insertOrUpdateShow(show);
		dbw.close();
	}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(success)
			tm.onShowUpdated(show);
	}

}
