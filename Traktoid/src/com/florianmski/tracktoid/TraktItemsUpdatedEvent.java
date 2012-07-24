package com.florianmski.tracktoid;

import java.util.List;

import com.florianmski.traktoid.TraktoidInterface;

public class TraktItemsUpdatedEvent<T extends TraktoidInterface<T>> extends TraktItemsEvent<T> 
{
	public TraktItemsUpdatedEvent(List<T> traktItems) 
	{
		super(traktItems);
	}
	
	public TraktItemsUpdatedEvent(T traktItem) 
	{
		super(traktItem);
	}	
}
