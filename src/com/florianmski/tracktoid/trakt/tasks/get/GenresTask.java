package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Genre;

public class GenresTask extends TraktTask
{
	private List<Genre> genres = new ArrayList<Genre>();
	private GenresListener listener;
	private TraktApiBuilder<List<Genre>> builder;
	
	public <T extends TraktoidInterface<T>> GenresTask(TraktManager tm, Fragment fragment, GenresListener listener, TraktApiBuilder<List<Genre>> builder) 
	{
		super(tm, fragment);
		
		this.listener = listener;
		this.builder = builder;
	}

	@Override
	protected boolean doTraktStuffInBackground() 
	{
		genres = builder.fire();
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success && !Utils.isActivityFinished(fragment.getActivity()))
			listener.onGenres(genres);
	}
	
	public interface GenresListener
	{
		public void onGenres(List<Genre> genres);
	}

}
