package com.florianmski.tracktoid.ui.activities.phone;

import org.zeroxlab.widget.AnimationLayout;

import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.R;

public class MenuActivity  extends TraktActivity implements AnimationLayout.Listener
{
	protected AnimationLayout mLayout;

	public AnimationLayout getAnimationLayout()
	{
		if(mLayout == null)
		{
			mLayout = (AnimationLayout) findViewById(R.id.animation_layout);
			mLayout.setListener(this);
		}
		
		return mLayout;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case android.R.id.home:
			mLayout.toggleSidebar(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() 
	{
		if (mLayout.isOpening())
			mLayout.closeSidebar(true);
		else
			finish();
	}

	@Override
	public void onSidebarOpened() 
	{

	}

	@Override
	public void onSidebarClosed() 
	{

	}

	@Override
	public boolean onContentTouchedWhenOpening() 
	{
		mLayout.closeSidebar(true);
		return true;
	}
}
