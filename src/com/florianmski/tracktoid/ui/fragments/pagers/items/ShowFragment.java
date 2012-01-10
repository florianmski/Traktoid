package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.text.SimpleDateFormat;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.DayOfTheWeek;
import com.jakewharton.trakt.enumerations.Rating;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ShowFragment extends PagerItemFragment
{
	private TvShow s;
	
	public static ShowFragment newInstance(TvShow s)
	{
		ShowFragment f = new ShowFragment();
		Bundle args = new Bundle();
		args.putSerializable("object", s);
		f.setArguments(args);

		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		s = (TvShow) (getArguments() != null ? getArguments().getSerializable("object") : null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_show, null);
		
		TextView tvAir = (TextView)v.findViewById(R.id.textViewAir);
		TextView tvOverview = (TextView)v.findViewById(R.id.textViewOverview);
		TextView tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ImageView ivFanart = (ImageView)v.findViewById(R.id.imageViewFanart);
		final ImageView ivWatched = (ImageView)v.findViewById(R.id.imageViewBadge);
		final ImageView ivRating = (ImageView)v.findViewById(R.id.imageViewRating);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*Image.RATIO_FANART));
		ivFanart.setLayoutParams(params);
		ivFanart.setScaleType(ScaleType.CENTER_CROP);

		DayOfTheWeek airDay = s.airDay;

		String airTime = s.airTime == null || s.airTime.equals("") || s.airTime.equals("null") ? "?" : s.airTime;
		String network = s.network == null || s.network.equals("") || s.network.equals("null") ? "?" : s.network;
		String runtime = s.runtime == 0 ? "?min" : s.runtime + "min";
		String firstAired = s.firstAired == null ? "" : "First Aired : " + new SimpleDateFormat("MM/dd/yyyy").format(s.firstAired) + "\n";

		tvAir.setText(firstAired + ((airDay == null || airDay.toString().equals("")) ? "?" : airDay) + " at " + airTime + " on " + network + " (" + runtime + ")");

		tvOverview.setText(s.overview);
		ivRating.setImageBitmap(null);
		ivWatched.setImageBitmap(null);

		Image i = new Image(s.tvdbId, s.images.screen, Image.FANART);
		AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback();

		//configure the callback
		cb.url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivFanart).image(cb);
		
		TransitionDrawable td = null;

		if(s.rating == Rating.Love)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_loved)});
		else if(s.rating == Rating.Hate)
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_hated)});

		if(td != null)
			td.startTransition(1000);

		ivRating.setImageDrawable(td);

		if(s.progress == 100)
		{
			td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(1000);
			ivWatched.setImageDrawable(td);
		}
		else
			ivWatched.setImageDrawable(null);

		if(s.ratings != null)
			tvPercentage.setText(s.ratings.percentage+"%");
		else
			tvPercentage.setText("?%");

		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSaveState(Bundle toSave) {
		// TODO Auto-generated method stub
		
	}
}
