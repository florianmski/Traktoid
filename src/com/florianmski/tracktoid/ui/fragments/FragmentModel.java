package com.florianmski.tracktoid.ui.fragments;

import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;

public class FragmentModel extends TraktFragment
{
	public static FragmentModel newInstance(Bundle args)
	{
		FragmentModel f = new FragmentModel();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_about, null);
		
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, 0, 0, "OK")
			.setIcon(R.drawable.icon)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == 0) 
		{	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		// TODO Auto-generated method stub
	}
}
