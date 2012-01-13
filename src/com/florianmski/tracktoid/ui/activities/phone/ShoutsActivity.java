package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;

import com.florianmski.tracktoid.ui.fragments.ShoutsFragment;

public class ShoutsActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_shouts);
		if(savedInstanceState == null)
			setPrincipalFragment(ShoutsFragment.newInstance(getIntent().getExtras()));
	}
}
