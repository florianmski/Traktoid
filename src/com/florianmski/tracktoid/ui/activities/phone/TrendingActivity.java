package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.TrendingFragment;

public class TrendingActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trending);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_trending, TrendingFragment.newInstance(getIntent().getExtras())).commit();
	}
}
