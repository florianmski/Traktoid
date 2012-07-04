package com.florianmski.tracktoid.adapters.lists;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.jakewharton.trakt.entities.TvShow;

public class ListSearchAdapter extends RootAdapter<TvShow>
{
	public ListSearchAdapter(Context context, List<TvShow> shows)
	{
		super(context, shows);
	}

	@Override
	public View doGetView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_serie, parent, false);
			holder = new ViewHolder();

			holder.bvBanner = (BadgesView)convertView.findViewById(R.id.badgesLayoutBanner);
			holder.ivBanner = (ImageView)convertView.findViewById(R.id.imageViewBanner);
			holder.tvSeason = (TextView)convertView.findViewById(R.id.textViewShow);

			int height = (int) (parent.getWidth()*TraktImage.RATIO_BANNER);
			holder.bvBanner.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, height));
			holder.ivBanner.setScaleType(ScaleType.FIT_CENTER);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();

		TvShow show = getItem(position);

		holder.bvBanner.initialize();
		
		TraktImage i = TraktImage.getBanner(show);
		AQuery aq = listAq.recycle(convertView);
		if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
			setPlaceholder(holder.ivBanner);
		else
		{
			holder.bvBanner.setTraktItem(show);
			aq.id(holder.ivBanner).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);
		}

		holder.tvSeason.setText(show.title);

		return convertView;
	}

	private static class ViewHolder 
	{
		private BadgesView bvBanner;
		private ImageView ivBanner;
		private TextView tvSeason;
	}
}
