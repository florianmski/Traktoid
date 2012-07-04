package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import android.content.Context;
import com.jakewharton.trakt.entities.TvShowSeason;

public class DBSeasonsTask extends DBTask
{
	private List<TvShowSeason> seasons;
	private String tvdbId;
	private boolean getEpisodesToo;
	private boolean orderByASC;
	
	public DBSeasonsTask(Context context, DBListener listener, String tvdbId, boolean getEpisodesToo, boolean orderByASC) 
	{
		super(context, listener);
		
		this.tvdbId = tvdbId;
		this.getEpisodesToo = getEpisodesToo;
		this.orderByASC = orderByASC;
	}
	
	@Override
	protected void doDBStuff() 
	{
		seasons = dbw.getSeasons(tvdbId, getEpisodesToo, orderByASC);
	}
	
	@Override
	protected void onPostExecute (Boolean success)
	{
		listener.onDBSeasons(seasons);
	}
	
}

