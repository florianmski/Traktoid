package com.florianmski.tracktoid.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

//inspired by https://github.com/slapperwan/gh4a/blob/master/src/com/gh4a/adapter/RootAdapter.java
public abstract class RootAdapter<T> extends BaseAdapter 
{
    protected List<T> items;
    protected Context context;

    public RootAdapter(Context context, List<T> items) 
    {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() 
    {
        return items == null ? 0 : items.size();
    }
    @Override
    public T getItem(int position) 
    {
        return items.get(position);
    }
    
    @Override
    public long getItemId(int position) 
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        return doGetView(position, convertView, parent);
    }

    public void add(T object, boolean toTheEnd) 
    {
    	if(toTheEnd)
    		items.add(object);
    	else
    		items.add(0, object);
        notifyDataSetChanged();
    }
    
    public void remove(T object) 
    {
        items.remove(object);
        notifyDataSetChanged();
    }

    public List<T> getItems() 
    {
        return items;
    }
    
    public void updateItems(List<T> items) 
    {
        this.items = items;
        notifyDataSetChanged();
    }
    
    public void clear() 
    {
        items.clear();
        notifyDataSetChanged();
    }
    
    public abstract View doGetView(int position, View convertView, ViewGroup parent);
}
