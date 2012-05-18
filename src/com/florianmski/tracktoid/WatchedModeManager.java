package com.florianmski.tracktoid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.florianmski.tracktoid.ui.fragments.season.PagerSeasonFragment.OnWatchedModeListener;


public class WatchedModeManager
{
	private static WatchedModeManager instance;
	private List<OnWatchedModeListener> listeners;
	private boolean watchedMode = false;

	private WatchedModeManager() 
	{
		listeners = new ArrayList<OnWatchedModeListener>();
	}

	public static WatchedModeManager getInstance()
	{
		if(instance == null)
			instance = new WatchedModeManager();

		return instance;
	}

	public void addListener(OnWatchedModeListener listener)
	{
		listeners.add(listener);
		listener.setWatchedMode(watchedMode);
	}

	public void removeListener(OnWatchedModeListener listener)
	{
		listeners.remove(listener);
	}

	public void setWatchedMode(boolean on)
	{		
		if(watchedMode != on)
		{
			this.watchedMode = on;

			for(OnWatchedModeListener listener : listeners)
				listener.setWatchedMode(on);
		}
	}

	public List<Map<Integer, Boolean>> getWatchedList() 
	{
		List<Map<Integer, Boolean>> list = new ArrayList<Map<Integer,Boolean>>();
		for(OnWatchedModeListener listener : listeners)
			list.add(listener.getWatchedList());

		return list;
	}
}
