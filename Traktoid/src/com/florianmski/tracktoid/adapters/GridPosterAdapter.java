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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.widgets.BadgesView;
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
	private ListCheckerManager<T> lcm;

	public GridPosterAdapter(Activity context, List<T> items, int height, ListCheckerManager<T> lcm)
	{
		super(context, items);
		this.height = height;
		this.lcm = lcm;
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
			//TODO rethink this
//		case FILTER_LOVED :
//			filteredItems.clear();
//			for(T item : items)
//			{
//				if(item.getRating() == Rating.Love)
//					filteredItems.add(item);
//			}
//			break;
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

	public void updateItems(List<T> items)
	{
		boolean dataChanged = false;

		for(T item : items)
		{
			int index = Collections.binarySearch(this.items, item);
			if(index < 0 || index >= this.items.size())
				this.items.add(item);
			else
				this.items.set(index, item);

			dataChanged = true;
		}

		if(dataChanged)
		{
			Collections.sort(this.items);
			setFilter(currentFilter);
		}
	}

	@Override
	public void remove(T item)
	{
		int index = Collections.binarySearch(items, item);
		if(index >= 0 && index < items.size())
		{
			items.remove(index);
			setFilter(currentFilter);
		}
	}

	public void remove(List<T> items)
	{
		boolean dataChanged = false;

		for(T item : items)
		{
			int index = Collections.binarySearch(this.items, item);
			if(index >= 0 && index < this.items.size())
			{
				this.items.remove(index);
				dataChanged = true;
			}
		}

		if(dataChanged)
			setFilter(currentFilter);
	}

	@Override
	public void refreshItems(List<T> items)
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
		final ViewHolder<T> holder;

		if (convertView == null)
		{
			holder = new ViewHolder<T>();

			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_show, null, false);

			holder.ivPoster = (ImageView) convertView.findViewById(R.id.imageViewPoster);
			holder.ivPoster.setScaleType(ScaleType.CENTER_CROP);

			holder.bv = (BadgesView<T>) convertView.findViewById(R.id.badgesLayout);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder<T>) convertView.getTag();

		GridView.LayoutParams paramsRl = new GridView.LayoutParams(LayoutParams.FILL_PARENT, height);
		holder.bv.setLayoutParams(paramsRl);

		final T item = getItem(position);
		String url = item.getImages().poster;
		String id = item.getId();

		holder.bv.initialize();
		holder.ivPoster.setImageBitmap(null);

		TraktImage i = TraktImage.getPoster(item);
		AQuery aq = listAq.recycle(convertView);

		if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
			setPlaceholder(holder.ivPoster);
		else
		{
			holder.ivPoster.setScaleType(ScaleType.CENTER_CROP);
			aq.id(holder.ivPoster).image(i.getUrl(), true, true);

			holder.bv.setTraktItem(item);
		}

		return lcm.checkView(item, convertView);
	}

	private static class ViewHolder<T extends TraktoidInterface<T>> 
	{
		private ImageView ivPoster;
		private BadgesView<T> bv;
	}
}