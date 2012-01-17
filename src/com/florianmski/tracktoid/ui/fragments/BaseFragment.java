package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.florianmski.tracktoid.StatusView;

public abstract class BaseFragment extends Fragment
{
	private StatusView sv;
	private boolean restoreStateCalled = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
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
	
	public abstract void onRestoreState(Bundle savedInstanceState);
	public abstract void onSaveState(Bundle toSave);
}
