package com.florianmski.tracktoid.ui.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;

public class AboutFragment extends BaseFragment
{
	private ImageView ivNyan;
	
	public static AboutFragment newInstance(Bundle args)
	{
		AboutFragment f = new AboutFragment();
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
		
		//prepare nyan cat animation
		final AnimationDrawable animation = Utils.getNyanCat(getActivity());

		ivNyan.setBackgroundDrawable(animation);
		
		// run the start() method later on the UI thread
		ivNyan.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				animation.start();
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_about, null);
		
		ivNyan =  (ImageView) v.findViewById(R.id.imageViewNyan);
		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
		ivNyan.setLayoutParams(params);
		
		return v;
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
}
