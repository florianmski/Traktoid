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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.enumerations.Rating;

public class GridPosterAdapter<T extends TraktoidInterface<T>> extends RootAdapter<T>
{
	public static final int FILTER_ALL = 0;
	public static final int FILTER_UNWATCHED = 1;
	public static final int FILTER_LOVED = 2;

	protected List<T> filteredItems = new ArrayList<T>();
	protected int height;
	protected int currentFilter = 0;
	protected Handler h = new Handler();

	public GridPosterAdapter(Activity context, List<T> items, int height)
	{
		super(context, items);
		this.height = height;
	}
	
	@Override
	public void clear() 
	{
		items.clear();
		filteredItems.clear();
		currentFilter = 0;
		notifyDataSetChanged();
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
			filteredItems.clear();
			filteredItems.addAll(items);
			break;
		case FILTER_UNWATCHED :
			filteredItems.clear();
			for(T item : items)
			{
				if(!item.isWatched())
					filteredItems.add(item);
			}
			break;
		case FILTER_LOVED :
			filteredItems.clear();
			for(T item : items)
			{
				if(item.getRating() == Rating.Love)
					filteredItems.add(item);
			}
			break;
		}
		this.notifyDataSetChanged();
	}

	public void updateItem(T item)
	{
		int index = Collections.binarySearch(items, item);
		if(index < 0 || index >= items.size())
			items.add(item);
		else
			items.set(index, item);

		Collections.sort(items);

		setFilter(currentFilter);
	}

	@Override
	public void remove(T item)
	{
		int index = Collections.binarySearch(items, item);
		if(index >= 0 && index < items.size())
			items.remove(index);

		setFilter(currentFilter);
	}

	@Override
	public void updateItems(List<T> items)
	{
		this.items = items;
		setFilter(currentFilter);
	}

	@Override
	public int getCount() 
	{
		return filteredItems.size();
	}

	@Override
	public T getItem(int position) 
	{
		return filteredItems.get(position);
	}

	@Override
	public View doGetView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

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
			holder.ivCollection = (ImageView) convertView.findViewById(R.id.imageViewCollection);
			holder.ivWatchlist = (ImageView) convertView.findViewById(R.id.imageViewWatchlist);

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

		final T item = getItem(position);
		boolean progress = item.isWatched();
		Rating rating = item.getRating();
		String url = item.getImages().poster;
		String id = item.getId();

		holder.ivRating.setImageBitmap(null);
		holder.ivWatched.setImageBitmap(null);
		holder.ivCollection.setImageBitmap(null);
		holder.ivWatchlist.setImageBitmap(null);
		holder.ivPoster.setImageBitmap(null);

		Image i = new Image(id, url, Image.POSTER);
		AQuery aq = listAq.recycle(convertView);

		if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
			setPlaceholder(holder.ivPoster);
		else
		{
			holder.ivPoster.setScaleType(ScaleType.CENTER_CROP);
			aq.id(holder.ivPoster).image(i.getUrl(), true, true);
//			File posterImage = aq.getCachedFile(i.getUrl());
//
//			if(posterImage != null)
//				aq.id(holder.ivPoster).image(posterImage, true, 0, null);
//			else
//			{
//				cb.url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(true).memCache(true);
//				aq.id(holder.ivPoster).image(cb);
//			}

			TransitionDrawable td = null;

			if(rating == Rating.Love)
				td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_loved)});
			else if(rating == Rating.Hate)
				td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_hated)});

			h.post(new TDRunnable(holder.ivRating, td));

			if(progress)
			{
				td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_watched)});
				h.post(new TDRunnable(holder.ivWatched, td));
			}
			
			if(item.isInCollection())
			{
				td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_collection)});
				h.post(new TDRunnable(holder.ivCollection, td));
			}
			
			if(item.isInWatchlist())
			{
				td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_watchlist)});
				h.post(new TDRunnable(holder.ivWatchlist, td));
			}
		}

		return convertView;
	}

	private static class ViewHolder 
	{
		private RelativeLayout rl;
		private ImageView ivPoster;
		private ImageView ivRating;
		private ImageView ivWatched;
		private ImageView ivCollection;
		private ImageView ivWatchlist;
	}

	private class TDRunnable implements Runnable
	{
		private ImageView iv;
		private TransitionDrawable td;

		public TDRunnable(ImageView iv, TransitionDrawable td)
		{
			this.iv = iv;
			this.td = td;
		}

		@Override
		public void run() 
		{
			if(td != null)
				td.startTransition(2000);
			iv.setImageDrawable(td);
		}

	}
}