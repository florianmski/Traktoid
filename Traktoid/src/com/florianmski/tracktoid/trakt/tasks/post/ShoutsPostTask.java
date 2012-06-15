package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.jakewharton.trakt.TraktApiBuilder;

public class ShoutsPostTask extends PostTask
{
	public ShoutsPostTask(TraktManager tm, Fragment fragment, TraktApiBuilder<?> builder, PostListener pListener) 
	{
		super(tm, fragment, builder, pListener);
	}

	@Override
	protected void doAfterPostStuff() 
	{
		
	}
}
