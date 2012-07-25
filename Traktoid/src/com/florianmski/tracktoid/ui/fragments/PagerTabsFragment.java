package com.florianmski.tracktoid.ui.fragments;


import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.florianmski.tracktoid.R;


public abstract class PagerTabsFragment extends TraktFragment implements ViewPager.OnPageChangeListener 
{
	protected ViewPager mViewPager;
	protected TabsAdapter mTabsAdapter;

	public abstract View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState != null)
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab"));
		
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = getView(inflater, container, savedInstanceState);

		mViewPager = (ViewPager)v.findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(getActivity(), mViewPager);

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle toSave)
	{
		toSave.putInt("tab", getActionBar().getSelectedTab().getPosition());
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int position) 
	{
		getActionBar().setSelectedNavigationItem(position);
	}

	public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener
	{
		private final Context mContext;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		final class TabInfo 
		{
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) 
			{
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(FragmentActivity activity, ViewPager pager) 
		{
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mViewPager = pager;
			new setAdapterTask().execute();
		}

		public void addTab(String name, Class<?> clss, Bundle args) 
		{
			TabInfo info = new TabInfo(name, clss, args);
			mTabs.add(info);
			ActionBar.Tab tab = PagerTabsFragment.this.getActionBar().newTab();
            tab.setText(name);
            tab.setTabListener(this);
            PagerTabsFragment.this.getActionBar().addTab(tab);
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
	    public void onTabSelected(Tab tab, FragmentTransaction transaction) 
	    {
	    	int position = PagerTabsFragment.this.getActionBar().getSelectedTab().getPosition();
			mViewPager.setCurrentItem(position);
	    }
		
		@Override
	    public void onTabReselected(Tab tab, FragmentTransaction transaction) {}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

		private class setAdapterTask extends AsyncTask<Void,Void,Void>
		{
			@Override
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
}
