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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.AdapterInterface;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.TvShow;

public class ListRecommendationAdapter<T extends TraktoidInterface> extends BaseAdapter implements AdapterInterface
{
	private List<T> recommendations;
	private Context context;
	private DismissListener listener;
	private Bitmap placeholder = null;
	
	public ListRecommendationAdapter(ArrayList<T> recommendations, Context context)
	{
		this.recommendations = recommendations;
		this.context = context;
	}
	
	public void setOnDismissListener(DismissListener listener)
	{
		this.listener = listener;
	}
	
	public void refreshData(List<T> recommendations)
	{
		this.recommendations = recommendations;
		this.notifyDataSetChanged();
	}
	
	public ArrayList<T> getRecommendations()
	{
		return (ArrayList<T>) recommendations;
	}
	
	@Override
	public void clear()
	{
		this.recommendations.clear();
		this.notifyDataSetChanged();
	}
	
	@Override
    public int getCount() 
    {
        return recommendations.size();
    }

    @Override
    public Object getItem(int position) 
    {
        return null;
    }
    
    @Override
    public long getItemId(int position) 
    {
        return 0;
    }
    
    @Override
    public int getItemViewType(int position) 
	{
		return position;
    }

    @Override
	public View getView(final int position, View convertView, ViewGroup parent) 
    {
    	final ViewHolder holder;

        if (convertView == null) 
        {
        	convertView = LayoutInflater.from(context).inflate(R.layout.list_item_recommendation, null);
            holder = new ViewHolder();
            holder.ivFanart = (ImageView)convertView.findViewById(R.id.imageViewFanart);
            holder.ivDismiss = (ImageView)convertView.findViewById(R.id.imageViewDismiss);
            holder.tvShow = (TextView)convertView.findViewById(R.id.textViewShow);
            int width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
            int height = (int) (width * Image.RATIO_FANART);
            holder.ivFanart.setLayoutParams(new RelativeLayout.LayoutParams(width, height));           
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
        
        final T s = recommendations.get(position);
        
        holder.ivDismiss.setOnClickListener(new OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
				if(listener != null)
					listener.onDismiss(s.getId());
			}
		});
        
        Image i = new Image(s.getId(), s.getImages().fanart, Image.FANART);
        AQuery aq = new AQuery(convertView);
        if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
            aq.id(holder.ivFanart).image(placeholder);
        else
        	aq.id(holder.ivFanart).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);
                        
        holder.tvShow.setText(s.getTitle());

        return convertView;
    }
    
    private static class ViewHolder 
    {
    	private ImageView ivFanart;
    	private ImageView ivDismiss;
    	private TextView tvShow;
    }
    
    public interface DismissListener
    {
    	public void onDismiss(String id);
    }
}
