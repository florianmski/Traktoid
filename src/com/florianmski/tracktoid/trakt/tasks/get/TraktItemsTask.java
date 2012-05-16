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

import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;

public class TraktItemsTask<T extends TraktoidInterface<T>> extends TraktTask
{
	private List<T> traktItems = new ArrayList<T>();
	private TraktApiBuilder<?> builder;
	private boolean sort;
	private TraktItemsListener<T> listener;
	
	public TraktItemsTask(TraktManager tm, Fragment fragment, TraktItemsListener<T> listener, TraktApiBuilder<?> builder, boolean sort) 
	{
		super(tm, fragment);
		
		this.builder = builder;
		this.sort = sort;
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean doTraktStuffInBackground()
	{		
		traktItems = (List<T>) builder.fire();
		
		if(sort)
			Collections.sort(traktItems);
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success && !Utils.isActivityFinished(fragment.getActivity()))
			listener.onTraktItems(traktItems);
	}
	
	public interface TraktItemsListener<E extends TraktoidInterface<E>>
	{
		public void onTraktItems(List<E> traktItems);
	}
}
