package com.florianmski.tracktoid.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class PagerFragment extends TraktFragment implements OnPageChangeListener
{
	public final static int IT_TITLE = 0;
	public final static int IT_CIRCLE = 1;
	public final static int IT_TAB = 2;

	protected PageIndicator pageIndicator;
	protected ViewPager viewPager;
	protected ImageView ivBackground;

	protected PagerAdapter adapter;

	protected int currentPagerPosition;	
	protected int indicatorType = IT_TITLE;

	protected AQuery aq;

	public PagerFragment() {}

	public PagerFragment(FragmentListener listener) 
	{
		super(listener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		//if we don't set an adapter and user go back home, an exception is raised because view pager can't save its state
		//(nullpointer exception)
		//so I set a dummy adapter before the real one is ready to be displayed
//		viewPager.setAdapter(new PagerAdapter() 
//		{	
//			@Override
//			public void startUpdate(View container) {}
//
//			@Override
//			public Parcelable saveState() {return null;}
//
//			@Override
//			public void restoreState(Parcelable state, ClassLoader loader) {}
//
//			@Override
//			public boolean isViewFromObject(View view, Object object) {return false;}
//
//			@Override
//			public Object instantiateItem(View container, int position) {return null;}
//
//			@Override
//			public int getCount() {return 0;}
//
//			@Override
//			public void finishUpdate(View container) {}
//
//			@Override
//			public void destroyItem(View container, int position, Object object) {}
//		});

		aq = new AQuery(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_pager, null);

		viewPager = (ViewPager) v.findViewById(R.id.paged_view);
		
		switch(indicatorType)
		{
		case IT_TITLE :
			pageIndicator = (TitlePageIndicator) v.findViewById(R.id.page_indicator_title);
			break;
		case IT_CIRCLE :
			pageIndicator = (CirclePageIndicator) v.findViewById(R.id.page_indicator_circle);
			break;
		case IT_TAB :
			pageIndicator = (TabPageIndicator) v.findViewById(R.id.page_indicator_tab);
			break;
		}
		ivBackground = (ImageView) v.findViewById(R.id.imageViewBackground);

		return v;
	}

	protected void setBackground(Image i)
	{
		aq.id(ivBackground).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);
	}

	protected void initPagerFragment(PagerAdapter adapter)
	{
		this.adapter = adapter;

		currentPagerPosition = getActivity().getIntent().getIntExtra("position", 0);

		viewPager.setAdapter(adapter);

		onPageSelected(currentPagerPosition);

		ivBackground.setScaleType(ScaleType.CENTER_CROP);

		switch(indicatorType)
		{
		case IT_TITLE :
			pageIndicator.setViewPager(viewPager);

			((TitlePageIndicator)pageIndicator).setVisibility(View.VISIBLE);
			break;
		case IT_TAB :
			((TabPageIndicator)pageIndicator).setViewPager(viewPager);

			((TabPageIndicator)pageIndicator).setVisibility(View.VISIBLE);
			break;
		case IT_CIRCLE :
			((CirclePageIndicator)pageIndicator).setViewPager(viewPager);

			if(viewPager.getChildCount() > 1)
				((CirclePageIndicator)pageIndicator).setVisibility(View.VISIBLE);
			break;
		}
		
		pageIndicator.setCurrentItem(currentPagerPosition);
		pageIndicator.setOnPageChangeListener(this);
	}

	public void setPageIndicatorType(int it)
	{
		this.indicatorType = it;
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
