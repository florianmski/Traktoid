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
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.enumerations.Rating;

public class EpisodeFragment extends PagerItemTraktFragment
{
	private TvShowEpisode e;
	private String tvdbId;

	public static EpisodeFragment newInstance(Bundle args)
	{
		EpisodeFragment f = new EpisodeFragment();
		f.setArguments(args);
		return f;
	}

	public static EpisodeFragment newInstance(TvShowEpisode e, String tvdbId)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_EPISODE, e);
		args.putString(TraktoidConstants.BUNDLE_TVDB_ID, tvdbId);

		return newInstance(args);
	}

	public EpisodeFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if(getArguments() != null)
		{
			e = (TvShowEpisode) getArguments().getSerializable(TraktoidConstants.BUNDLE_EPISODE);
			tvdbId = getArguments().getString(TraktoidConstants.BUNDLE_TVDB_ID);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public Date getFirstAired() 
	{
		return e.firstAired;
	}

	@Override
	public Ratings getRatings() 
	{
		return e.ratings;
	}

	@Override
	public Rating getRating() 
	{
		return e.rating;
	}

	@Override
	public boolean isWatched() 
	{
		return e.watched;
	}

	@Override
	public Image getImage() 
	{
		return new Image(tvdbId, e.images.screen, e.season, e.number);
	}

	@Override
	public String getOverview() 
	{
		return e.overview;
	}
}
