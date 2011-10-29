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

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.viewpagerindicator.TitleProvider;

public class PagerEpisodeAdapter extends PagerAdapter implements TitleProvider
{
	private List<TvShowEpisode> episodes;
	private Context context;
	private String tvdbId;

	public PagerEpisodeAdapter(List<TvShowEpisode> episodes, String tvdb_id, Context context)
	{
		this.episodes = episodes;
		this.context = context;
		this.tvdbId = tvdb_id;
	}
	
	public void reloadData(List<TvShowEpisode> episodes)
	{
		this.episodes = episodes;
		this.notifyDataSetChanged();
	}
	
	public TvShowEpisode getEpisode(int position)
	{
		return episodes.get(position);
	}

	@Override
	public int getCount() 
	{
		return episodes.size();
	}

	@Override
	/** @see http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view */
	public int getItemPosition(Object object) 
	{
	    return POSITION_NONE;
	}

	@Override
	public String getTitle(int position) 
	{
		return "S" + Utils.addZero(episodes.get(position).getSeason()) + " E" + Utils.addZero(episodes.get(position).getNumber());
	}
	
	@Override
	public void destroyItem(View pager, int position, Object view) 
	{
		((ViewPager)pager).removeView((View)view);
	}

	@Override
	public void finishUpdate(View container) {}

	@Override
	public Object instantiateItem(View pager, int position) 
	{
		View v = LayoutInflater.from(context).inflate(R.layout.pager_item_episode, null, false);
		TextView tvTitle = (TextView)v.findViewById(R.id.textViewTitle);
		TextView tvOverview = (TextView)v.findViewById(R.id.textViewOverview);
		TextView tvAired = (TextView)v.findViewById(R.id.textViewAired);
		TextView tvPercentage = (TextView)v.findViewById(R.id.textViewPercentage);
		ImageView ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		final ImageView ivWatched = (ImageView)v.findViewById(R.id.imageViewBadge);

		//sometimes pager.getWidth = 0, don't know why so I use this trick
		int width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width*Image.RATIO_SCREEN));
		ivScreen.setLayoutParams(params);
		ivScreen.setScaleType(ScaleType.CENTER_CROP);
		
		TvShowEpisode e = episodes.get(position);

		tvTitle.setText(e.getTitle());
		tvOverview.setText(e.getOverview());
		
		if(e.getFirstAired().getTime() == 0)
			tvAired.setText("Never or date is not known");
		else
			tvAired.setText("First Aired : \n" + new SimpleDateFormat("MMMM d, y").format(e.getFirstAired()));
		
		if(e.getRatings() != null)
			tvPercentage.setText(e.getRatings().getPercentage()+"%");
		
		ivWatched.setImageBitmap(null);

		Image i = new Image(tvdbId, e.getImages().getScreen(), e.getSeason(), e.getNumber());
		AQuery aq = new AQuery(v);
		//create a bitmap ajax callback object
		BitmapAjaxCallback cb = new BitmapAjaxCallback();

		//configure the callback
		cb.url(i.getUrl()).animation(android.R.anim.fade_in).fileCache(false).memCache(true);
		aq.id(ivScreen).image(cb);
		
		if(e.getWatched())
		{
			TransitionDrawable td = new TransitionDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.empty), context.getResources().getDrawable(R.drawable.badge_watched)});
			td.startTransition(1000);
			ivWatched.setImageDrawable(td);
		}

		((ViewPager)pager).addView(v, 0);

		return v;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) 
	{
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {}

	@Override
	public Parcelable saveState() 
	{
		return null;
	}

	@Override
	public void startUpdate(View container) {}

}
