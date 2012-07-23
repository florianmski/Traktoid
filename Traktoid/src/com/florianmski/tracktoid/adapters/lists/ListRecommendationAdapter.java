package com.florianmski.tracktoid.adapters.lists;

import java.util.List;

import android.app.Activity;
import android.content.Context;
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
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.traktoid.TraktoidInterface;

public class ListRecommendationAdapter<T extends TraktoidInterface<T>> extends RootAdapter<T> 
{
	private DismissListener listener;
	
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

    @SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public View doGetView(int position, View convertView, ViewGroup parent) 
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
            int height = (int) (width * TraktImage.RATIO_FANART);
            holder.ivFanart.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
            holder.bv = (BadgesView<T>)convertView.findViewById(R.id.badgesLayoutBanner);
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
        
        holder.bv.initialize();
        
        TraktImage i = TraktImage.getFanart(s);
        AQuery aq = listAq.recycle(convertView);
        if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
        	setPlaceholder(holder.ivFanart);
        else
        {
        	aq.id(holder.ivFanart).image(i.getUrl(), true, false, 0, 0, null, android.R.anim.fade_in);
        	holder.bv.setTraktItem(s);
        }
                        
        holder.tvShow.setText(s.getTitle());

        return convertView;
    }
    
    private static class ViewHolder 
    {
    	private ImageView ivFanart;
    	private ImageView ivDismiss;
    	private TextView tvShow;
    	@SuppressWarnings("rawtypes")
		private BadgesView bv;
    }
    
    public interface DismissListener
    {
    	public void onDismiss(String id);
    }
}
