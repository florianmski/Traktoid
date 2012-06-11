package com.florianmski.tracktoid.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.ListCheckerManager.ListCheckerListener;
import com.florianmski.tracktoid.adapters.RootAdapter;

public class CheckableGridView<T> extends GridView implements ListCheckerListener
{
	private ListCheckerManager<T> lcm;
	private int position;

	private OnItemClickListener itemListener;
	private OnItemLongClickListener longClickListener;
	private OnItemClickListener itemActionModeListener = new OnItemClickListener() 
	{
		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
		{
			if(lcm.isActivated())
			{
				T item = ((RootAdapter<T>) getAdapter()).getItem(position);
				lcm.check(item);
				lcm.checkView(item, v);
				
				//apparently gridView has some issue to correctly update the view if it's the first position so do it ourself
				if(position == 0)
					((BaseAdapter) getAdapter()).notifyDataSetChanged();
			}
			else if(itemListener != null)
				itemListener.onItemClick(arg0, v, position, id);				
		}
	};

	public CheckableGridView(Context context) 
	{
		super(context);
	}

	public CheckableGridView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}

	public CheckableGridView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
	}

	public void initialize(final SherlockFragment fragment, int position, final ListCheckerManager<T> lcm)
	{
		this.lcm = lcm;
		this.position = position;

		setOnItemLongClickListener(longClickListener = new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) 
			{
				//if startActionMode already active, considering the longclick is a click
				if(lcm.isActivated())
					itemActionModeListener.onItemClick(arg0, v, position, id);
				else
				{
					ActionMode mode = fragment.getSherlockActivity().startActionMode(lcm.getCallback());
					lcm.setActionMode(mode);
					lcm.toggle();
					//a negative position means that user starts action mode with something else than a long click on the list
					if(position >= 0)
						itemActionModeListener.onItemClick(arg0, v, position, id);
				}
				return true;
			}
		});

		super.setOnItemClickListener(itemActionModeListener);
	}

	public void start()
	{
		if(longClickListener != null)
			longClickListener.onItemLongClick(null, null, -1, 0);
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		itemListener = listener;		
	}
	@Override
	public void onActionModeFinished() 
	{
		((BaseAdapter) getAdapter()).notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCheckAll(int position) 
	{
		if(this.position == position)
		{
			for(T item : ((RootAdapter<T>) getAdapter()).getItems())
			{
				if(!lcm.isChecked(item))
					lcm.check(item);
			}
			((BaseAdapter) getAdapter()).notifyDataSetChanged();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCheckNone(int position) 
	{
		Log.d("test","onCheckNone : " + position);
		if(this.position == position)
		{
			for(T item : ((RootAdapter<T>) getAdapter()).getItems())
			{
				if(lcm.isChecked(item))
					lcm.check(item);
			}
			((BaseAdapter) getAdapter()).notifyDataSetChanged();
		}
	}
}
