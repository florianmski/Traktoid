package com.florianmski.tracktoid.ui.activities.phone;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.florianmski.tracktoid.R;

public class BaseActivity extends FragmentActivity
{
	public void setPrincipalFragment(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, null).commit();
	}
}
