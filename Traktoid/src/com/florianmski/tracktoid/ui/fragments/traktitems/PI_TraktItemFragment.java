package com.florianmski.tracktoid.ui.fragments.traktitems;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.SatelliteDrawable;
import com.florianmski.tracktoid.TraktListener;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.pagers.PagerDetailsAdapter;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.post.CheckinPostTask;
import com.florianmski.tracktoid.trakt.tasks.post.InCollectionTask;
import com.florianmski.tracktoid.trakt.tasks.post.InWatchlistTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.trakt.tasks.post.RateTask;
import com.florianmski.tracktoid.trakt.tasks.post.SeenTask;
import com.florianmski.tracktoid.ui.activities.phone.ShoutsActivity;
import com.florianmski.tracktoid.ui.fragments.PagerTabsViewFragment;
import com.florianmski.tracktoid.widgets.AlphaToggleButton;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.tracktoid.widgets.RateDialog;
import com.florianmski.tracktoid.widgets.RateDialog.OnColorChangedListener;
import com.florianmski.tracktoid.widgets.ScrollingTextView;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public abstract class PI_TraktItemFragment<T extends TraktoidInterface<T>> extends PagerTabsViewFragment implements TraktListener<T>
{
	protected T item;

	private ScrollingTextView tvAired;
	private TextView tvPercentage;
	private ImageView ivScreen;
	private BadgesView<T> bl;
	private SatelliteMenu menu;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if(getArguments() != null)
			item = (T) getArguments().getSerializable(TraktoidConstants.BUNDLE_TRAKT_ITEM);

		getSherlockActivity().invalidateOptionsMenu();

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
		mTabsAdapter.addTab(mTabHost.newTabSpec("genres").setIndicator("Genres"), android.R.layout.simple_list_item_1, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("links").setIndicator("Links"), android.R.layout.simple_list_item_1, null);
		//		mTabsAdapter.addTab(mTabHost.newTabSpec("shouts").setIndicator("Shouts"), R.layout.fragment_shouts, null);

		List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		items.add(new SatelliteMenuItem(R.id.action_bar_rate, new SatelliteDrawable(R.drawable.ab_icon_rate, getResources())));
		items.add(new SatelliteMenuItem(R.id.action_bar_add_to_collection, new SatelliteDrawable(R.drawable.badge_collection, getResources())));
		items.add(new SatelliteMenuItem(R.id.action_bar_add_to_watchlist, new SatelliteDrawable(R.drawable.badge_watchlist, getResources())));
		items.add(new SatelliteMenuItem(R.id.action_bar_watched_seen, new SatelliteDrawable(R.drawable.ab_icon_eye, getResources())));
		if(!(this.item instanceof TvShow))
			items.add(new SatelliteMenuItem(R.id.action_bar_watched_checkin, new SatelliteDrawable(R.drawable.ab_icon_checkin, getResources())));

		menu.addItems(items);

		menu.setOnItemClickedListener(new SateliteClickedListener() 
		{
			public void eventOccured(int id) 
			{
				switch(id)
				{
				case R.id.action_bar_rate:
					new RateDialog(getActivity(), new OnColorChangedListener() 
					{
						@Override
						public void rateChanged(Rating r) 
						{
							RateTask.createTask(getActivity(), PI_TraktItemFragment.this.item, r, null).fire();
						}
					}, item.getRating()).show();
					break;
				case R.id.action_bar_add_to_collection:
					InCollectionTask.createTask(getActivity(), item, !item.isInCollection(), null).fire();
					break;
				case R.id.action_bar_add_to_watchlist:
					InWatchlistTask.createTask(getActivity(), item, !item.isInWatchlist(), null).fire();
					break;
				case R.id.action_bar_watched_checkin:
					final Dialog dialogCheckin = new Dialog(getSherlockActivity());
					dialogCheckin.setContentView(R.layout.dialog_checkin);
					dialogCheckin.setTitle("Checkin");
					dialogCheckin.show();

					final EditText edt = (EditText) dialogCheckin.findViewById(R.id.editTextCheckin);
					final TextView tv = (TextView) dialogCheckin.findViewById(R.id.textViewCheckin);
					Button btnCheckin = (Button) dialogCheckin.findViewById(R.id.buttonCheckin);
					final AlphaToggleButton atbFacebook = (AlphaToggleButton) dialogCheckin.findViewById(R.id.toggleButtonFacebook);
					final AlphaToggleButton atbTwitter = (AlphaToggleButton) dialogCheckin.findViewById(R.id.toggleButtonTwitter);
					final AlphaToggleButton atbTumblr = (AlphaToggleButton) dialogCheckin.findViewById(R.id.toggleButtonTumblr);

					edt.addTextChangedListener(new TextWatcher() 
					{
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {}

						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

						@Override
						public void afterTextChanged(Editable s) 
						{
							tv.setText(100 - edt.getText().length() + " chars left");
						}
					});

					//TODO might be great to display the name of the item depending on this type (check the trakt website)
					//for instance : SHOW NAME + "EPISODE NAME" for an episode
					//MOVIE NAME (YEAR) for a movie
					edt.setText("I'm watching \"" + item.getTitle() + "\" using #traktoid");
					btnCheckin.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							CheckinPostTask
							.createTask(getActivity(), item, true, new PostListener() 
							{
								@Override
								public void onComplete(Response r, boolean success) 
								{
									if(r.wait > 0)
									{
										//ask user to cancel the current checkin or not
										AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
										//TODO wait time must be readable (not just seconds)
										builder.setMessage("There is already a checkin in progress. Either cancel this checkin or wait " + r.wait + " before you can check in again.")
										.setCancelable(false)
										.setPositiveButton("Cancel checkin", new DialogInterface.OnClickListener() 
										{
											//if cancel, cancel the current checkin then when it's done resend the checkin then close the dialog
											public void onClick(DialogInterface dialog, int id) 
											{
												CheckinPostTask.createTask(getSherlockActivity(), item, false, new PostListener() 
												{
													@Override
													public void onComplete(Response r, boolean success) 
													{
														CheckinPostTask
														.createTask(getActivity(), item, true, null)
														.share(atbFacebook.isChecked(), atbTwitter.isChecked(), atbTumblr.isChecked(), false)
														.message(edt.getText().toString().trim())
														.fire();
														dialogCheckin.dismiss();
													}
												}).fire();
											}
										})
										.setNegativeButton("Close", new DialogInterface.OnClickListener() 
										{
											//if close, close the two dialogs and do nothing
											public void onClick(DialogInterface dialog, int id) 
											{
												dialog.cancel();
												dialogCheckin.dismiss();
											}
										});
										builder.create().show();
									}
									//if all is good just close the dialog
									else
										dialogCheckin.dismiss();
								}
							})
							.share(atbFacebook.isChecked(), atbTwitter.isChecked(), atbTumblr.isChecked(), false)
							.message(edt.getText().toString().trim())
							.fire();
						}
					});

					break;
				case R.id.action_bar_watched_seen:
					SeenTask.createTask(getActivity(), item, !item.isWatched(), null).fire();
					break;
				}
			}
		});
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

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = super.onCreateView(inflater, container, savedInstanceState);

		tvAired = (ScrollingTextView)v.findViewById(R.id.textViewAired);
		tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		bl = (BadgesView<T>)v.findViewById(R.id.badgesView);
		menu = (SatelliteMenu) v.findViewById(R.id.menu);

		if(Build.VERSION.SDK_INT >= 11)
			menu.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

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

		menu.add(0, R.id.action_bar_shouts, 0, "Shouts")
		.setIcon(R.drawable.ab_icon_shouts)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{	
		case R.id.action_bar_shouts:
			Intent i = new Intent(getActivity(), ShoutsActivity.class);
			i.putExtra(TraktoidConstants.BUNDLE_TRAKT_ITEM, this.item);
			startActivity(i);
			break;
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
