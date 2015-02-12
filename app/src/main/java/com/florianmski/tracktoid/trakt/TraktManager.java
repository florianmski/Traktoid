package com.florianmski.tracktoid.trakt;

import android.content.Context;

import com.florianmski.tracktoid.BuildConfig;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidPrefs;
import com.uwetrottmann.trakt.v2.TraktV2;

public class TraktManager extends TraktV2
{	
	private static TraktManager traktManager;

	public static synchronized TraktManager getInstance()
	{	
		if (traktManager == null)
			return null;
		return traktManager;
	}

	private TraktManager(Context context)
	{		
        setIsDebug(BuildConfig.DEBUG);

		setApiKey(context.getString(R.string.trakt_client_id));
        setAccessToken(TraktoidPrefs.INSTANCE.getAccessToken());
	}

	public static void create(Context context)
	{
		traktManager = new TraktManager(context);
	}
}
