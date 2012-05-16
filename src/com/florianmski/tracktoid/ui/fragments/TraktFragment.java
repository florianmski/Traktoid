package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.StatusView;
import com.florianmski.tracktoid.TraktListener;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;

public abstract class TraktFragment extends BaseFragment implements TraktListener
{
	protected TraktManager tm = TraktManager.getInstance();
	protected TraktTask commonTask;
	
	public TraktFragment() {}

	//TODO keep it ?
	public TraktFragment(FragmentListener listener)
	{
		super();
//		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		tm.addObserver(this);
	}

	@Override
	public void onDestroy()
	{
		//remove observer
		tm.removeObserver(this);
		//cancel a task which is useless outside this activity
		if(commonTask != null)
			commonTask.cancel(true);
		super.onDestroy();
	}

	public interface FragmentListener
	{
		public void onFragmentAction(Fragment f, Bundle bundle, int actionToPerformed);
	}

	@Override
	public void onAfterTraktRequest(boolean success) {}

	@Override
	public void onBeforeTraktRequest() {}

	@Override
	public void onErrorTraktRequest(Exception e) 
	{
		//TODO
		StatusView sv = getStatusView();
		if(sv != null)
		{
			sv.hide().text("Something goes wrong :/\n" + e.getMessage());
//			sv.hide().text(null);
		}
	}

	@Override
	public void onShowRemoved(TvShow show) {}

	@Override
	public void onShowUpdated(TvShow show) {}
	
	@Override
	public void onMovieRemoved(Movie movie) {}

	@Override
	public void onMovieUpdated(Movie movie) {}
	
	@Override
	public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars) {}
}
