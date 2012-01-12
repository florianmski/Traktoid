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
		try 
		{
			return new StatusView(v);
		} 
		catch (Exception e) 
		{
			return null;
		}
	}
	
	private StatusView(View v) throws Exception
	{
		this.rlStatus = (RelativeLayout)v.findViewById(R.id.relativeLayoutStatus);
		
		if(rlStatus == null)
			throw new Exception("There is no status view in this layout");
		
		this.tvStatus = (TextView)rlStatus.findViewById(R.id.textViewStatus);
		this.pbStatus = (ProgressBar)rlStatus.findViewById(R.id.progressBarStatus);
		
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
