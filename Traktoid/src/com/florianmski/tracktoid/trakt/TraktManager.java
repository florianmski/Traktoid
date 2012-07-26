package com.florianmski.tracktoid.trakt;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.florianmski.tracktoid.Utils;
import com.jakewharton.trakt.ServiceManager;

public class TraktManager extends ServiceManager implements OnSharedPreferenceChangeListener
{	
	private static TraktManager traktManager;

	private static String username;
	private static String password;

	@SuppressWarnings("unused")
	private Context context;

	public static synchronized TraktManager getInstance()
	{	
		//should not arrive
		if (traktManager == null)
			return null;
		return traktManager;
	}

	private TraktManager(Context context) 
	{		
		this.context = context;
   
		try 
		{
			InputStream inputStream = context.getAssets().open("trakt_key.txt");
			String key = Utils.readInputStream(inputStream).trim();
			setApiKey(key);
		} 
		catch (IOException e) 
		{
			throw new IllegalStateException("You must put your trakt api key in assets/trakt_key.txt");
		}

		setAccountInformations(context);
	}

	public static void create(Context context)
	{
		traktManager = new TraktManager(context);
	}

	public void setAccountInformations(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		username = prefs.getString("editTextUsername", "test1").trim();
		password = prefs.getString("editTextPassword", "test1").trim();

		//in Traktoid <= 0.6, password was stored non encrypted (I know...)
		//so store it encrypted now!
		if(!prefs.getBoolean("sha1", false))
		{
			password = Utils.SHA1(password);
			prefs.edit().putString("editTextPassword", password);
			prefs.edit().putBoolean("sha1", true);
		}

		setAuthentication(username, password);

		prefs.registerOnSharedPreferenceChangeListener(this);
	}

	public static String getUsername()
	{
		return username;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		if(key.equals("editTextUsername"))
			username = sharedPreferences.getString("editTextUsername", "test1").trim();
		else if(key.equals("editTextPassword"))
			password = sharedPreferences.getString("editTextPassword", "test1").trim();

		setAuthentication(username, password);
	}
}
