package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.util.AttributeSet;

public abstract class BaseFragment extends Fragment
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState != null)
		{
			onRestoreState(savedInstanceState);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle toSave)
	{
		super.onSaveInstanceState(toSave);
		onSaveState(toSave);
	}
	
	public abstract void onRestoreState(Bundle savedInstanceState);
	public abstract void onSaveState(Bundle toSave);
}
