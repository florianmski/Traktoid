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
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.widget.ScrollingTextView;
import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.ui.EpisodeActivity;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;

public class ListCalendarAdapter extends BaseAdapter
{
	public final static int TYPE_SEPARATOR = 0, TYPE_ROW = 1;
	public final static int nbByRow = 2;

	private List<CalendarDate> calendarDates;
	private List<Object> objects = new ArrayList<Object>();
	private Context context;
	private Bitmap placeholder;

	public ListCalendarAdapter(List<CalendarDate> calendarDates, Context context)
	{
		this.calendarDates = calendarDates;
		this.context = context;

		placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);

		if(calendarDates != null)
		{
			for(CalendarDate cd : calendarDates)
			{
				objects.add(cd);
				for(int i = 0; i < (int) Math.ceil((cd.getEpisodes().size()*1.0)/(nbByRow*1.0)); i++)
				{
					List<CalendarTvShowEpisode> temp = new ArrayList<CalendarTvShowEpisode>();
					for(int j = 0; j < nbByRow; j++)
					{
						if(j+(nbByRow*i) < cd.getEpisodes().size())
							temp.add(cd.getEpisodes().get(j+(nbByRow*i)));
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
		for(CalendarDate cd : calendarDates)
			count += Math.ceil(cd.getEpisodes().size()/(nbByRow*1.0));
		return count + calendarDates.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
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

	@SuppressWarnings("unchecked")
	public View getView(final int position, View convertView, ViewGroup parent) 
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
				holder.llSeparator.setBackgroundColor(Color.parseColor("#D3C8B8"));
				holder.llSeparator.setClickable(false);
				holder.tvDay = new TextView(context);
				holder.tvDay.setTextSize(20);
				holder.tvDay.setTextColor(Color.BLACK);
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

				int width = parent.getWidth()/nbByRow;
				int height = (int) (width*0.562893082);

				for(int i = 0; i < holder.rlScreen.length; i++)
				{						
					holder.rlScreen[i] = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_calendar, null);
					holder.rlScreen[i].setLayoutParams(new LinearLayout.LayoutParams(width, height));

					holder.tvShow[i] = (ScrollingTextView) holder.rlScreen[i].findViewById(R.id.textViewShow);
					holder.tvTitle[i] = (ScrollingTextView) holder.rlScreen[i].findViewById(R.id.textViewTitle);
					holder.tvAirTime[i] = (ScrollingTextView) holder.rlScreen[i].findViewById(R.id.textViewAirTime);
					holder.livScreen[i] = (ImageView) holder.rlScreen[i].findViewById(R.id.imageViewScreen);
					holder.livScreen[i].setFocusable(true);

					holder.llEpisodes.addView(holder.rlScreen[i]);
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
			CalendarDate cd = (CalendarDate) getItem(position);

			holder.tvDay.setText(DateFormat.getLongDateFormat(context).format(cd.getDate()));

			return holder.llSeparator;
		}
		case TYPE_ROW :
		{	        	
			List<CalendarTvShowEpisode> episodes = (List<CalendarTvShowEpisode>) getItem(position);

			for(int i = 0; i < nbByRow; i++)
			{
				holder.tvShow[i].setVisibility(View.INVISIBLE);
				holder.tvTitle[i].setVisibility(View.INVISIBLE);
				holder.tvAirTime[i].setVisibility(View.INVISIBLE);
				holder.livScreen[i].setVisibility(View.INVISIBLE);
			}

			for(int i = 0; i < episodes.size(); i++)
			{
				holder.tvShow[i].setVisibility(View.VISIBLE);
				holder.tvTitle[i].setVisibility(View.VISIBLE);
				holder.tvAirTime[i].setVisibility(View.VISIBLE);
				holder.livScreen[i].setVisibility(View.VISIBLE);

				CalendarTvShowEpisode e = episodes.get(i);

				final TvShowEpisode episode = e.getEpisode();

				holder.tvShow[i].setText(e.getShow().getTitle());

				String title = (episode.getSeason() < 10) ? "0"+episode.getSeason()+"x" : episode.getSeason()+"x";
				title += (episode.getNumber() < 10) ? "0"+episode.getNumber() : episode.getNumber()+"";
				title += " "+episode.getTitle();

				holder.tvTitle[i].setText(title);
				holder.tvAirTime[i].setText(e.getShow().getAirTime() + " on " + e.getShow().getNetwork());

				Image image = new Image(e.getShow().getTvdbId(), episode.getImages().getScreen(), Image.CALENDAR, true);
				AQuery aq = new AQuery(holder.llEpisodes);

				if(aq.shouldDelay(holder.llEpisodes, parent, image.getUrl(), 0))
					aq.id(holder.livScreen[i]).image(placeholder);
				else
					aq.id(holder.livScreen[i]).image(image.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);

				holder.livScreen[i].setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						Intent i = new Intent(context, EpisodeActivity.class);
						ArrayList<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>();
						episodes.add(episode);
						i.putExtra("results", episodes);
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
		private RelativeLayout[] rlScreen = new RelativeLayout[nbByRow];
	}
}
