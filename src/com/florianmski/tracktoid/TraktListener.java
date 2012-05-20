package com.florianmski.tracktoid;

import java.util.ArrayList;

import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;

public interface TraktListener
{
	public void onBeforeTraktRequest();
	public void onAfterTraktRequest(boolean success);
	public void onErrorTraktRequest(Exception e);
	public void onShowUpdated(TvShow show);
	public void onShowRemoved(TvShow show);
	public void onMovieUpdated(Movie movie);
	public void onMovieRemoved(Movie movie);
	public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars);
}
