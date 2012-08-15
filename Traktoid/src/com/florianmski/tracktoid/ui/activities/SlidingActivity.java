package com.florianmski.tracktoid.ui.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.BaseFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivityBase;
import com.slidingmenu.lib.app.SlidingActivityHelper;

public class SlidingActivity extends TraktActivity implements SlidingActivityBase 
{
	private SlidingActivityHelper mHelper;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
	}
	
	public void init(BaseFragment f)
	{
		setContentView(R.layout.frame);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, f).commit();
		setBehindContentView(R.layout.slide);

		this.setSlidingActionBarEnabled(true);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.actionbar_home_width);
		getSlidingMenu().setBehindScrollScale(0.25f);
	}

	public void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	public View findViewById(int id) 
	{
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	public void setContentView(int id) 
	{
		setContentView(getLayoutInflater().inflate(id, null));
	}

	public void setContentView(View v) 
	{
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void setContentView(View v, LayoutParams params) 
	{
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	public void setBehindContentView(int id) 
	{
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	public void setBehindContentView(View v) 
	{
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void setBehindContentView(View v, LayoutParams params) 
	{
		mHelper.setBehindContentView(v, params);
	}

	public SlidingMenu getSlidingMenu() 
	{
		return mHelper.getSlidingMenu();
	}

	public void toggle() 
	{
		mHelper.toggle();
	}

	public void showAbove() 
	{
		mHelper.showAbove();
	}

	public void showBehind() 
	{
		mHelper.showBehind();
	}

	public void setSlidingActionBarEnabled(boolean b) 
	{
		mHelper.setSlidingActionBarEnabled(b);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		boolean b = mHelper.onKeyDown(keyCode, event);
		if (b) return b;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
