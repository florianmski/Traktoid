package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;

import com.florianmski.tracktoid.StatusView;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.TraktManager.TraktListener;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.entities.TvShow;

public abstract class TraktFragment extends BaseFragment implements TraktListener
{
	protected TraktManager tm = TraktManager.getInstance();
	protected TraktTask commonTask;

	public TraktFragment() 
	{

	}

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

	protected void setTitle(String title)
	{
		getSupportActivity().getSupportActionBar().setTitle(title);
	}

	protected void setSubtitle(String subtitle)
	{
		getSupportActivity().getSupportActionBar().setSubtitle(subtitle);
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
		StatusView sv = getStatusView();
		if(sv != null)
		{
			if(!Utils.isOnline(getActivity()))
				sv.hide().text("Internet connection required!");
			else
				sv.hide().text("Something goes wrong :/\n" + e.getMessage());
		}
	}

	@Override
	public void onShowRemoved(TvShow show) {}

	@Override
	public void onShowUpdated(TvShow show) {}
}
