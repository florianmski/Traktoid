package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.Context;

import com.florianmski.tracktoid.trakt.tasks.TraktTask;

public abstract class GetTask<TResult> extends TraktTask<TResult>
{
	public GetTask(Activity ref) 
	{
		super(ref);
	}
	
	public GetTask(Activity ref, ExecutorService executor) 
	{
		super(ref, executor);
	}
	
	public GetTask(Context context, ExecutorService executor) 
	{
		super(context, executor);
	}

	@Override
	protected abstract TResult doTraktStuffInBackground();
}
