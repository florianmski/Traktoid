package com.florianmski.tracktoid.ui.activities.phone;

import android.R;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class BaseActivity extends SherlockFragmentActivity
{
	public void setPrincipalFragment(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment, null).commit();
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
	            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
