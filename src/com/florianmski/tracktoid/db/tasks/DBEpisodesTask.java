package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.TvShowEpisode;

public class DBEpisodesTask extends DBTask
{
	private List<TvShowEpisode> episodes;
	private String seasonId;
	private String tvdbId;
	
	public DBEpisodesTask(Context context, DBListener listener, String seasonId, String tvdbId) 
	{
		super(context, listener);
		
		this.seasonId = seasonId;
		this.tvdbId = tvdbId;
	}
	
	@Override
	protected void doDBStuff() 
	{
		episodes = dbw.getEpisodes(seasonId, tvdbId);
	}
	
	@Override
	protected void onPostExecute (Boolean success)
	{
		listener.onDBEpisodes(episodes);
	}
	
}
