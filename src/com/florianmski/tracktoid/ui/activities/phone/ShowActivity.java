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

import java.util.ArrayList;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.ui.fragments.pagers.ShowPagerFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowFragment;
import com.jakewharton.trakt.entities.TvShow;

public class ShowActivity extends TraktActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//		setContentView(R.layout.activity_show);
		setContentView(R.layout.activity_single_fragment);

		if(savedInstanceState == null)
		{
			Bundle b = getIntent().getExtras();
			SherlockFragment f = ShowPagerFragment.newInstance(b);
			if(b != null)
			{
				ArrayList<TvShow> result = (ArrayList<TvShow>) b.get(TraktoidConstants.BUNDLE_RESULTS);
				if(result != null && result.size() == 1)
					f = ShowFragment.newInstance(result.get(0));
			}

			setPrincipalFragment(f);
		}
	}
}
