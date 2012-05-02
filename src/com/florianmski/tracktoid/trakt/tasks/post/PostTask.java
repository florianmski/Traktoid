package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Response;

public class PostTask extends TraktTask
{
	protected TraktApiBuilder<?> builder;
	protected PostListener pListener;
	protected Response r;

	public PostTask(TraktManager tm, Fragment fragment, TraktApiBuilder<?> builder, PostListener pListener) 
	{
		super(tm, fragment);

		this.builder = builder;
		this.pListener = pListener;
	}

	@Override
	protected boolean doTraktStuffInBackground() 
	{
		showToast("Sending...", Toast.LENGTH_SHORT);

		doPrePostStuff();

		try
		{
			r = (Response) builder.fire();
		}
		catch(ClassCastException e)
		{
			e.printStackTrace();
		}

		if(r!= null && r.error == null)
		{
			//			showToast("Send to Trakt!", Toast.LENGTH_SHORT);
			showToast(r.message, Toast.LENGTH_SHORT);
			doAfterPostStuff();
			return true;
		}
		//sometimes there is no response but the action was performed (ex : remove sthg from collection)
		else if(r == null)
		{
			showToast(r.message, Toast.LENGTH_SHORT);
			doAfterPostStuff();
			return true;
		}
		else
		{
			showToast("Something goes wrong : " + r.error, Toast.LENGTH_SHORT);
			return false;
		}			
	}

	protected void doPrePostStuff() {}

	protected void doAfterPostStuff() {}

	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);

		if(pListener != null)
			pListener.onComplete(r, success);
	}

	public interface PostListener
	{
		public void onComplete(Response r, boolean success);
	}
}
