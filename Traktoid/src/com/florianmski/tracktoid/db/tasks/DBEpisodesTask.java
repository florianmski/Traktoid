package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.TvShowEpisode;

public class DBEpisodesTask extends DBTask<List<TvShowEpisode>>
{
	private String seasonId;
	
	public DBEpisodesTask(Context context, DBListener listener, String seasonId) 
	{
		super(context, listener);
		
		this.seasonId = seasonId;
	}
	
	@Override
	protected List<TvShowEpisode> doDBStuff() 
	{
		return dbw.getEpisodes(seasonId);
	}
	
	@Override
	protected void onCompleted(List<TvShowEpisode> result)
	{
		listener.onDBEpisodes(result);
	}
	
}
