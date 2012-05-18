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

import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.tracktoid.ui.fragments.recommendations.RecommendationMoviesFragment;
import com.florianmski.tracktoid.ui.fragments.recommendations.RecommendationShowsFragment;

public class RecommendationActivity extends MovieShowActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_recommendation);
//		setContentView(R.layout.activity_single_fragment);
//		
//		if(savedInstanceState == null)
//			setPrincipalFragment(RecommendationFragment.newInstance(getIntent().getExtras()));
	}

	@Override
	public TraktFragment getMovieFragment() 
	{
		return RecommendationMoviesFragment.newInstance(getIntent().getExtras());
	}

	@Override
	public TraktFragment getShowFragment() 
	{
		return RecommendationShowsFragment.newInstance(getIntent().getExtras());
	}
}
