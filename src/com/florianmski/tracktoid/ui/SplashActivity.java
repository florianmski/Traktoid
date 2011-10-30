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

package com.florianmski.tracktoid.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.florianmski.tracktoid.R;

//currently not used
public class SplashActivity extends Activity
{		
	private static final int STOPSPLASH = 0;
	//time in milliseconds
	private long SPLASHTIME = 2500;

	//handler for splash screen
	private Handler splashHandler = new Handler() 
	{
		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case STOPSPLASH:
					//remove SplashScreen from view
					finish();
					Intent intent = new Intent(SplashActivity.this, TraktoidActivity.class);
					startActivity(intent);
					break;
			}
			super.handleMessage(msg);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) 
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_splashscreen);
		
		TextView tvVersion = (TextView)findViewById(R.id.textViewVersion);
		ImageView ivLogo = (ImageView)findViewById(R.id.imageViewLogo);
		
		try 
		{
			tvVersion.setText("v " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} 
		catch (NameNotFoundException e) {}
		
		ivLogo.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		
		Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed(msg, SPLASHTIME);
	}

	@Override
	public void onDestroy()
	{
		splashHandler.removeMessages(STOPSPLASH);
		super.onDestroy();
	}

}
