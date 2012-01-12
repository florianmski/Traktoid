package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity
{
	public void setPrincipalFragment(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment, null).commit();
	}
}
