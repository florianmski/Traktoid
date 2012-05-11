package com.florianmski.tracktoid.trakt.tasks.post;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Response;

public class PostTask extends TraktTask
{
	protected List<TraktApiBuilder<?>> builders;
	protected PostListener pListener;
	protected Response r;

	public PostTask(TraktManager tm, Fragment fragment, TraktApiBuilder<?> builder, PostListener pListener) 
	{
		super(tm, fragment);

		this.builders = new ArrayList<TraktApiBuilder<?>>();
		if(builder != null)
			this.builders.add(builder);
		this.pListener = pListener;
	}

	@Override
	protected boolean doTraktStuffInBackground() 
	{
		showToast("Sending...", Toast.LENGTH_SHORT);

		doPrePostStuff();

		boolean ok = true;
		for(TraktApiBuilder<?> builder : builders)
		{
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
				ok &= true;
			}
			//sometimes there is no response but the action was performed (ex : remove sthg from collection)
			else if(r == null)
			{
				showToast("Send to Trakt!", Toast.LENGTH_SHORT);
				doAfterPostStuff();
				ok &= true;
			}
			else
			{
				showToast("Something goes wrong : " + r.error, Toast.LENGTH_SHORT);
				ok &= false;
			}
		}
		
		return ok;
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
