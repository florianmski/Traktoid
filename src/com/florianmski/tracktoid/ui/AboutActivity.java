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

import java.io.IOException;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.florianmski.tracktoid.R;

public class AboutActivity extends FragmentActivity
{
	private QuickAction qa;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);
		
		qa = new QuickAction(AboutActivity.this);
		qa.addActionItem(new ActionItem(0, "Nyan!"));
		
		//prepare nyan cat animation
		final AnimationDrawable animation = new AnimationDrawable();
		for(int i = 0; i < 12; i++)
		{
			try 
			{
				animation.addFrame(Drawable.createFromStream(getAssets().open("Frame"+i+".png"), null), 75);
			} 
			catch (IOException e) {}
		}

		animation.setOneShot(false);

		ImageView ivNyan =  (ImageView) findViewById(R.id.imageViewNyan);
		ivNyan.setBackgroundDrawable(animation);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
		ivNyan.setLayoutParams(params);

		// run the start() method later on the UI thread
		ivNyan.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				animation.start();
			}
		});
		
		ivNyan.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				qa.show(v);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	        case android.R.id.home:
	            // app icon in Action Bar clicked; go home
	            Intent intent = new Intent(this, TraktoidActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
