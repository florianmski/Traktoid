package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;

public abstract class SinglePaneActivity extends MenuActivity
{	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_fragment);

		getAnimationLayout();

		if(savedInstanceState == null)
		{
			setPrincipalFragment(getFragment());

			if(getIntent().getBooleanExtra(TraktoidConstants.BUNDLE_SLIDE_OPEN, false))
			{
				mLayout.openSidebar(false);

				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						mLayout.closeSidebar(true);
					}
				}, 1000);
			}
		}
		else
		{
			if(savedInstanceState.getBoolean(TraktoidConstants.BUNDLE_SLIDE_OPEN, false))
				mLayout.openSidebar(false);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(TraktoidConstants.BUNDLE_SLIDE_OPEN, mLayout.isOpening());
	}

	public abstract Fragment getFragment();
}