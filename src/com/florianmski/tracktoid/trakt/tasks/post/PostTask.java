package com.florianmski.tracktoid.trakt.tasks.post;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Response;

public class PostTask extends TraktTask<Response>
{
	protected List<TraktApiBuilder<?>> builders;
	protected PostListener pListener;
	protected Response r;

	public PostTask(Context context, TraktApiBuilder<?> builder, PostListener pListener) 
	{
		//post request one by one
		super(context, sSingleThreadExecutor);

		this.builders = new ArrayList<TraktApiBuilder<?>>();
		if(builder != null)
			this.builders.add(builder);
		this.pListener = pListener;
	}

	@Override
	protected Response doTraktStuffInBackground() 
	{
		showToast("Sending...", Toast.LENGTH_SHORT);

		doPrePostStuff();

		int i = 0;
		do
		{
			TraktApiBuilder<?> builder = builders.get(i);
			try
			{
				r = (Response) builder.fire();
			}
			catch(ClassCastException e) {}
			
			if(r != null && r.error == null)
			{
				//			showToast("Send to Trakt!", Toast.LENGTH_SHORT);
				showToast(r.message, Toast.LENGTH_SHORT);
				doAfterPostStuff();
			}
			//sometimes there is no response but the action was performed (ex : remove sthg from collection)
			else if(r == null)
			{
				showToast("Send to Trakt!", Toast.LENGTH_SHORT);
				//we don't want to return null because returning null is when the request has failed (exception, see TraktTask)
				r = new Response();
				doAfterPostStuff();
			}
			else
			{
				showToast("Something goes wrong : " + r.error, Toast.LENGTH_SHORT);
			}
			
			i++;
		}
		while(r != null && i < builders.size());
		
		return r;
	}

	protected void doPrePostStuff() {}

	protected void doAfterPostStuff() {}

	@Override
	protected void onCompleted(Response r)
	{
		if(pListener != null && getRef() != null)
			pListener.onComplete(r, r != null);
		
		if(r != null)
			sendEvent(r);
	}

	public interface PostListener
	{
		public void onComplete(Response r, boolean success);
	}

	@Override
	protected void sendEvent(Response result) {}
}
