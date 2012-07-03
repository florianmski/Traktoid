package com.florianmski.tracktoid.trakt.tasks;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.florianmski.tracktoid.TraktListener;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.apibuilder.ApiException;
import com.jakewharton.trakt.TraktException;

public abstract class TraktTask<TResult> extends BackgroundTaskWeak<Context, TResult>
{
	protected final static int TOAST = -1;
	protected final static int EVENT = -2;
	protected final static int ERROR = -3;

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

	public TraktTask(Context ref) 
	{
		this(ref, null);
	}

	public TraktTask(Context ref, ExecutorService executor) 
	{
		super(ref, executor);

		this.tm = TraktManager.getInstance();
		this.context = getRef().getApplicationContext();

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
				onFailed(new Exception("Internet connection required!"));

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
		if(result != null)
			sendEvent(result);
		
		Log.i("Traktoid","task ended!");
	}

	protected abstract void sendEvent(TResult result);

	@Override
	protected void onFailed(Exception e)
	{
		e.printStackTrace();
		this.error = e;
		this.publishProgress(ERROR, null);
		showToast("Error : " + e.getMessage(), Toast.LENGTH_LONG);
	}

	@Override
	protected void onPreExecute()
	{
		Log.i("Traktoid","start a task...");
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
		if(progress == TOAST)
			Toast.makeText(context, values[1], Integer.parseInt(values[0])).show();
		else if(progress == ERROR)
		{
			//TODO something smart
		}
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
		List<T> traktItems = new ArrayList<T>();
		traktItems.add(traktItem);
		traktItemsUpdated(traktItems);
	}

	public static <T extends TraktoidInterface<T>> void traktItemRemoved(T traktItem)
	{
		List<T> traktItems = new ArrayList<T>();
		traktItems.add(traktItem);
		traktItemsRemoved(traktItems);
	}
	
	public static <T extends TraktoidInterface<T>> void traktItemsUpdated(List<T> traktItems)
	{
		for(TraktListener<T> l : listeners)
		{
			try
			{
				//this is quite ugly, generics are new for me so it might have other cleaner solution
				//basically I check if the current TraktListener is parameterized with the traktItems list type
				//(avoid for instance that shows are added to movies grid when there is a synchronization)
				if(!traktItems.isEmpty() && ((ParameterizedType) l.getClass().getGenericSuperclass()).getActualTypeArguments()[0] == traktItems.get(0).getClass())
					l.onTraktItemsUpdated(traktItems);
			}
			catch(ClassCastException e) {e.printStackTrace();}
		}
	}

	public static <T extends TraktoidInterface<T>> void traktItemsRemoved(List<T> traktItems)
	{
		for(TraktListener<T> l : listeners)
		{
			try
			{
				if(!traktItems.isEmpty())
					l.onTraktItemsRemoved(traktItems);
			}
			catch(ClassCastException e) {}
		}
	}
}
