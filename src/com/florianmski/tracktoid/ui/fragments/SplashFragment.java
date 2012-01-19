package com.florianmski.tracktoid.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.ui.activities.phone.HomeActivity;
import com.florianmski.tracktoid.ui.activities.phone.LoginActivity;

public class SplashFragment extends TraktFragment
{
	private static final int STOPSPLASH = 0;
	//time in milliseconds
	private long SPLASHTIME = 2000;

	//handler for splash screen
	private Handler splashHandler = new Handler();
	private Runnable splasRunnable = new Runnable() 
	{
		@Override
		public void run() 
		{
			getActivity().finish();
			getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			Intent intent;
			
			//if we don't have user pass or username, go to login activity
			if(prefs.getString(TraktoidConstants.PREF_PASSWORD, null) == null || prefs.getString(TraktoidConstants.PREF_USERNAME, null) == null)
				intent = new Intent(getActivity(), LoginActivity.class);
			else
				intent = new Intent(getActivity(), HomeActivity.class);
			
			startActivity(intent);
			getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	};

	public static SplashFragment newInstance(Bundle args)
	{
		SplashFragment f = new SplashFragment();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getSupportActivity().getSupportActionBar().hide();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		//TODO
		//		new ActivityTask(tm, fragment)

		splashHandler.postDelayed(splasRunnable, SPLASHTIME);		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_splash, null);
		
		TextView tvVersion = (TextView)v.findViewById(R.id.textViewVersion);
		ImageView ivLogo = (ImageView)v.findViewById(R.id.imageViewLogo);

		try 
		{
			tvVersion.setText("v " + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
		} 
		catch (NameNotFoundException e) {}

		Animation a = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
		a.setDuration(1500);
		ivLogo.startAnimation(a);
		ivLogo.setImageResource(R.drawable.logo_512);
		
		ivLogo.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				splashHandler.removeMessages(STOPSPLASH);
				splashHandler.post(splasRunnable);
			}
		});
		
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, 0, 0, "OK")
			.setIcon(R.drawable.icon)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public void onDestroy()
	{
		splashHandler.removeMessages(STOPSPLASH);
		super.onDestroy();
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		// TODO Auto-generated method stub	
	}

}
