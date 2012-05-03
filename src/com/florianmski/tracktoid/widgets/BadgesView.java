package com.florianmski.tracktoid.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.florianmski.tracktoid.R;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.enumerations.Rating;

public class BadgesView<T extends TraktoidInterface<T>> extends RelativeLayout
{
	private T traktItem;
	private ImageView ivWatched;
	private ImageView ivRating;
	private ImageView ivCollection;
	private ImageView ivWatchlist;
	
	private RelativeLayout rlBadges;
	
	public BadgesView(Context context) 
	{
		super(context);
		initView(context);
	}
	
	public BadgesView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initView(context);
	}
	
	public BadgesView(Context context, AttributeSet attrs, int defStyle) 
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
    }
	
	public void setTraktItem(T traktItem)
	{
		this.traktItem = traktItem;
		
		toggleWatched(traktItem.isWatched());
		toggleRating(traktItem.getRating());
		toggleWatchlist(traktItem.isInWatchlist());
		toggleCollection(traktItem.isInCollection());
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
	
	private TransitionDrawable getTD(int drawableId)
	{
		TransitionDrawable td = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.drawable.empty), getResources().getDrawable(drawableId)});
		td.startTransition(1000);
		return td;
	}
	
	private void toggleBadge(boolean on, ImageView iv, int drawableId)
	{
		if(on)
			iv.setImageDrawable(getTD(drawableId));
		else
			iv.setImageBitmap(null);
	}
	
	public void toggleWatched(boolean on)
	{
		toggleBadge(on, ivWatched, R.drawable.badge_watched);
	}
	
	public void toggleRating(Rating r)
	{
		toggleBadge((r == Rating.Hate || r == Rating.Love), ivRating, r == Rating.Hate ? R.drawable.badge_hated : R.drawable.badge_loved);
	}
	
	public void toggleWatchlist(boolean on)
	{
		toggleBadge(on, ivWatchlist, R.drawable.badge_watchlist);
	}
	
	public void toggleCollection(boolean on)
	{
		toggleBadge(on, ivCollection, R.drawable.badge_collection);
	}
	
}
