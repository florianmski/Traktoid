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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.traktoid.TraktoidInterface;

public class ListRecommendationAdapter<T extends TraktoidInterface<T>> extends RootAdapter<T> 
{
	private DismissListener listener;
	private Bitmap placeholder = null;
	
	public ListRecommendationAdapter(List<T> recommendations, Context context)
	{
		super(context, recommendations);
	}
	
	public void setOnDismissListener(DismissListener listener)
	{
		this.listener = listener;
	}
    
    @Override
    public int getItemViewType(int position) 
	{
		return position;
    }

    @Override
	public View doGetView(final int position, View convertView, ViewGroup parent) 
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
        
        final T s = getItem(position);
        
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
        if(aq.shouldDelay(position, convertView, parent, i.getUrl()))
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
