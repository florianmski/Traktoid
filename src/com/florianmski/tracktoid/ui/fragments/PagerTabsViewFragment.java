package com.florianmski.tracktoid.ui.fragments;


import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import com.florianmski.tracktoid.R;


public abstract class PagerTabsViewFragment extends TraktFragment 
{
	protected TabHost mTabHost;
	protected ViewPager mViewPager;
	protected TabsViewAdapter mTabsAdapter;

	public abstract View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
	public abstract TabsViewAdapter getAdapter();

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

//		mTabsAdapter = new TabsViewAdapter(getActivity(), mTabHost, mViewPager);
		mTabsAdapter = getAdapter();

		if (savedInstanceState != null)
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = getView(inflater, container, savedInstanceState);

		mTabHost = (TabHost)v.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager)v.findViewById(R.id.pager);

		return v;
	}

	public static abstract class TabsViewAdapter extends PagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener 
	{
		protected final Context mContext;
		protected final TabHost mTabHost;
		protected final ViewPager mViewPager;
		protected final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		protected static final class TabInfo 
		{
			private final int layoutId;
			
			TabInfo(String _tag, int _layoutId, Bundle _args) 
			{
				layoutId = _layoutId;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory 
		{
			private final Context mContext;

			public DummyTabFactory(Context context) 
			{
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) 
			{
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsViewAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) 
		{
			super();
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(TabsViewAdapter.this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, int layoutId, Bundle args) 
		{
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, layoutId, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			return mTabs.size();
		}
		
		public abstract void fillLayout(View v, int layoutId); 
		
		@Override
		public Object instantiateItem(View pager, int position) 
		{
			TabInfo info = mTabs.get(position);

			View v = LayoutInflater.from(mContext).inflate(info.layoutId, null);
			
			fillLayout(v, info.layoutId);
			
			((ViewPager)pager).addView(v);
			return v;
		}

		@Override
		public void destroyItem(View pager, int position, Object v) 
		{
			((ViewPager) pager).removeView((View) v);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) 
		{
			return view == object;
		}

		@Override
		public Parcelable saveState() 
		{
			return null;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public void startUpdate(View arg0) {}

		@Override
		public void finishUpdate(View arg0) {}

		@Override
		public void onTabChanged(String tabId) 
		{
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

		@Override
		public void onPageSelected(int position) 
		{
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onPageScrollStateChanged(int state) {}
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		toSave.putString("tab", mTabHost.getCurrentTabTag());
	}

}
