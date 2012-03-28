package com.florianmski.tracktoid.ui.fragments.pagers;


import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;


public abstract class TabsPagerFragment extends TraktFragment 
{
	protected TabHost mTabHost;
	protected ViewPager mViewPager;
	protected TabsAdapter mTabsAdapter;

	public abstract View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

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

		mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);

		if (savedInstanceState != null)
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
	}

//	public void initMoreViews(View v){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = getView(inflater, container, savedInstanceState);

		mTabHost = (TabHost)v.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager)v.findViewById(R.id.pager);

//		initMoreViews(v);

		return v;
	}

	public static class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener 
	{
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo 
		{
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) 
			{
				tag = _tag;
				clss = _class;
				args = _args;
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

		public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) 
		{
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
//			new setAdapterTask().execute();
			mViewPager.setAdapter(TabsAdapter.this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) 
		{
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) 
		{
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		@Override
		public void onTabChanged(String tabId) 
		{
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) 
		{
		}

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
		

		private class setAdapterTask extends AsyncTask<Void,Void,Void>
		{
			protected Void doInBackground(Void... params) 
			{
				return null;
			}

			@Override
			protected void onPostExecute(Void result) 
			{
				mViewPager.setAdapter(TabsAdapter.this);
			}
		}
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		toSave.putString("tab", mTabHost.getCurrentTabTag());
	}

}
