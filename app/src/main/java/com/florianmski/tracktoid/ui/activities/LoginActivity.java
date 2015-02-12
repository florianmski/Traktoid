package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.login.LoginFragment;

public class LoginActivity extends TranslucentActivity
{
    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_login;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
        {
            LoginFragment f = LoginFragment.newInstance(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_login, f, null).commit();
        }
	}

    public static void launch(Activity a)
    {
        launchActivity(a, LoginActivity.class, null);
    }
}