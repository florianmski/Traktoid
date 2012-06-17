package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.concurrent.ExecutorService;

import android.content.Context;

import com.florianmski.tracktoid.trakt.tasks.TraktTask;

public abstract class GetTask<TResult> extends TraktTask<TResult>
{
	public GetTask(Context ref) 
	{
		super(ref);
	}
	
	public GetTask(Context ref, ExecutorService executor) 
	{
		super(ref, executor);
	}

	@Override
	protected abstract TResult doTraktStuffInBackground();
}
