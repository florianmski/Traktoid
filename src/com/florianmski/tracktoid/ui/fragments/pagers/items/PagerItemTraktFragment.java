package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.pagers.PagerDetailsAdapter;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.ui.fragments.pagers.TabsViewPagerFragment;
import com.florianmski.tracktoid.widgets.ScrollingTextView;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.enumerations.Rating;

public abstract class PagerItemTraktFragment<T extends TraktoidInterface> extends TabsViewPagerFragment
{
	protected T item;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
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
//		View v = inflater.inflate(R.layout.pager_item_trakt, null);
		View v = super.onCreateView(inflater, container, savedInstanceState);

		ScrollingTextView tvAired = (ScrollingTextView)v.findViewById(R.id.textViewAired);
		TextView tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ImageView ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		ImageView ivRating = (ImageView)v.findViewById(R.id.imageViewRating);
		final ImageView ivWatched = (ImageView)v.findViewById(R.id.imageViewBadge);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*Image.RATIO_SCREEN));
		ivScreen.setLayoutParams(params);
		ivScreen.setScaleType(ScaleType.CENTER_CROP);
		
		if(item.getFirstAired() == null || item.getFirstAired().getTime() == 0)
			tvAired.setText("Never or date is not known");
		else
			tvAired.setText("First Aired : " + DateFormat.getLongDateFormat(getActivity()).format(item.getFirstAired()));		
		
		ivRating.setImageBitmap(null);
		ivWatched.setImageBitmap(null);

//		Image i = new Image(item.getId(), item.getImages().fanart, Image.FANART);
		final AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback().url(Image.get(Image.FANART, item.getImages()).getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivScreen).image(cb);

		TransitionDrawable td = null;

		if(item.getRating() == Rating.Love)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_loved)});
		else if(item.getRating() == Rating.Hate)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_hated)});

		if(td != null)
			td.startTransition(1000);

		ivRating.setImageDrawable(td);

		if(item.isWatched())
		{
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(1000);
			ivWatched.setImageDrawable(td);
		}
		else
			ivWatched.setImageDrawable(null);

		if(item.getRatings() != null)
			tvPercentage.setText(item.getRatings().percentage+"%");
		else
			tvPercentage.setText("?%");
		
		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

}
