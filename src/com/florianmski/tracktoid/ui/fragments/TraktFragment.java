package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;

public abstract class TraktFragment extends BaseFragment
{
	protected TraktManager tm = TraktManager.getInstance();
	protected TraktTask commonTask;
	
	public TraktFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		tm.addObserver(this);
	}

	@Override
	public void onDestroy()
	{
		//remove observer
//		tm.removeObserver(this);
		//cancel a task which is useless outside this activity
		if(commonTask != null)
			commonTask.cancel();
		super.onDestroy();
	}

//	@Override
//	public void onAfterTraktRequest(boolean success) {}
//
//	@Override
//	public void onBeforeTraktRequest() {}
//
//	@Override
//	public void onErrorTraktRequest(Exception e) 
//	{
//		StatusView sv = getStatusView();
//		if(sv != null)
//			sv.hide().text("Something goes wrong :/\n" + e.getMessage());
//	}

//	@Override
//	public void onShowRemoved(TvShow show) {}
//
//	@Override
//	public void onShowUpdated(TvShow show) {}
//	
//	@Override
//	public void onMovieRemoved(Movie movie) {}
//
//	@Override
//	public void onMovieUpdated(Movie movie) {}
//	
//	@Override
//	public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars) {}
}
