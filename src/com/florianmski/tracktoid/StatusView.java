package com.florianmski.tracktoid;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatusView 
{
	private RelativeLayout rlStatus;
	private TextView tvStatus;
	private ProgressBar pbStatus;

	public static StatusView instantiate(View v)
	{
		return new StatusView(v);
	}
	
	public StatusView(View v)
	{
		this.rlStatus = (RelativeLayout)v.findViewById(R.id.relativeLayoutStatus);
		
		if(rlStatus == null)
			return;
		
		this.tvStatus = (TextView)v.findViewById(R.id.textViewStatus);
		this.pbStatus = (ProgressBar)v.findViewById(R.id.progressBarStatus);
		
		this.hide().text(null);
		rlStatus.setVisibility(View.VISIBLE);
	}
	
	public StatusView show()
	{
		this.pbStatus.setVisibility(View.VISIBLE);
		return this;
	}
	
	public StatusView hide()
	{
		this.pbStatus.setVisibility(View.GONE);
		return this;
	}
	
	public StatusView text(String text)
	{
		if(text == null)
			this.tvStatus.setVisibility(View.GONE);
		else
		{
			this.tvStatus.setVisibility(View.VISIBLE);
			this.tvStatus.setText(text);
		}
		return this;
	}
}
