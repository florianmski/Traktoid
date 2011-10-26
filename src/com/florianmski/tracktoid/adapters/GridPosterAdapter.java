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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class GridPosterAdapter extends BaseAdapter
{
	private static final int padding = 2;

	private Activity context;
	private List<TvShow> shows;
	private int cHeight;

	public GridPosterAdapter(Activity context, List<TvShow> series, int cHeight) 
	{
		this.context = context;
		this.shows = series;
		this.cHeight = cHeight;
	}

	public void updateShow(TvShow show)
	{
		int index = Collections.binarySearch(shows, show);
		if(index < 0 || index >= shows.size())
			shows.add(show);
		else
			shows.set(index, show);

		Collections.sort(shows);

		this.notifyDataSetChanged();
	}

	public void removeShow(TvShow show)
	{
		int index = Collections.binarySearch(shows, show);
		if(index >= 0 && index < shows.size())
			shows.remove(index);

		this.notifyDataSetChanged();
	}

	public void updateShows(ArrayList<TvShow> shows, ArrayList<Integer> percentages)
	{
		this.shows = shows;
		this.notifyDataSetChanged();
	}

	public List<TvShow> getShows()
	{
		return shows;
	}

	public int getCount() 
	{
		return shows.size();
	}

	public Object getItem(int position) 
	{
		return shows.get(position);
	}

	public long getItemId(int position) 
	{
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null)
		{
			holder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_show, null, false);

			holder.rl = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutPoster);
			GridView.LayoutParams paramsRl = new GridView.LayoutParams(LayoutParams.FILL_PARENT, cHeight);
			holder.rl.setLayoutParams(paramsRl);
			holder.rl.setPadding(padding, padding, padding, padding);

			holder.ivPoster = (ImageView) convertView.findViewById(R.id.imageViewPoster);         	

			holder.ivRating = (ImageView) convertView.findViewById(R.id.imageViewRating);
			holder.ivWatched = (ImageView) convertView.findViewById(R.id.imageViewWatched);

			RelativeLayout.LayoutParams paramsIvRating = new RelativeLayout.LayoutParams(cHeight/8, cHeight/8);
			paramsIvRating.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			RelativeLayout.LayoutParams paramsIvWatched = new RelativeLayout.LayoutParams(cHeight/4, cHeight/4);
			paramsIvWatched.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.ivRating.setLayoutParams(paramsIvRating);
			holder.ivWatched.setLayoutParams(paramsIvWatched);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();

		final TvShow show = shows.get(position);

		holder.ivRating.setImageBitmap(null);
		holder.ivWatched.setImageBitmap(null);
		holder.ivPoster.setImageBitmap(null);

		Image i = new Image(show.getTvdbId(), show.getImages().getPoster(), Image.POSTER, true);
		AQuery aq = new AQuery(convertView);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback();

		//configure the callback
		cb.url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(true).memCache(true);
		aq.id(holder.ivPoster).image(cb);
		
		TransitionDrawable td = null;

		if(show.getRating() == Rating.Love)
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_loved)});
		else if(show.getRating() == Rating.Hate)
			td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_hated)});

		if(td != null)
			td.startTransition(2000);

		holder.ivRating.setImageDrawable(td);

		if(show.getProgress() == 100)
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