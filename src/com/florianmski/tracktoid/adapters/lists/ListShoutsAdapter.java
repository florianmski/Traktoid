package com.florianmski.tracktoid.adapters.lists;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.jakewharton.trakt.entities.Shout;

public class ListShoutsAdapter extends RootAdapter<Shout>
{
	public ListShoutsAdapter(List<Shout> shouts, Context context)
	{
		super(context, shouts);
	}

	public void revealSpoiler(int position)
	{
		if(position < getCount() && items.get(position).spoiler)
		{
			items.get(position).spoiler = false;
			this.notifyDataSetChanged();
		}
	}

	@Override
	public View doGetView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null) 
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_shout, null);
			holder = new ViewHolder();
			holder.ivAvatar = (ImageView)convertView.findViewById(R.id.imageViewAvatar);
			holder.ivSpoiler = (ImageView)convertView.findViewById(R.id.imageViewSpoiler);
			holder.tvUsername = (TextView)convertView.findViewById(R.id.textViewUsername);
			holder.tvDate = (TextView)convertView.findViewById(R.id.textViewDate);
			holder.tvShout = (TextView)convertView.findViewById(R.id.textViewShout);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();

		Shout s = getItem(position);

		final AQuery aq = listAq.recycle(convertView);
		BitmapAjaxCallback cb = new BitmapAjaxCallback()
		{
			@Override
			public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
			{     
				//                    iv.setImageBitmap(Utils.shadowBitmap(Utils.roundBitmap(bm)));
				aq.id(iv).image(Utils.roundBitmap(bm)).animate(android.R.anim.fade_in);
			}

		}.url(s.user.avatar).animation(android.R.anim.fade_in).fileCache(false).memCache(true);

		if(aq.shouldDelay(convertView, parent, s.user.avatar, 0))
			setPlaceholder(holder.ivAvatar);
		else
			aq.id(holder.ivAvatar).image(cb);

		holder.tvUsername.setText(s.user.username);
		holder.tvDate.setText(new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm").format(s.inserted.getTime()));
		holder.tvShout.setText(s.shout);

		if(s.spoiler)
		{
			holder.tvShout.setVisibility(View.GONE);
			holder.ivSpoiler.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.tvShout.setVisibility(View.VISIBLE);
			holder.ivSpoiler.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static class ViewHolder 
	{
		private ImageView ivAvatar;
		private ImageView ivSpoiler;
		private TextView tvShout;
		private TextView tvUsername;
		private TextView tvDate;
	}
}
