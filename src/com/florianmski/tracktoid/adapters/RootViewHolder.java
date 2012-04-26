package com.florianmski.tracktoid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RootViewHolder
{
	public static RootViewHolder get(View convertView, ViewGroup parent, int layout)
	{
		if(convertView == null)
			return new RootViewHolder(parent, layout);
		else
			return (RootViewHolder)convertView.getTag();
	}

	protected final View root;

	protected RootViewHolder(ViewGroup parent, int layout)
	{
		root = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
		root.setTag(this);
	}
}
