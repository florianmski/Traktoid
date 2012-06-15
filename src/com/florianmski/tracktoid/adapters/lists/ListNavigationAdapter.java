package com.florianmski.tracktoid.adapters.lists;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.adapters.lists.ListNavigationAdapter.NavigationItem;
import com.florianmski.tracktoid.ui.activities.phone.CalendarActivity;
import com.florianmski.tracktoid.ui.activities.phone.LibraryActivity;
import com.florianmski.tracktoid.ui.activities.phone.RecommendationActivity;
import com.florianmski.tracktoid.ui.activities.phone.SearchActivity;
import com.florianmski.tracktoid.ui.activities.phone.TrendingActivity;

public class ListNavigationAdapter extends RootAdapter<NavigationItem>
{	
	public ListNavigationAdapter(Context context) 
	{
		super(context, getNavigationItems());
	}

	private static List<NavigationItem> getNavigationItems()
	{
		List<NavigationItem> items = new ArrayList<NavigationItem>();

//		items.add(new NavigationItem("Profile", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
//		items.add(new NavigationItem("History", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
		items.add(new NavigationItem("Library", R.drawable.ab_icon_arrow_right, LibraryActivity.class));
//		items.add(new NavigationItem("Ratings", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
//		items.add(new NavigationItem("Charts", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
//		items.add(new NavigationItem("Progress", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
//		items.add(new NavigationItem("Friends", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
//		items.add(new NavigationItem("Shouts", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
//		items.add(new NavigationItem("Watchlist", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
//		items.add(new NavigationItem("Lists", R.drawable.ab_icon_arrow_right, PagerLibraryFragment.class));
		items.add(new NavigationItem("Calendar", R.drawable.ab_icon_arrow_right, CalendarActivity.class));
		items.add(new NavigationItem("Trending", R.drawable.ab_icon_arrow_right, TrendingActivity.class));
		items.add(new NavigationItem("Recommendation", R.drawable.ab_icon_arrow_right, RecommendationActivity.class));
		items.add(new NavigationItem("Search", R.drawable.ab_icon_arrow_right, SearchActivity.class));

		return items;
	}
	
	@Override
	public View doGetView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null) 
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_navigation, parent, false);
			holder = new ViewHolder();
			holder.tvNavigation = (TextView)convertView.findViewById(R.id.textViewNavigation);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();
		
		NavigationItem item = getItem(position);
		
		holder.tvNavigation.setText(item.text);
		
		return convertView;
	}
	
	private static class ViewHolder 
	{
		private TextView tvNavigation;
	}
	
	public static class NavigationItem
	{
		public String text;
		public int resId;
		public Class<?> activityClass;
		
		public NavigationItem(String text, int resId, Class<?> activityClass)
		{
			this.text= text;
			this.resId = resId;
			this.activityClass = activityClass;
		}
	}

}
