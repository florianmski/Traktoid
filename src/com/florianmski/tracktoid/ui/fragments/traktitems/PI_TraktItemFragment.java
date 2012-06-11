package com.florianmski.tracktoid.ui.fragments.traktitems;

import java.util.List;

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
import com.florianmski.tracktoid.TraktListener;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerDetailsAdapter;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.post.CheckinPostTask;
import com.florianmski.tracktoid.trakt.tasks.post.InCollectionTask;
import com.florianmski.tracktoid.trakt.tasks.post.InWatchlistTask;
import com.florianmski.tracktoid.trakt.tasks.post.SeenTask;
import com.florianmski.tracktoid.ui.activities.phone.ShoutsActivity;
import com.florianmski.tracktoid.ui.fragments.PagerTabsViewFragment;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.tracktoid.widgets.ScrollingTextView;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.TvShow;

public abstract class PI_TraktItemFragment<T extends TraktoidInterface<T>> extends PagerTabsViewFragment implements TraktListener<T>
{
	protected T item;

	private ScrollingTextView tvAired;
	private TextView tvPercentage;
	private ImageView ivScreen;
	private BadgesView<T> bl;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if(getArguments() != null)
			item = (T) getArguments().getSerializable(TraktoidConstants.BUNDLE_TRAKT_ITEM);

		getActivity().invalidateOptionsMenu();

		TraktTask.addObserver(this);
	}

	@Override
	public void onDestroy()
	{
		TraktTask.removeObserver(this);
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		mTabsAdapter.addTab(mTabHost.newTabSpec("summary").setIndicator("Summary"), R.layout.pager_item_details_summary, null);
		//		mTabsAdapter.addTab(mTabHost.newTabSpec("shouts").setIndicator("Shouts"), R.layout.fragment_shouts, null);
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

		tvAired = (ScrollingTextView)v.findViewById(R.id.textViewAired);
		tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		bl = (BadgesView<T>)v.findViewById(R.id.badgesLayout);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*TraktImage.RATIO_SCREEN));
		ivScreen.setLayoutParams(params);
		ivScreen.setScaleType(ScaleType.CENTER_CROP);

		refreshView();

		return v;
	}

	private void refreshView()
	{
		if(item.getFirstAired() == null || item.getFirstAired().getTime() == 0)
			tvAired.setText("Never, date is not known or try to refresh");
		else
			tvAired.setText("First Aired : " + DateFormat.getLongDateFormat(getActivity()).format(item.getFirstAired()));		

		bl.initialize();
		bl.setTraktItem(item);

		//		Image i = new Image(item.getId(), item.getImages().fanart, Image.FANART);
		final AQuery aq = new AQuery(ivScreen);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback().url(TraktImage.getFanart(item).getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivScreen).image(cb);

		if(item.getRatings() != null)
			tvPercentage.setText(item.getRatings().percentage+"%");
		else
			tvPercentage.setText("?%");

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		if(item != null)
			setTitle(item.getTitle());

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
		if(!(this.item instanceof TvShow))
		{
			watchMenu.add(0, R.id.action_bar_watched_checkin, 0, "Check in")
			.setIcon(R.drawable.ab_icon_eye)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

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
		case R.id.action_bar_watched_unseen:
			SeenTask.createTask(this, this.item, item.getItemId() == R.id.action_bar_watched_seen, null).fire();
			break;
		case R.id.action_bar_watched_checkin:
			CheckinPostTask.createTask(this, this.item, true, null).fire();
			break;
		case R.id.action_bar_shouts:
			Intent i = new Intent(getActivity(), ShoutsActivity.class);
			i.putExtra(TraktoidConstants.BUNDLE_TRAKT_ITEM, this.item);
			startActivity(i);
			break;
		case R.id.action_bar_add_to_collection:
		case R.id.action_bar_remove_from_collection:
			InCollectionTask.createTask(this, this.item, item.getItemId() == R.id.action_bar_add_to_collection, null).fire();
			break;
		case R.id.action_bar_add_to_watchlist:
		case R.id.action_bar_remove_from_watchlist:
			InWatchlistTask.createTask(this, this.item, item.getItemId() == R.id.action_bar_add_to_watchlist, null).fire();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public void onTraktItemsUpdated(List<T> traktItems) 
	{
		for(T traktItem : traktItems)
			if(traktItem.getId().equals(item.getId()))
			{
				this.item = traktItem;
				getActivity().invalidateOptionsMenu();
				refreshView();

				break;
			}
	}

	@Override
	public void onTraktItemsRemoved(List<T> traktItems) 
	{
		for(T traktItem : traktItems)
			if(traktItem.getId().equals(item.getId()))
			{
				getActivity().finish();
				break;
			}
	}
}
