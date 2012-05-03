package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerDetailsAdapter;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.trakt.tasks.post.InCollectionTask;
import com.florianmski.tracktoid.trakt.tasks.post.InWatchlistTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.phone.ShoutsActivity;
import com.florianmski.tracktoid.ui.fragments.pagers.TabsViewPagerFragment;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.tracktoid.widgets.ScrollingTextView;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Response;

public abstract class PagerItemTraktFragment<T extends TraktoidInterface<T>> extends TabsViewPagerFragment
{
	protected T item;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		getSherlockActivity().invalidateOptionsMenu();

		mTabsAdapter.addTab(mTabHost.newTabSpec("summary").setIndicator("Summary"), R.layout.pager_item_details_summary, null);
		//		mTabsAdapter.addTab(mTabHost.newTabSpec("shouts").setIndicator("Shouts"), R.layout.fragment_shouts, null);
		//		mTabsAdapter.addTab(mTabHost.newTabSpec("premieres2").setIndicator("Premieres2"), 0, null);
		//		mTabsAdapter.addTab(mTabHost.newTabSpec("premieres3").setIndicator("Premieres3"), 0, null);
		//		mTabsAdapter.addTab(mTabHost.newTabSpec("premieres4").setIndicator("Premieres4"), 0, null);
	}

	@Override
	public TabsViewAdapter getAdapter()
	{
		return new PagerDetailsAdapter<T>(getActivity(), mTabHost, mViewPager, item);
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.pager_item_trakt, null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = super.onCreateView(inflater, container, savedInstanceState);

		ScrollingTextView tvAired = (ScrollingTextView)v.findViewById(R.id.textViewAired);
		TextView tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ImageView ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		BadgesView<T> bl = (BadgesView<T>)v.findViewById(R.id.badgesLayout);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*Image.RATIO_SCREEN));
		ivScreen.setLayoutParams(params);
		ivScreen.setScaleType(ScaleType.CENTER_CROP);

		if(item.getFirstAired() == null || item.getFirstAired().getTime() == 0)
			tvAired.setText("Never, date is not known or try to refresh");
		else
			tvAired.setText("First Aired : " + DateFormat.getLongDateFormat(getActivity()).format(item.getFirstAired()));		

		bl.initialize();
		bl.setTraktItem(item);

		//		Image i = new Image(item.getId(), item.getImages().fanart, Image.FANART);
		final AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback().url(Image.get(Image.FANART, item.getImages()).getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivScreen).image(cb);

		if(item.getRatings() != null)
			tvPercentage.setText(item.getRatings().percentage+"%");
		else
			tvPercentage.setText("?%");

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		SubMenu watchMenu = menu.addSubMenu("Watched");

		if(this.item != null && !this.item.isWatched())
		{
			watchMenu.add(0, R.id.action_bar_watched_seen, 0, "Seen")
			.setIcon(R.drawable.ab_icon_eye)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		else
		{
			watchMenu.add(0, R.id.action_bar_watched_unseen, 0, "Unseen")
			.setIcon(R.drawable.ab_icon_eye)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		//it is always possible to do a checkin even if user has already seen it
		watchMenu.add(0, R.id.action_bar_watched_checkin, 0, "Check in")
		.setIcon(R.drawable.ab_icon_eye)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		MenuItem watchItem = watchMenu.getItem();
		watchItem.setIcon(R.drawable.ab_icon_eye)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, R.id.action_bar_shouts, 0, "Shouts")
		.setIcon(R.drawable.ab_icon_shouts)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if(this.item != null && !this.item.isInCollection())
		{
			menu.add(0, R.id.action_bar_add_to_collection, 0, "Add to collection")
			.setIcon(R.drawable.ab_icon_shouts)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}
		else
		{
			menu.add(0, R.id.action_bar_remove_from_collection, 0, "Remove from collection")
			.setIcon(R.drawable.ab_icon_shouts)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}

		if(this.item != null && !this.item.isInWatchlist())
		{
			menu.add(0, R.id.action_bar_add_to_watchlist, 0, "Add to watchlist")
			.setIcon(R.drawable.ab_icon_shouts)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}
		else
		{
			menu.add(0, R.id.action_bar_remove_from_watchlist, 0, "Remove from watchlist")
			.setIcon(R.drawable.ab_icon_close)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{	
		case R.id.action_bar_watched_seen:
			break;
		case R.id.action_bar_watched_unseen:
			break;
		case R.id.action_bar_watched_checkin:
			break;
		case R.id.action_bar_shouts:
			Intent i = new Intent(getActivity(), ShoutsActivity.class);
			i.putExtra(TraktoidConstants.BUNDLE_TRAKT_ITEM, this.item);
			startActivity(i);
			break;
		case R.id.action_bar_add_to_collection:
		case R.id.action_bar_remove_from_collection:
			new InCollectionTask<T>(tm, this, this.item, item.getItemId() == R.id.action_bar_add_to_collection, new PostListener() 
			{
				@Override
				public void onComplete(Response r, boolean success) 
				{
					if(success)
					{
						
					}
				}
			}).fire();
			break;
		case R.id.action_bar_add_to_watchlist:
		case R.id.action_bar_remove_from_watchlist:
			new InWatchlistTask<T>(tm, this, this.item, item.getItemId() == R.id.action_bar_add_to_watchlist, new PostListener() 
			{
				@Override
				public void onComplete(Response r, boolean success) 
				{
					if(success)
					{
						
					}
				}
			}).fire();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

}
