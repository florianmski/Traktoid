package com.florianmski.tracktoid.ui.activities;

import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.trending.TrendingFragment;

public class TrendingActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trending);
		
		if(savedInstanceState == null)
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_trending, TrendingFragment.newInstance(getIntent().getExtras())).commit();
	}
}
