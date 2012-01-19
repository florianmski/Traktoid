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
import android.support.v4.app.Fragment;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.TraktFragment.FragmentListener;
import com.flurry.android.FlurryAgent;

public class TraktActivity extends BaseActivity implements FragmentListener
{		
	@Override
	public void onStart()
	{
	   super.onStart();
	   FlurryAgent.onStartSession(this, getResources().getString(R.string.flurry_key));
	}
	
	@Override
	public void onStop()
	{
	   super.onStop();
	   FlurryAgent.onEndSession(this);
	}
	
	protected void setTitle(String title)
	{
		getSupportActionBar().setTitle(title);
	}
	
	protected void setSubtitle(String subtitle)
	{
		getSupportActionBar().setSubtitle(subtitle);
	}

	@Override
	public void onFragmentAction(Fragment f, Bundle bundle, int actionToPerformed)
	{
		
	}
}
