package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import com.florianmski.tracktoid.db.tasks.DBTask.DBListener;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public abstract class DBAdapter implements DBListener
{
	@Override
	public void onDBShows(List<TvShow> shows) {}
	@Override
	public void onDBMovies(List<Movie> movies) {}
	@Override
	public void onDBSeasons(List<TvShowSeason> seasons) {}
	@Override
	public void onDBSeason(List<TvShowEpisode> episodes) {}
	@Override
	public void onDBEpisodes(List<TvShowEpisode> episodes) {}
	@Override
	public void onDBCalendar(List<CalendarDate> calendar) {}
}
