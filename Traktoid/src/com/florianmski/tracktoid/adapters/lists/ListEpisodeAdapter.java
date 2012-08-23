package com.florianmski.tracktoid.adapters.lists;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.widgets.OverlaysView;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ListEpisodeAdapter extends RootAdapter<TvShowEpisode>
{	
	private ListCheckerManager<TvShowEpisode> lcm;
	private String seasonId;

	public ListEpisodeAdapter(List<TvShowEpisode> episodes, String seasonId, Context context)
	{
		super(context, episodes);

		this.lcm = ListCheckerManager.getInstance();
		this.seasonId = seasonId;
	}

	public void updateItems(List<TvShowEpisode> items)
	{
		boolean dataChanged = false;

		Log.e("test","size : " + items.size());
		
		for(TvShowEpisode item : items)
		{
			if(item.seasonId.equals(seasonId))
			{
				int index = Collections.binarySearch(this.items, item);
				if(index < 0 || index >= this.items.size())
					this.items.add(item);
				else
					this.items.set(index, item);
				dataChanged = true;
			}
		}

		if(dataChanged)
		{
			Collections.sort(this.items);
			notifyDataSetChanged();
		}
	}

	public void remove(List<TvShowEpisode> items)
	{
		boolean dataChanged = false;

		for(TvShowEpisode item : items)
		{
			if(item.seasonId.equals(seasonId))
			{
				int index = Collections.binarySearch(this.items, item);
				if(index >= 0 && index < this.items.size())
				{
					this.items.remove(index);
					dataChanged = true;
				}
			}
		}
		
		if(dataChanged)
			notifyDataSetChanged();
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public View doGetView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null) 
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_episode, parent, false);
			holder = new ViewHolder();
			holder.ivScreen = (ImageView)convertView.findViewById(R.id.imageViewScreen);
			holder.tvTitle = (TextView)convertView.findViewById(R.id.textViewTitle);
			holder.tvEpisode = (TextView)convertView.findViewById(R.id.textViewEpisode);
			holder.bv = (OverlaysView<TvShowEpisode>)convertView.findViewById(R.id.badgesLayout);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();

		TvShowEpisode e = getItem(position);

		TraktImage i = TraktImage.getScreen(e);
		final AQuery aq = listAq.recycle(convertView);
		BitmapAjaxCallback cb = new BitmapAjaxCallback()
		{
			@Override
			public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
			{     
				aq.id(iv).image(Utils.borderBitmap(bm, context)).animate(android.R.anim.fade_in);
			}

		}.url(i.getUrl()).fileCache(false).memCache(true).ratio(9.0f / 16.0f);

		holder.bv.initialize();

		//in case user scroll the list fast, stop loading images from web
		if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
			setPlaceholder(holder.ivScreen);
		else
		{
			aq.id(holder.ivScreen).image(cb);        
			holder.bv.setTraktItem(e);
		}

		holder.tvTitle.setText(e.title);
		holder.tvEpisode.setText("Episode " + e.number);

		return lcm.checkView(e, convertView);
	}

	private static class ViewHolder 
	{
		private ImageView ivScreen;
		private TextView tvTitle;
		private TextView tvEpisode;
		private OverlaysView<TvShowEpisode> bv;
	}
}
