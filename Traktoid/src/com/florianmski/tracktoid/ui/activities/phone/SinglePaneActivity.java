/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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