package com.florianmski.tracktoid.trakt.tasks.get;

import java.util.concurrent.ExecutorService;

import android.support.v4.app.Fragment;

import com.florianmski.tracktoid.trakt.tasks.TraktTask;

public abstract class GetTask<TResult> extends TraktTask<TResult>
{
	public GetTask(Fragment ref) 
	{
		super(ref);
	}
	
	public GetTask(Fragment ref, ExecutorService executor) 
	{
		super(ref, executor);
	}

	@Override
	protected abstract TResult doTraktStuffInBackground();
}
