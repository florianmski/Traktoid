/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid;

import java.io.IOException;
import java.security.MessageDigest;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class Utils 
{
	private static ProgressBar pb;
	private static LinearLayout ll;

	public static void setEmptyView(AdapterView<?> av, Context context)
	{
		ProgressBar emptyView = new ProgressBar(context);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100, 100);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		emptyView.setLayoutParams(lp);
		emptyView.setIndeterminate(true);
		((ViewGroup)av.getParent()).addView(emptyView);
		av.setEmptyView(emptyView);
	}
	
	public static void showLoading(Activity a)
	{
		ll = new LinearLayout(a);
		ll.setGravity(Gravity.CENTER);
		pb = new ProgressBar(a);
		ll.addView(pb);
		a.addContentView(ll, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	public static void showLoading(Fragment f)
	{
		ll = new LinearLayout(f.getActivity());
		ll.setGravity(Gravity.CENTER);
		pb = new ProgressBar(f.getActivity());
		ll.addView(pb);
		((ViewGroup)f.getView()).addView(ll, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	public static void removeLoading()
	{
		if(ll != null)
			ll.removeAllViews();
	}

	//check if device is connected to the internet or not
	public static final boolean isOnline(Context context) 
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}

	public static String addZero(int number)
	{
		return number < 10 ? "0"+number : number+"";
	}

	private static String convertToHex(byte[] data) 
	{ 
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) { 
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do { 
				if ((0 <= halfbyte) && (halfbyte <= 9)) 
					buf.append((char) ('0' + halfbyte));
				else 
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		}
		return buf.toString();
	} 

	public static String SHA1(String text) 
	{ 
		try
		{
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		}
		catch(Exception e) {}
		return null;
	}

	public static boolean isTabletDevice(Context context) 
	{
		if (android.os.Build.VERSION.SDK_INT >= 11) // honeycomb
		{
			Configuration con = context.getResources().getConfiguration();
			return con.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE);
		}
		return false;
	}
	
	public static boolean isLandscape(Activity a)
	{
		return a.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
	public static AnimationDrawable getNyanCat(Context context)
	{
		//prepare nyan cat animation
		final AnimationDrawable animation = new AnimationDrawable();
		for(int i = 0; i < 12; i++)
		{
			try 
			{
				animation.addFrame(Drawable.createFromStream(context.getAssets().open("Frame"+i+".png"), null), 75);
			} 
			catch (IOException e) {}
		}

		animation.setOneShot(false);
		
		return animation;
	}
	
	public static boolean isActivityFinished(Activity a)
	{
		return a == null || a.isFinishing();
	}
}
