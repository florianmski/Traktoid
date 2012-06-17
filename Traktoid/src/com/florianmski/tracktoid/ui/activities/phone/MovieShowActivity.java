package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;

public abstract class MovieShowActivity extends MenuActivity
{
	private static int SHOW = 0;
	private static int MOVIE = 1;

	private SherlockFragment showFragment = null;
	private SherlockFragment movieFragment = null;

	private int actualFragment = SHOW;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_fragment);

		if(savedInstanceState == null)
		{
			if(actualFragment == SHOW)
				setPrincipalFragment(showFragment = getShowFragment());
			else
				setPrincipalFragment(movieFragment = getMovieFragment());
		}
		
		getAnimationLayout();
	}

	@Override
	public void setPrincipalFragment(Fragment fragment)
	{
		getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{		
		menu.add(0, R.id.action_bar_movie_show, 0, actualFragment == SHOW ? "Movie" : "Tv show")
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if(item.getItemId() == R.id.action_bar_movie_show)
		{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();			
			if(actualFragment == SHOW)				
				ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
			else
				ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

			//			if(actualFragment == SHOW)
			//			{        		
			//				ft.hide(showFragment);        			
			//				if(movieFragment == null)
			//				{
			//					movieFragment = getMovieFragment();
			//					ft.add(R.id.content, movieFragment);
			//				}
			//				else
			//					ft.show(movieFragment);
			//				actualFragment = MOVIE;
			//			}
			//			else
			//			{        		
			//				ft.hide(movieFragment);     
			//				if(showFragment == null)
			//				{
			//					showFragment = getShowFragment();
			//					ft.add(R.id.content, showFragment);
			//				}
			//				else
			//					ft.show(showFragment);
			//				actualFragment = SHOW;
			//			}

			//			if(actualFragment == SHOW)
			//			{        		
			//				ft.detach(showFragment);        			
			//				if(movieFragment == null)
			//				{
			//					movieFragment = getMovieFragment();
			//					ft.add(R.id.content, movieFragment);
			//				}
			//				else
			//					ft.attach(movieFragment);
			//				actualFragment = MOVIE;
			//			}
			//			else
			//			{        		
			//				ft.detach(movieFragment);     
			//				if(showFragment == null)
			//				{
			//					showFragment = getShowFragment();
			//					ft.add(R.id.content, showFragment);
			//				}
			//				else
			//					ft.attach(showFragment);
			//				actualFragment = SHOW;
			//			}

			if(actualFragment == SHOW)
			{        		
				ft.remove(showFragment);        			
				if(movieFragment == null)
					movieFragment = getMovieFragment();

				ft.add(R.id.content, movieFragment);
				actualFragment = MOVIE;
			}
			else
			{        		
				ft.remove(movieFragment);     
				if(showFragment == null)
					showFragment = getShowFragment();

				ft.add(R.id.content, showFragment);
				actualFragment = SHOW;
			}

			ft.commit();
		}
		return true;
	}

	public abstract TraktFragment getMovieFragment();
	public abstract TraktFragment getShowFragment();
}
