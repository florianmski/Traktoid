package com.florianmski.tracktoid.ui.activities.phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class BaseActivity extends SherlockFragmentActivity
{
	public void setPrincipalFragment(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().replace(org.zeroxlab.demo.R.id.animation_layout_content, fragment, null).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
	        case android.R.id.home:
	            // app icon in Action Bar clicked; go home
	            Intent intent = new Intent(this, HomeActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void setTitle(String title)
	{
		getSupportActionBar().setTitle(title);
	}
	
	protected void setSubtitle(String subtitle)
	{
		getSupportActionBar().setSubtitle(subtitle);
	}
	
	public void launchActivity(Class<?> activityToLaunch, Bundle args)
	{
		Intent i = new Intent(this, activityToLaunch);
		if(args != null)
			i.putExtras(args);
		startActivity(i);
	}
	
	public void launchActivity(Class<?> activityToLaunch)
	{
		launchActivity(activityToLaunch, null);
	}
}
