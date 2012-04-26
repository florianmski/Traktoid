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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.TvShow;

public class ListSearchAdapter extends RootAdapter<TvShow>
{
	private Bitmap placeholder = null;

	public ListSearchAdapter(Context context, ArrayList<TvShow> shows)
	{
		super(context, shows);
		placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
	}

	@Override
	public View doGetView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_serie, parent, false);
			holder = new ViewHolder();

			holder.rlBanner = (RelativeLayout)convertView.findViewById(R.id.relativeLayoutBanner);
			holder.ivBanner = (ImageView)convertView.findViewById(R.id.imageViewBanner);
			holder.tvSeason = (TextView)convertView.findViewById(R.id.textViewShow);

			int height = (int) (parent.getWidth()*Image.RATIO_BANNER);
			holder.rlBanner.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, height));
			holder.ivBanner.setScaleType(ScaleType.FIT_CENTER);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();

		TvShow show = getItem(position);

		Image i = new Image(show.tvdbId, null, Image.BANNER);
		AQuery aq = new AQuery(convertView);
		if(aq.shouldDelay(position, convertView, parent, i.getUrl()))
			aq.id(holder.ivBanner).image(placeholder);
		else
			aq.id(holder.ivBanner).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);

		holder.tvSeason.setText(show.title);

		return convertView;
	}

	private static class ViewHolder 
	{
		private RelativeLayout rlBanner;
		private ImageView ivBanner;
		private TextView tvSeason;
	}
}
