package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;

public abstract class TraktFragment extends BaseFragment
{
	protected TraktManager tm = TraktManager.getInstance();
	
	public TraktFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}
