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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.post.WatchedEpisodesTask;

public class Utils 
{
	//check if device is connected to the internet or not
	public static final boolean isOnline(Context context) 
	{
		if(context == null)
			return false;
		
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
		if(text.equals(""))
			return "";
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

	public static void chooseBetweenSeenAndCheckin(final WatchedEpisodesTask task, Context context)
	{
		List<Map<Integer, Boolean>> listWatched = task.getListWatched();

		int size = 0;
		int index = 0;
		int i = 0;

		for(Map<Integer, Boolean> map : listWatched)
		{
			size += map.size();
			if(size > 0)
				index = i;
			i++;
		}

		//if there is only one episode selected and user want to mark it as watched
		if(size == 1 && listWatched.get(index).containsValue(true))
		{
			final CharSequence[] items = {"I've watched it", "I'm watching it right now!"};

			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Pick a method");
			builder.setItems(items, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int item) 
				{
					TraktManager.getInstance().addToQueue(task.init(item == 1));
				}
			});
			builder.create().show();
		}
		//standard "seen" method
		else
			TraktManager.getInstance().addToQueue(task.init(false));
	}

	public static long getPSTTimestamp(long timestamp)
	{
		TimeZone tz = TimeZone.getTimeZone("GMT-08:00");
		int offsetFromUTC = tz.getOffset(timestamp);
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		c.setTimeInMillis(timestamp);
		c.add(Calendar.MILLISECOND, offsetFromUTC);
		return c.getTimeInMillis()/1000;
	}


	public static boolean isSameDay(Date d1, Date d2)
	{
		if(d1.getTime()/(100*60*60*24) == d2.getTime()/(100*60*60*24))
			return true;

		return false;
	}

	public static Bitmap roundBitmap(Bitmap bm)
	{
		if(bm == null)
			return null;

		Bitmap output = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 20;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bm, rect, rect, paint);
		
		return shadowBitmap(output);
	}

	private static Bitmap shadowBitmap(Bitmap bm)
	{
		if(bm == null)
			return null;

		BlurMaskFilter blurFilter = new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL);
		Paint shadowPaint = new Paint();
		shadowPaint.setMaskFilter(blurFilter);

		int[] offsetXY = new int[2];
		Bitmap shadowImage = bm.extractAlpha(shadowPaint, offsetXY);		
		Bitmap shadowImage32 = shadowImage.copy(Bitmap.Config.ARGB_8888, true);

		shadowImage.recycle();
		
		Canvas canvas = new Canvas(shadowImage32);
		canvas.drawBitmap(bm, -offsetXY[0], -offsetXY[1], null);
		
		return shadowImage32;
	}

	public static Bitmap borderBitmap(Bitmap bm, Context context)
	{
		if(bm == null)
			return null;
		
		int stroke = 5;
		
		Bitmap output = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paintStroke = new Paint();

		paintStroke.setStrokeWidth(stroke);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(context.getResources().getColor(R.color.list_divider_color));
		paintStroke.setAntiAlias(true);

		final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());

		canvas.drawBitmap(bm, 0, 0, null);
		canvas.drawRect(rect, paintStroke);

		return shadowBitmap(output);
	}
}
