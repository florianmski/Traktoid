package com.florianmski.tracktoid.ui.fragments;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;

public class AboutFragment extends Fragment
{
	private QuickAction qa;
	private ImageView ivNyan;
	
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
		
		qa = new QuickAction(getActivity());
		qa.addActionItem(new ActionItem(0, "Nyan!"));
		
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
		
		ivNyan.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				qa.show(v);
			}
		});
	}

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
