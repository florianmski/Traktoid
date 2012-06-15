package com.florianmski.tracktoid.adapters.pagers;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.florianmski.tracktoid.R;

public class PagerDashboardAdapter extends PagerAdapter
{
	private final static int layoutIds[] = {R.layout.pager_item_dashboard_1};
	private final static int buttonsIds[] = {R.id.home_btn_calendar, R.id.home_btn_myshows, R.id.home_btn_recommendations, R.id.home_btn_search, R.id.home_btn_trending};
	private onDashboardButtonClicked listener;	
	private OnClickListener dashboardButtonListener = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			listener.onClick(v.getId());
		}
	};

	public PagerDashboardAdapter(onDashboardButtonClicked listener) 
	{
		this.listener = listener;
	}

	@Override
	public int getCount() 
	{
		return layoutIds.length;
	}

	@Override
	public Object instantiateItem(View pager, int position) 
	{
		View v = LayoutInflater.from(((Fragment)listener).getActivity()).inflate(layoutIds[position], null);
		
		Button[] buttons = new Button[buttonsIds.length];
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i] = (Button)v.findViewById(buttonsIds[i]);
			if(buttons[i] != null)
				buttons[i].setOnClickListener(dashboardButtonListener);
		}
		
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
	
	public interface onDashboardButtonClicked
	{
		public void onClick(int buttonId);
	}

}
