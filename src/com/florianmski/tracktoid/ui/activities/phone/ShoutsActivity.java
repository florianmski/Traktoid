package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.ShoutsFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.EpisodeFragment;

public class ShoutsActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_shouts);
		setPrincipalFragment(ShoutsFragment.newInstance(getIntent().getExtras()));
	}
}
