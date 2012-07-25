package com.florianmski.tracktoid.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.florianmski.tracktoid.R;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.enumerations.Rating;

public class OverlaysView<T extends TraktoidInterface<T>> extends RelativeLayout
{
	//there is an issue with gridviews, for the first item, overlays don't appear, don't know why...

	private T traktItem;
	private ImageView ivWatched;
	private ImageView ivRating;
	private ImageView ivCollection;
	private ImageView ivWatchlist;

	private RelativeLayout rlBadges;

	private Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);

	public OverlaysView(Context context) 
	{
		super(context);
		initView(context);
	}

	public OverlaysView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initView(context);
	}

	public OverlaysView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.view_badges, this);
		rlBadges = (RelativeLayout)v.findViewById(R.id.relativeLayoutBadges);
		ivWatched = (ImageView)v.findViewById(R.id.imageViewWatched);
		ivRating = (ImageView)v.findViewById(R.id.imageViewRating);
		ivCollection = (ImageView)v.findViewById(R.id.imageViewCollection);
		ivWatchlist = (ImageView)v.findViewById(R.id.imageViewWatchlist);

		animation.setDuration(1000);
	}

	public void setTraktItem(T traktItem)
	{
		this.traktItem = traktItem;

		//		startAnimation(animation);

		toggleWatched(OverlaysView.this.traktItem.isWatched());
		toggleRating(OverlaysView.this.traktItem.getRating());
		toggleWatchlist(OverlaysView.this.traktItem.isInWatchlist());
		toggleCollection(OverlaysView.this.traktItem.isInCollection());
	}

	public void initialize()
	{
		toggleWatched(false);
		toggleRating(Rating.Unrate);
		toggleWatchlist(false);
		toggleCollection(false);		

		//bring badges to the front so it's not covered by images
		bringChildToFront(rlBadges);
	}

	private void toggleBadge(boolean on, ImageView iv, int drawableId)
	{
		toggleBadge(on, iv, getResources().getDrawable(drawableId));
//		if(on)
//		{
//			iv.startAnimation(animation);
//			iv.setImageResource(drawableId);
//		}
//		else
//			iv.setImageBitmap(null);
	}
	
	private void toggleBadge(boolean on, ImageView iv, Drawable drawable)
	{
		if(on)
		{
			iv.startAnimation(animation);
			iv.setImageDrawable(drawable);
		}
		else
			iv.setImageBitmap(null);
	}

	public void toggleWatched(boolean on)
	{
		toggleBadge(on, ivWatched, R.drawable.badge_watched);
	}

	public void toggleRating(Rating r)
	{
		if(r == null)
			return;
		
		if(r == Rating.Unrate)
			return;
		else if(r == Rating.Love)
			toggleBadge(true, ivRating, R.drawable.badge_loved);
		else if(r == Rating.Hate)
			toggleBadge(true, ivRating, R.drawable.badge_hated);
		else
			toggleBadge(true, ivRating, new RateDrawable(r));
	}

	public void toggleWatchlist(boolean on)
	{
		toggleBadge(on, ivWatchlist, R.drawable.badge_watchlist);
	}

	public void toggleCollection(boolean on)
	{
		toggleBadge(on, ivCollection, R.drawable.badge_collection);
	}
	
	public void setOverlaysVisibility(int visibility)
	{
		ivWatched.setVisibility(visibility);
		ivRating.setVisibility(visibility);
		ivCollection.setVisibility(visibility);
		ivWatchlist.setVisibility(visibility);
	}

}
