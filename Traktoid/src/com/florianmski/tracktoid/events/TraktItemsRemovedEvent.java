package com.florianmski.tracktoid.events;

import java.util.List;

import com.florianmski.traktoid.TraktoidInterface;

public class TraktItemsRemovedEvent<T extends TraktoidInterface<T>> extends TraktItemsEvent<T> 
{
	public TraktItemsRemovedEvent(List<T> traktItems) 
	{
		super(traktItems);
	}
	
	public TraktItemsRemovedEvent(T traktItem) 
	{
		super(traktItem);
	}	
}
