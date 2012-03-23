package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;

import com.actionbarsherlock.view.Window;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.ShowFragment;
import com.florianmski.tracktoid.ui.fragments.pagers.items.ShowsLibraryFragment;

public class LibraryActivity extends TraktActivity
{
	public final static int FRAGMENT_REFRESH_DATA = 0;
	public final static int FRAGMENT_REFRESH_GRID_VIEW = 1;

	private ShowsLibraryFragment myShowsFragment;
	private ShowFragment myShowFragment;

	//TODO le plus simple est d'afficher le second fragment direct, ca évite toute ces merdes


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shows);

		if (savedInstanceState == null)
		{
			// myShowsFragment = new MyShowsFragment(this);
			// getSupportFragmentManager().beginTransaction().replace(R.id.fragment_left, myShowsFragment).commit();
			//
			// myShowFragment = new MyShowFragment(this);
			// FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_right, myShowFragment);
			// if(!Utils.isLandscape(this))
			// ft.hide(myShowFragment);
			// ft.commit();
		}
	}
}