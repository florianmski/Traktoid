package com.florianmski.tracktoid.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.Image;
import com.jakewharton.trakt.entities.Shout;
import com.jakewharton.trakt.entities.TvShow;

public class ListShoutsAdapter extends BaseAdapter
{
	private List<Shout> shouts;
	private Context context;
	private Bitmap placeholder;
	
	public ListShoutsAdapter(List<Shout> shouts2, Context context)
	{
		this.shouts = shouts2;
		this.context = context;
		placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty);
	}
	
	@Override
    public int getCount() 
    {
        return shouts.size();
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

    public View getView(final int position, View convertView, ViewGroup parent) 
    {
    	final ViewHolder holder;

        if (convertView == null) 
        {
        	convertView = LayoutInflater.from(context).inflate(R.layout.list_item_shout, null);
            holder = new ViewHolder();
            holder.ivAvatar = (ImageView)convertView.findViewById(R.id.imageViewAvatar);
            holder.tvUsername = (TextView)convertView.findViewById(R.id.textViewUsername);
            holder.tvDate = (TextView)convertView.findViewById(R.id.textViewDate);
            holder.tvShout = (TextView)convertView.findViewById(R.id.textViewShout);
//            int width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
//            int height = (int) (width * Image.RATIO_FANART);
//            holder.ivAvatar.setLayoutParams(new RelativeLayout.LayoutParams(width, height));           
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
        
        Shout s = shouts.get(position);
        
        AQuery aq = new AQuery(convertView);
        if(aq.shouldDelay(convertView, parent, s.getUser().getAvatar(), 0))
            aq.id(holder.ivAvatar).image(placeholder);
        else
        	aq.id(holder.ivAvatar).image(s.getUser().getAvatar(), true, false, 0, 0, null, android.R.anim.fade_in);
                        
        holder.tvUsername.setText(s.getUser().getUsername());
        holder.tvDate.setText(new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm").format(s.getInserted().getTime()));
        holder.tvShout.setText(s.getShout());

        return convertView;
    }
    
    private static class ViewHolder 
    {
    	private ImageView ivAvatar;
    	private TextView tvShout;
    	private TextView tvUsername;
    	private TextView tvDate;
    }
}
