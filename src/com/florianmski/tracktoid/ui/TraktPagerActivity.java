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

package com.florianmski.tracktoid.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class TraktPagerActivity extends TraktActivity implements OnPageChangeListener
{
	protected TitlePageIndicator pagerIndicatorTitle;
	protected CirclePageIndicator pagerIndicatorCircle;
	protected ViewPager viewPager;
	protected ImageView ivBackground;
	
	protected PagerAdapter adapter;
	
	protected int currentPagerPosition;	
	protected boolean titleIndicator = true;
	
    protected AQuery aq;

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager);
		
		viewPager = (ViewPager) findViewById(R.id.paged_view);
		pagerIndicatorTitle = (TitlePageIndicator) findViewById(R.id.page_indicator_title);
		pagerIndicatorCircle = (CirclePageIndicator) findViewById(R.id.page_indicator_circle);
		ivBackground = (ImageView) findViewById(R.id.imageViewBackground);
		
		//if we don't set an adapter and user go back home, an exception is raised because view pager can't save its state
		//(nullpointer exception)
		//so I set a dummy adapter before the real one is ready to be displayed
		viewPager.setAdapter(new PagerAdapter() 
		{	
			@Override
			public void startUpdate(View container) {}
			
			@Override
			public Parcelable saveState() {return null;}
			
			@Override
			public void restoreState(Parcelable state, ClassLoader loader) {}
			
			@Override
			public boolean isViewFromObject(View view, Object object) {return false;}
			
			@Override
			public Object instantiateItem(View container, int position) {return null;}
			
			@Override
			public int getCount() {return 0;}
			
			@Override
			public void finishUpdate(View container) {}
			
			@Override
			public void destroyItem(View container, int position, Object object) {}
		});
		
		aq = new AQuery(this);
	}
	
	protected void setBackground(Image i)
	{
        aq.id(ivBackground).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);
	}
	
	protected void initPagerActivity(PagerAdapter adapter)
	{
		this.adapter = adapter;
		
		currentPagerPosition = getIntent().getIntExtra("position", 0);
		
		viewPager.setAdapter(adapter);

		onPageSelected(currentPagerPosition);
		
		ivBackground.setScaleType(ScaleType.CENTER_CROP);

		if(titleIndicator)
		{
			pagerIndicatorTitle.setViewPager(viewPager);
			pagerIndicatorTitle.setCurrentItem(currentPagerPosition);
			pagerIndicatorTitle.setOnPageChangeListener(this);
			
			pagerIndicatorTitle.setBackgroundColor(Color.parseColor("#5F000000"));
			pagerIndicatorTitle.setFooterColor(Color.WHITE);
			
			pagerIndicatorTitle.setVisibility(View.VISIBLE);
		}
		else
		{
			pagerIndicatorCircle.setViewPager(viewPager);
			pagerIndicatorCircle.setCurrentItem(currentPagerPosition);
			pagerIndicatorCircle.setOnPageChangeListener(this);
						
			pagerIndicatorCircle.setVisibility(View.VISIBLE);
		}
	}
	
	//if true, viewpager will use a titleindicator, if false a circleindicator
	public void setTitleIndicator(boolean titleIndicator)
	{
		this.titleIndicator = titleIndicator;
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset,	int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) 
	{
		currentPagerPosition = position;
	}
}