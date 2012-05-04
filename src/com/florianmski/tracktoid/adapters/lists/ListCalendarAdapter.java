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

package com.florianmski.tracktoid.adapters.lists;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.ui.activities.phone.EpisodeActivity;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.tracktoid.widgets.ScrollingTextView;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ListCalendarAdapter extends RootAdapter<CalendarDate> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public final static int TYPE_SEPARATOR = 0, TYPE_ROW = 1;
	public final static int NB_BY_ROW_PORTRAIT = 2;
	public final static int NB_BY_ROW_LANDSCAPE = 3;

	private List<Object> objects = new ArrayList<Object>();
	private static int nbByRow;

	public ListCalendarAdapter(List<CalendarDate> calendarDates, Context context)
	{
		super(context, calendarDates);

		int orientation = context.getResources().getConfiguration().orientation;
		if(orientation == Configuration.ORIENTATION_PORTRAIT)
			nbByRow = NB_BY_ROW_PORTRAIT;
		else
			nbByRow = NB_BY_ROW_LANDSCAPE;

		if(calendarDates != null)
		{
			for(CalendarDate cd : calendarDates)
			{
				objects.add(cd);
				for(int i = 0; i < (int) Math.ceil((cd.episodes.size()*1.0)/(nbByRow*1.0)); i++)
				{
					List<CalendarTvShowEpisode> temp = new ArrayList<CalendarTvShowEpisode>();
					for(int j = 0; j < nbByRow; j++)
					{
						if(j+(nbByRow*i) < cd.episodes.size())
							temp.add(cd.episodes.get(j+(nbByRow*i)));
					}
					objects.add(temp);
				}
			}
		}
	}

	@Override
	public int getCount() 
	{
		int count = 0;
		for(CalendarDate cd : items)
			count += Math.ceil(cd.episodes.size()/(nbByRow*1.0));
		return count + items.size();
	}

	@Override
	public int getItemViewType(int position) 
	{
		if(objects.get(position) instanceof CalendarDate)
			return TYPE_SEPARATOR;
		else
			return TYPE_ROW;
	}

	@Override
	public int getViewTypeCount() 
	{
		return 2;
	}

	@Override
	@SuppressWarnings("unchecked")
	public View doGetView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;
		int type = getItemViewType(position);
		if (convertView == null) 
		{
			holder = new ViewHolder();
			switch(type)
			{
			case TYPE_SEPARATOR :
			{
				holder.llSeparator = new LinearLayout(context);
				ListView.LayoutParams params = new ListView.LayoutParams(LayoutParams.FILL_PARENT, 50);
				holder.llSeparator.setLayoutParams(params);
				holder.llSeparator.setGravity(Gravity.CENTER_VERTICAL);
				holder.llSeparator.setPadding(15, 0, 0, 0);
				holder.llSeparator.setBackgroundColor(context.getResources().getColor(R.color.list_pressed_color));
				holder.llSeparator.setClickable(false);
				holder.tvDay = new TextView(context);
				holder.tvDay.setTextSize(20);
				holder.tvDay.setTextColor(Color.WHITE);
				holder.llSeparator.addView(holder.tvDay);
				holder.llSeparator.setTag(holder);
				break;
			}
			case TYPE_ROW :
			{
				holder.llEpisodes = new LinearLayout(context);
				holder.llEpisodes.setOrientation(LinearLayout.HORIZONTAL);
				ListView.LayoutParams params = new ListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				holder.llEpisodes.setLayoutParams(params);
//				holder.llEpisodes.setFocusable(false);
//				holder.llEpisodes.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

				int width = parent.getWidth()/nbByRow;
				int height = (int) (width*0.562893082);

				for(int i = 0; i < holder.bvScreen.length; i++)
				{						
					holder.bvScreen[i] = (BadgesView) LayoutInflater.from(context).inflate(R.layout.item_calendar, null);
					holder.bvScreen[i].setLayoutParams(new LinearLayout.LayoutParams(width, height));

					holder.tvShow[i] = (ScrollingTextView) holder.bvScreen[i].findViewById(R.id.textViewShow);
					holder.tvTitle[i] = (ScrollingTextView) holder.bvScreen[i].findViewById(R.id.textViewTitle);
					holder.tvAirTime[i] = (ScrollingTextView) holder.bvScreen[i].findViewById(R.id.textViewAirTime);
					holder.livScreen[i] = (ImageView) holder.bvScreen[i].findViewById(R.id.imageViewScreen);
					holder.llScreen[i] = (LinearLayout) holder.bvScreen[i].findViewById(R.id.linearLayoutScreen);
//					holder.livScreen[i].setFocusable(true);
					
//					holder.rlScreen[i].setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//					holder.tvShow[i].setDuplicateParentStateEnabled(true);

					holder.llEpisodes.addView(holder.bvScreen[i]);
				}

				holder.llEpisodes.setTag(holder);
				break;
			}
			}

		} 
		else
			holder = (ViewHolder) convertView.getTag();

		switch(type)
		{
		case TYPE_SEPARATOR :
		{
			CalendarDate cd = (CalendarDate) objects.get(position);

			holder.tvDay.setText(new SimpleDateFormat("EEEE dd MMMM yyyy").format(cd.date));

			return holder.llSeparator;
		}
		case TYPE_ROW :
		{	        	
			List<CalendarTvShowEpisode> episodes = (List<CalendarTvShowEpisode>) objects.get(position);

			for(int i = 0; i < nbByRow; i++)
			{
				holder.tvShow[i].setVisibility(View.INVISIBLE);
				holder.llScreen[i].setVisibility(View.INVISIBLE);
				holder.livScreen[i].setVisibility(View.INVISIBLE);
			}

			for(int i = 0; i < episodes.size(); i++)
			{
				holder.tvShow[i].setVisibility(View.VISIBLE);
				holder.llScreen[i].setVisibility(View.VISIBLE);
				holder.livScreen[i].setVisibility(View.VISIBLE);

				final CalendarTvShowEpisode e = episodes.get(i);

				final TvShowEpisode episode = e.episode;

				holder.tvShow[i].setText(e.show.title);

				String title = (episode.season < 10) ? "0"+episode.season+"x" : episode.season+"x";
				title += (episode.number < 10) ? "0"+episode.number : episode.number+"";
				title += " "+episode.title;

				holder.tvTitle[i].setText(title);
				holder.tvAirTime[i].setText(e.show.airTime + " on " + e.show.network);

				TraktImage image;
				File posterImage = null;
				AQuery aq = listAq.recycle(holder.llEpisodes);
				
				if(episode.images.screen != null && episode.images.fanart != null)
					image = TraktImage.getScreen(episode);
				else
				{
					//offline calendar (display show's poster)
					image = TraktImage.getPoster(e.show);
					posterImage = aq.getCachedFile(image.getUrl());
				}
				
				holder.bvScreen[i].initialize();
				
				//TODO image étirées
				//faire la méthode opposée de setPlaceHolder()
				//realeasePlaceholder()
				if(aq.shouldDelay(holder.llEpisodes, parent, image.getUrl(), 0))
					setPlaceholder(holder.livScreen[i]);
				else
				{
					if(posterImage != null)
						aq.id(holder.livScreen[i]).image(posterImage, 0);
					else
						aq.id(holder.livScreen[i]).image(image.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);
					
					holder.bvScreen[i].setTraktItem(episode);
				}

				holder.bvScreen[i].setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						Intent i = new Intent(context, EpisodeActivity.class);
						ArrayList<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>();
						//workaround to display image in the episode view
						
						if(episode.images.screen != null)
							episode.images.screen = episode.images.screen.replace("-940","");
						
						episodes.add(episode);
						i.putExtra(TraktoidConstants.BUNDLE_RESULTS, episodes);
						i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, e.show.tvdbId);
						context.startActivity(i);
					}
				});
			}

			return holder.llEpisodes;
		}
		}
		return null;

	}

	private static class ViewHolder 
	{
		private LinearLayout llEpisodes;

		private LinearLayout llSeparator;
		private TextView tvDay;

		private ScrollingTextView[] tvShow = new ScrollingTextView[nbByRow];
		private ScrollingTextView[] tvTitle = new ScrollingTextView[nbByRow];
		private ScrollingTextView[] tvAirTime = new ScrollingTextView[nbByRow];
		private ImageView[] livScreen = new ImageView[nbByRow];
		private BadgesView[] bvScreen = new BadgesView[nbByRow];
		private LinearLayout[] llScreen = new LinearLayout[nbByRow];
	}
}
