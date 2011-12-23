/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class GridPosterAdapter extends BaseAdapter
{
	private static final int FILTER_ALL = 0;
	private static final int FILTER_UNWATCHED = 1;
	private static final int FILTER_LOVED = 2;

	private Activity context;
	private List<TvShow> shows;
	private List<TvShow> filterShows = new ArrayList<TvShow>();
	private int height;
	private int currentFilter = 0;

	public GridPosterAdapter(Activity context, List<TvShow> shows, int height) 
	{
		this.context = context;
		this.shows = shows;
		this.filterShows.addAll(shows);
		this.height = height;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
		notifyDataSetChanged();
	}

	public void setFilter(int filter)
	{
		currentFilter = filter;
		switch(filter)
		{
		case FILTER_ALL :
			filterShows.clear();
			filterShows.addAll(shows);
			break;
		case FILTER_UNWATCHED :
			filterShows.clear();
			for(TvShow s : shows)
			{
				if(s.progress < 100)
					filterShows.add(s);
			}
			break;
		case FILTER_LOVED :
			filterShows.clear();
			for(TvShow s : shows)
			{
				if(s.rating == Rating.Love)
					filterShows.add(s);
			}
			break;
		}

		this.notifyDataSetChanged();
	}

	public void updateShow(TvShow show)
	{
		int index = Collections.binarySearch(shows, show);
		if(index < 0 || index >= shows.size())
			shows.add(show);
		else
			shows.set(index, show);

		Collections.sort(shows);

		setFilter(currentFilter);
	}

	public void removeShow(TvShow show)
	{
		int index = Collections.binarySearch(shows, show);
		if(index >= 0 && index < shows.size())
			shows.remove(index);

		setFilter(currentFilter);
	}

	public void updateShows(List<TvShow> shows)
	{
		this.shows = shows;
		setFilter(currentFilter);
	}

	@Override
	public int getCount() 
	{
		return filterShows.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return filterShows.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;
		
//		Log.e("test","test");

		if (convertView == null)
		{
			holder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_show, null, false);

			holder.rl = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutPoster);
//			GridView.LayoutParams paramsRl = new GridView.LayoutParams(LayoutParams.FILL_PARENT, height);
//			holder.rl.setLayoutParams(paramsRl);
//			holder.rl.setPadding(PADDING, PADDING, PADDING, PADDING);

			holder.ivPoster = (ImageView) convertView.findViewById(R.id.imageViewPoster);
			holder.ivPoster.setScaleType(ScaleType.CENTER_CROP);

			holder.ivRating = (ImageView) convertView.findViewById(R.id.imageViewRating);
			holder.ivWatched = (ImageView) convertView.findViewById(R.id.imageViewWatched);

//			RelativeLayout.LayoutParams paramsIvRating = new RelativeLayout.LayoutParams(height/8, height/8);
//			paramsIvRating.addRule(RelativeLayout.ALIGN_RIGHT, holder.ivPoster.getId());
//			RelativeLayout.LayoutParams paramsIvWatched = new RelativeLayout.LayoutParams(height/4, height/4);
//			paramsIvWatched.addRule(RelativeLayout.ALIGN_RIGHT, holder.ivPoster.getId());
//			holder.ivRating.setLayoutParams(paramsIvRating);
//			holder.ivWatched.setLayoutParams(paramsIvWatched);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();
		
		GridView.LayoutParams paramsRl = new GridView.LayoutParams(LayoutParams.FILL_PARENT, height);
		holder.rl.setLayoutParams(paramsRl);
		
		RelativeLayout.LayoutParams paramsIvRating = new RelativeLayout.LayoutParams(height/8, height/8);
		paramsIvRating.addRule(RelativeLayout.ALIGN_RIGHT, holder.ivPoster.getId());
		RelativeLayout.LayoutParams paramsIvWatched = new RelativeLayout.LayoutParams(height/4, height/4);
		paramsIvWatched.addRule(RelativeLayout.ALIGN_RIGHT, holder.ivPoster.getId());
		holder.ivRating.setLayoutParams(paramsIvRating);
		holder.ivWatched.setLayoutParams(paramsIvWatched);
		
		final TvShow show = filterShows.get(position);

		holder.ivRating.setImageBitmap(null);
		holder.ivWatched.setImageBitmap(null);
		holder.ivPoster.setImageBitmap(null);

		Image i = new Image(show.tvdbId, show.images.poster, Image.POSTER);
		AQuery aq = new AQuery(convertView);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback();

		File posterImage = aq.getCachedFile(i.getUrl());
		
		//configure the callback
		if(posterImage != null)
			aq.id(holder.ivPoster).image(posterImage, 0);
		else
		{
			cb.url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(true).memCache(true);
			aq.id(holder.ivPoster).image(cb);
		}

		TransitionDrawable td = null;

		if(show.rating == Rating.Love)
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_loved)});
		else if(show.rating == Rating.Hate)
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_hated)});

		if(td != null)
			td.startTransition(2000);

		holder.ivRating.setImageDrawable(td);

		if(show.progress == 100)
		{
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(2000);
			holder.ivWatched.setImageDrawable(td);
		}

		return convertView;
	}

	private static class ViewHolder 
	{
		private RelativeLayout rl;
		private ImageView ivPoster;
		private ImageView ivRating;
		private ImageView ivWatched;
	}
}