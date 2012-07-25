package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.entities.TvShowEpisode;

public class DBSeasonTask  extends DBTask<List<TvShowEpisode>>
{
	private String seasonId;
	
	public DBSeasonTask(Context context, String seasonId, DBListener listener) 
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
		listener.onDBSeason(result);
	}

}
