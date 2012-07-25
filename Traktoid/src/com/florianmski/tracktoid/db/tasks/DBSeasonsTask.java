package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;
import com.jakewharton.trakt.entities.TvShowSeason;

public class DBSeasonsTask extends DBTask<List<TvShowSeason>>
{
	private String tvdbId;
	private boolean getEpisodesToo;
	private boolean orderByASC;
	
	public DBSeasonsTask(Context context, String tvdbId, boolean getEpisodesToo, boolean orderByASC, DBListener listener) 
	{
		super(context, listener);
		
		this.tvdbId = tvdbId;
		this.getEpisodesToo = getEpisodesToo;
		this.orderByASC = orderByASC;
	}
	
	@Override
	protected List<TvShowSeason> doDBStuff() 
	{
		return dbw.getSeasons(tvdbId, getEpisodesToo, orderByASC);
	}
	
	@Override
	protected void onCompleted(List<TvShowSeason> result)
	{
		listener.onDBSeasons(result);
	}
	
}

