package com.florianmski.tracktoid.trakt.tasks.post;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Response;

public class PostTask extends TraktTask
{
	private TraktApiBuilder<?> builder;
	private PostListener pListener;
	private Response r;
	
	public PostTask(TraktManager tm, Fragment fragment, TraktApiBuilder<?> builder, PostListener pListener) 
	{
		super(tm, fragment);
		
		this.builder = builder;
		this.pListener = pListener;
	}
	
	@Override
	protected void doTraktStuffInBackground() 
	{
		showToast("Sending...", Toast.LENGTH_SHORT);
		
		doPrePostStuff();
		
		r = (Response) builder.fire();
		
		doAfterPostStuff();
		
		if(r.error == null)
			showToast("Send to Trakt!", Toast.LENGTH_SHORT);
		else
			showToast("Something goes wrong : " + r.error, Toast.LENGTH_SHORT);
	}
	
	protected void doPrePostStuff() {}
	
	protected void doAfterPostStuff() {}
	
	@Override
	protected void onPostExecute(Boolean success)
	{
		super.onPostExecute(success);
		
		if(success && pListener != null)
			pListener.onComplete(r);
	}
	
	public interface PostListener
	{
		public void onComplete(Response r);
	}
}
