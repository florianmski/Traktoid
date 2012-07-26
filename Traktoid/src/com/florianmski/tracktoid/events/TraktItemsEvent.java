package com.florianmski.tracktoid.events;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.florianmski.traktoid.TraktoidInterface;

public class TraktItemsEvent<T extends TraktoidInterface<T>>
{
	private List<T> traktItems;

	public TraktItemsEvent(List<T> traktItems)
	{
		this.traktItems = traktItems;
	}

	public TraktItemsEvent(T traktItem)
	{
		this.traktItems = new ArrayList<T>();
		this.traktItems.add(traktItem);
	}

	public List<T> getTraktItems(Object o)
	{
		//this is quite ugly, generics are new for me so it might have other cleaner solution
		//basically I check if the current TraktListener is parameterized with the traktItems list type
		//(avoid for instance that shows are added to movies grid when there is a synchronization)
		if(!traktItems.isEmpty() 
				&& ((ParameterizedType) o.getClass().getGenericSuperclass()).getActualTypeArguments()[0] == traktItems.get(0).getClass())
			return traktItems;

		return null;
	}
}
