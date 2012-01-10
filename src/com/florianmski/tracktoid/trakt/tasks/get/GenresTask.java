package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.entities.Genre;

public class GenresTask extends TraktTask
{
	private ArrayList<Genre> genres = new ArrayList<Genre>();
	private GenresListener listener;
	
	public GenresTask(TraktManager tm, Fragment fragment, GenresListener listener) 
	{
		super(tm, fragment);
		
		this.listener = listener;
	}

	@Override
	protected boolean doTraktStuffInBackground() 
	{
		genres = (ArrayList<Genre>) tm.genreService().shows().fire();
		
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
		public void onGenres(ArrayList<Genre> genres);
	}

}
