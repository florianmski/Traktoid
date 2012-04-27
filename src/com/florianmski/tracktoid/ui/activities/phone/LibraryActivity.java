package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;

import com.florianmski.tracktoid.ui.fragments.pagers.LibraryFragment;

public class LibraryActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_shows);

		if (savedInstanceState == null)
			setPrincipalFragment(LibraryFragment.newInstance(getIntent().getExtras()));
	}
}