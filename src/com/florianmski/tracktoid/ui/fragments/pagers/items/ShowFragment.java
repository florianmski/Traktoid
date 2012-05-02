package com.florianmski.tracktoid.ui.fragments.pagers.items;

import android.os.Bundle;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.jakewharton.trakt.entities.TvShow;

public class ShowFragment extends PagerItemTraktFragment<TvShow>
{
	private boolean existsInDb = false;

	public static ShowFragment newInstance(Bundle args)
	{
		ShowFragment f = new ShowFragment();
		f.setArguments(args);
		return f;
	}

	public static ShowFragment newInstance(TvShow s)
	{
		Bundle args = new Bundle();
		args.putSerializable(TraktoidConstants.BUNDLE_TVSHOW, s);

		return newInstance(args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		item = (TvShow) (getArguments() != null ? getArguments().getSerializable(TraktoidConstants.BUNDLE_TVSHOW) : null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		DatabaseWrapper dbw = getDBWrapper();
		existsInDb = dbw.showExist(item.tvdbId);
		getSherlockActivity().invalidateOptionsMenu();
	}
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//	{
//		super.onCreateOptionsMenu(menu, inflater);
//		
//		//check if user has already this show in his lib. If so hide the "add" button
//		if(!existsInDb)
//		{
//			menu.add(0, R.id.action_bar_add, 0, "Add")
//			.setIcon(R.drawable.ab_icon_add)
//			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		}
//
//		menu.add(0, R.id.action_bar_shouts, 0, "Shouts")
//		.setIcon(R.drawable.ab_icon_shouts)
//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) 
//	{
//		switch(item.getItemId())
//		{
//		case R.id.action_bar_add :
//			ArrayList<TvShow> shows = new ArrayList<TvShow>();
//			shows.add(this.item);
//			tm.addToQueue(new UpdateShowsTask(tm, this, shows));
//			return true;
//		case R.id.action_bar_shouts :
//			Intent i = new Intent(getActivity(), ShoutsActivity.class);
//			i.putExtra(TraktoidConstants.BUNDLE_TVDB_ID, this.item.tvdbId);
//			i.putExtra(TraktoidConstants.BUNDLE_TITLE, this.item.title);
//			startActivity(i);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
