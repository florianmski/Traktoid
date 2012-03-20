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

import com.actionbarsherlock.view.Window;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.ui.fragments.ShowFragment;

public class MyShowActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_my_show);
		setContentView(R.layout.activity_single_fragment);
		
		if(Utils.isLandscape(this))
		{
			finish();
			return;
		}
		
		if (savedInstanceState == null) 
		{
//            getSupportFragmentManager()
//             .beginTransaction()
//             .add(android.R.id.content, new MyShowFragment())
//             .commit();
			setPrincipalFragment(ShowFragment.newInstance(getIntent().getExtras()));
        }
	}
}
