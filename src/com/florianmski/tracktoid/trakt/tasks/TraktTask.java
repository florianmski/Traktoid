package com.florianmski.tracktoid.trakt.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.florianmski.tracktoid.TraktListener;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.apibuilder.ApiException;
import com.jakewharton.trakt.TraktException;

public abstract class TraktTask<TResult> extends BackgroundTaskWeak<Fragment, TResult>
{
	protected final static int TOAST = -1;
	protected final static int ERROR = -2;

	protected TraktManager tm;
	protected TraktListener tListener;
	//this not will not display toast
	protected boolean silent = false;
	protected boolean silentConnectionError = false;
	protected boolean inQueue = false;
	protected Exception error;
	protected Context context;
	
	private static List<TraktListener> listeners = new ArrayList<TraktListener>();
	protected static ExecutorService sSingleThreadExecutor = new SingleThreadExecutor();
	
	public TraktTask(Fragment ref) 
	{
		this(ref, null);
	}

	public TraktTask(Fragment ref, ExecutorService executor) 
	{
		super(ref, executor);

		this.tm = TraktManager.getInstance();
		this.context = getRef().getActivity().getApplicationContext();

		try
		{
			tListener = (TraktListener)getRef();
		}
		catch(ClassCastException e){}
		
		setId(this.getClass().getSimpleName());
	}

	@Override
	protected TResult doWorkInBackground() throws Exception 
	{
		if(!Utils.isOnline(context))
		{
			if(getRef() != null && !silentConnectionError)
				handleException(new Exception("Internet connection required!"));

			return doOfflineTraktStuff();
		}
		try
		{
			return doTraktStuffInBackground();
		}
		catch (ApiException e) 
		{
			onFailed(e);
			return null;
		}
		catch (TraktException e) 
		{
			onFailed(e);
			return null;
		}
		catch (IllegalArgumentException e) 
		{
			onFailed(e);
			return null;
		}
	}

	@Override
	protected void onCompleted(TResult result) 
	{
		//has to be executed otherwise tasks will stay in queue even when finished
		//		tm.onAfterTraktRequest(tListener, result != null, inQueue);
		Log.i("Traktoid","task finish!");
	}

	@Override
	protected void onFailed(Exception e)
	{

	}

	@Override
	protected void onPreExecute()
	{
		Log.i("Traktoid","start a task...");
		//		if(getRef() != null)
		//			tm.onBeforeTraktRequest(tListener);
	}

	protected abstract TResult doTraktStuffInBackground();

	protected TResult doOfflineTraktStuff()
	{
		return null;
	}

	protected void showToast(String message, int duration)
	{
		if(!silent)
			publishProgress(TOAST, null, String.valueOf(duration), message);
	}

	@Override
	protected void onProgressPublished(int progress, TResult tmpResult, String... values)
	{
//		if(getRef() != null)
//		{
			if(progress == TOAST)
				Toast.makeText(context, values[1], Integer.parseInt(values[0])).show();
			//			else if(progress == ERROR)
			//				tm.onErrorTraktRequest(tListener, error);
//		}
	}

	private void handleException(Exception e)
	{
		e.printStackTrace();
		this.error = e;
		this.publishProgress(ERROR, null);
		showToast("Error : " + e.getMessage(), Toast.LENGTH_LONG);
	}

	public TraktTask<TResult> silent(boolean silent) 
	{
		this.silent = silent;
		return this;
	}

	//do nothin special in case of connection error (not even showing a toast)
	public TraktTask<TResult> silentConnectionError(boolean silentConnectionError) 
	{
		this.silentConnectionError = silentConnectionError;
		return this;
	}

	public void fire() 
	{
		execute();
	}

	public static <T extends TraktoidInterface<T>> void addObserver(TraktListener<T> listener)
	{
		listeners.add(listener);
	}

	public static <T extends TraktoidInterface<T>> void removeObserver(TraktListener<T> listener)
	{
		listeners.remove(listener);
	}

	public static <T extends TraktoidInterface<T>> void traktItemUpdated(T traktItem)
	{
		for(TraktListener<T> l : listeners)
		{
			try
			{
				l.onTraktItemUpdated(traktItem);
			}
			catch(ClassCastException e) {}
		}
	}

	public static <T extends TraktoidInterface<T>> void traktItemRemoved(T traktItem)
	{
		for(TraktListener<T> l : listeners)
		{
			try
			{
				l.onTraktItemRemoved(traktItem);
			}
			catch(ClassCastException e) {}
		}
	}
}
