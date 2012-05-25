/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;

public class TraktItemsTask<T extends TraktoidInterface> extends TraktTask<List<T>>
{
	private List<T> traktItems = new ArrayList<T>();
	private TraktApiBuilder<?> builder;
	private boolean sort;
	private TraktItemsListener<T> listener;
	
	public TraktItemsTask(Fragment fragment, TraktItemsListener<T> listener, TraktApiBuilder<?> builder, boolean sort) 
	{
		super(fragment);
		
		this.builder = builder;
		this.sort = sort;
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<T> doTraktStuffInBackground()
	{		
		traktItems = (List<T>) builder.fire();
		
		if(sort && traktItems != null && traktItems.size() > 0)
			Collections.sort(traktItems);
		
		return traktItems;
	}
	
	@Override
	protected void onCompleted(List<T> traktItems)
	{		
		if(traktItems != null && getRef() != null)
			listener.onTraktItems(traktItems);
	}
	
	public interface TraktItemsListener<E extends TraktoidInterface>
	{
		public void onTraktItems(List<E> traktItems);
	}
}
