package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.AboutFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.LoginPagerFragment;

public class LoginActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_login);
		setPrincipalFragment(LoginPagerFragment.newInstance(getIntent().getExtras()));
	}
}