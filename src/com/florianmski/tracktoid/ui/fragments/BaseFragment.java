package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;
import com.florianmski.tracktoid.StatusView;
import com.florianmski.tracktoid.db.DatabaseWrapper;

public abstract class BaseFragment extends SherlockFragment
{
	private StatusView sv;
	private boolean restoreStateCalled = false;
	private DatabaseWrapper dbw = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
		
		if(savedInstanceState != null)
		{
			onRestoreState(savedInstanceState);
			restoreStateCalled = true;
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		//in case the fragment use setRetainInstance(true)
		if(savedInstanceState != null && !restoreStateCalled)
		{
			onRestoreState(savedInstanceState);
			restoreStateCalled = true;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle toSave)
	{
		super.onSaveInstanceState(toSave);
		onSaveState(toSave);
	}
	
	@Override
	public void onViewCreated(View v, Bundle savedInstanceState)
	{
		super.onViewCreated(v, savedInstanceState);
		
		sv = StatusView.instantiate(v);
	}
	
	public StatusView getStatusView()
	{
		return sv;
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		if (dbw != null) 
		{
			dbw.close();
			dbw = null;
		}
	}

	protected DatabaseWrapper getDBWrapper() 
	{
		if (dbw == null)
			dbw = new DatabaseWrapper(getActivity());
		return dbw;
	}
	
	public abstract void onRestoreState(Bundle savedInstanceState);
	public abstract void onSaveState(Bundle toSave);
}
