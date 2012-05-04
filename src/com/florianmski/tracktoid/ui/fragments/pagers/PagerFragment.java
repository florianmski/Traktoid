package com.florianmski.tracktoid.ui.fragments.pagers;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
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

	public static PagerFragment newInstance(Bundle args)
	{
		PagerFragment f = new PagerFragment();
		f.setArguments(args);
		return f;
	}
	
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

	protected void setBackground(TraktImage i)
	{
		aq.id(ivBackground).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);
	}

	protected void initPagerFragment(PagerAdapter adapter)
	{
		this.adapter = adapter;

		currentPagerPosition = getArguments() == null ? 0 : getArguments().getInt(TraktoidConstants.BUNDLE_POSITION, 0);
		currentPagerPosition = currentPagerPosition < 0 ? 0 : (currentPagerPosition >= adapter.getCount() ? adapter.getCount() - 1 : currentPagerPosition);

		viewPager.setAdapter(adapter);

		onPageSelected(currentPagerPosition);

		ivBackground.setScaleType(ScaleType.CENTER_CROP);

		switch(indicatorType)
		{
		case IT_TITLE :
			pageIndicator.setViewPager(viewPager, currentPagerPosition);

			((TitlePageIndicator)pageIndicator).setVisibility(View.VISIBLE);
			break;
		case IT_TAB :
			((TabPageIndicator)pageIndicator).setViewPager(viewPager, currentPagerPosition);

			((TabPageIndicator)pageIndicator).setVisibility(View.VISIBLE);
			break;
		case IT_CIRCLE :
			((CirclePageIndicator)pageIndicator).setViewPager(viewPager, currentPagerPosition);

			if(viewPager.getChildCount() > 1)
				((CirclePageIndicator)pageIndicator).setVisibility(View.VISIBLE);
			break;
		}
		
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

	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		
	}
}
