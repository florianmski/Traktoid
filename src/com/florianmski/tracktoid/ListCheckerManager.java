package com.florianmski.tracktoid;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class ListCheckerManager<T>
{	
	private static ListCheckerManager<?> instance;
	private static ActionMode mode;

	private List<T> checkedItems = new ArrayList<T>();
	
	private ActionMode.Callback mActionModeListener;
	
	private boolean on = false;
	private int pageSelected;
	
	private List<ListCheckerListener> listeners;
	
	private int viewSelectedResId = -1;
	private int noSelectedColorResId = -1;
	private int selectedColorResId = -1;

	public ListCheckerManager() 
	{
		listeners = new ArrayList<ListCheckerListener>();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ListCheckerManager<T> getInstance()
	{
		if(instance == null)
			instance = new ListCheckerManager<T>();
		return (ListCheckerManager<T>) instance;
	}

	public void addListener(ListCheckerListener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(ListCheckerListener listener)
	{
		listeners.remove(listener);
	}
	
	public void check(T item)
	{
		if(!isChecked(item))
			checkedItems.add(item);
		else
			checkedItems.remove(item);
	}
	
	public View checkView(T item, View v)
	{
		if(isChecked(item))
			if(viewSelectedResId != -1)
				v.findViewById(viewSelectedResId).setBackgroundResource(selectedColorResId == -1 ? R.color.list_pressed_color : selectedColorResId);
			else
				v.setBackgroundResource(selectedColorResId == -1 ? R.color.list_pressed_color : selectedColorResId);
		else
			if(viewSelectedResId != -1)
				v.findViewById(viewSelectedResId).setBackgroundResource(noSelectedColorResId == -1 ? R.drawable.selector_list_classic : noSelectedColorResId);
			else
				v.setBackgroundResource(noSelectedColorResId == -1 ? R.drawable.selector_list_classic : noSelectedColorResId);
		
		return v;
	}
	
	public boolean isChecked(T item)
	{
		return checkedItems.contains(item);
	}
	
	public List<T> getItemsList() 
	{
		return checkedItems;
	}
	
//	public List<String> getIdsList() 
//	{
//		return checkedItems;
//	}
	
//	public <T> List<T> getItemsList() 
//	{
//		List<T> items = new ArrayList<T>();
//		for(ListCheckerListener l : listeners)
//			items.addAll(l.<T>getItemsList());
//		return items;
//	}
	
	public void clear()
	{
		Log.e("test", "call to clear()");
		checkedItems.clear();
	}
	
	public void toggle()
	{
		on = !on;
	}
	
	public void setActionMode(ActionMode mode)
	{
		ListCheckerManager.mode = mode;
	}
	
	public static void finish()
	{
		if(mode != null)
			mode.finish();
	}
	
	public boolean isActivated()
	{
		return on;
	}
	
	public void setPageSelected(int position)
	{
		pageSelected = position;
	}
	
	public void setOnActionModeListener(final ActionMode.Callback listener)
	{
		this.mActionModeListener = new CustomActionMode(listener);
	}
	
	public ActionMode.Callback getCallback()
	{
		return mActionModeListener;
	}
	
	public void setSelectedViewResId(int id)
	{
		this.viewSelectedResId = id;
	}
	
	public void setNoSelectedColorResId(int id)
	{
		this.noSelectedColorResId = id;
	}
	
	public void setSelectedColorResId(int id)
	{
		this.selectedColorResId = id;
	}
	
	public class CustomActionMode implements ActionMode.Callback
	{	
		private ActionMode.Callback listener;
		
		public CustomActionMode(ActionMode.Callback listener)
		{
			this.listener = listener;
		}
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
		{
			return false;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) 
		{
			menu.add(0, R.id.action_bar_all, 0, "all").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add(0, R.id.action_bar_none, 0, "none").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			return listener.onCreateActionMode(mode, menu);
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
		{
			if(item.getItemId() == R.id.action_bar_all)
				for(ListCheckerListener l : listeners)
					l.onCheckAll(pageSelected);
			else if(item.getItemId() == R.id.action_bar_none)
				for(ListCheckerListener l : listeners)
					l.onCheckNone(pageSelected);
			return listener.onActionItemClicked(mode, item);
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) 
		{
			listener.onDestroyActionMode(mode);
			clear();
			toggle();
			
			for(ListCheckerListener l : listeners)
				l.onActionModeFinished();
		}		
	}
	
	public interface ListCheckerListener
	{
		public void onActionModeFinished();
		public void onCheckAll(int position);
		public void onCheckNone(int position);
	}
}
