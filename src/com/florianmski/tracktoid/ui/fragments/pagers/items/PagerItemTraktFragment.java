package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.Date;

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
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.widgets.ScrollingTextView;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.enumerations.Rating;

public abstract class PagerItemTraktFragment extends PagerItemFragment
{
	public abstract Date getFirstAired();
	public abstract Ratings getRatings();
	public abstract Rating getRating();
	public abstract boolean isWatched();
	public abstract Image getImage();
	public abstract String getOverview();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_trakt, null);

		TextView tvOverview = (TextView)v.findViewById(R.id.textViewOverview);
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

		tvOverview.setText(getOverview());
		
		if(getFirstAired() == null || getFirstAired().getTime() == 0)
			tvAired.setText("Never or date is not known");
		else
			tvAired.setText("First Aired : " + DateFormat.getLongDateFormat(getActivity()).format(getFirstAired()));		
		
		ivRating.setImageBitmap(null);
		ivWatched.setImageBitmap(null);

		Image i = getImage();
		final AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback().url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivScreen).image(cb);

		TransitionDrawable td = null;

		if(getRating() == Rating.Love)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_loved)});
		else if(getRating() == Rating.Hate)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_hated)});

		if(td != null)
			td.startTransition(1000);

		ivRating.setImageDrawable(td);

		if(isWatched())
		{
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(1000);
			ivWatched.setImageDrawable(td);
		}
		else
			ivWatched.setImageDrawable(null);

		if(getRatings() != null)
			tvPercentage.setText(getRatings().percentage+"%");
		else
			tvPercentage.setText("?%");


		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

}
