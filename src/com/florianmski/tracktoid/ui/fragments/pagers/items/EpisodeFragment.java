package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.text.SimpleDateFormat;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
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
import com.jakewharton.trakt.entities.TvShowEpisode;

public class EpisodeFragment extends PagerItemFragment
{
	private TvShowEpisode e;
	private String tvdbId;
	
	public EpisodeFragment() 
	{
//		Bundle b = getActivity().getIntent().getExtras();
//		if(b != null)
//			setArguments(b);
	}
	
	public static EpisodeFragment newInstance(TvShowEpisode e, String tvdbId)
	{
		EpisodeFragment f = new EpisodeFragment();
		Bundle args = new Bundle();
		args.putSerializable("object", e);
		args.putString("tvdbId", tvdbId);
		f.setArguments(args);

		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		if(getArguments() != null)
		{
			e = (TvShowEpisode) getArguments().getSerializable("object");
			tvdbId = getArguments().getString("tvdbId");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.pager_item_episode, null);
		
		TextView tvOverview = (TextView)v.findViewById(R.id.textViewOverview);
		TextView tvAired = (TextView)v.findViewById(R.id.textViewAired);
		TextView tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ImageView ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		final ImageView ivWatched = (ImageView)v.findViewById(R.id.imageViewBadge);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*Image.RATIO_SCREEN));
		ivScreen.setLayoutParams(params);
		ivScreen.setScaleType(ScaleType.CENTER_CROP);
		
		tvOverview.setText(e.overview);
		
		if(e.firstAired.getTime() == 0)
			tvAired.setText("Never or date is not known");
		else
			tvAired.setText("First Aired : \n" + new SimpleDateFormat("MMMM d, y").format(e.firstAired));
		
		if(e.ratings != null)
			tvPercentage.setText(e.ratings.percentage+"%");
		
		ivWatched.setImageBitmap(null);

		Image i = new Image(tvdbId, e.images.screen, e.season, e.number);
		AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback();

		//configure the callback
		cb.url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivScreen).image(cb);
		
		if(e.watched)
		{
			TransitionDrawable td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(1000);
			ivWatched.setImageDrawable(td);
		}

		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
