package com.florianmski.tracktoid.ui.fragments.library;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.GridPosterAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBShowsTask;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.get.TraktItemsTask;
import com.florianmski.tracktoid.trakt.tasks.get.TraktItemsTask.TraktItemsListener;
import com.florianmski.tracktoid.trakt.tasks.get.UpdateShowsTask;
import com.florianmski.tracktoid.ui.activities.phone.ShowActivity;
import com.jakewharton.trakt.entities.TvShow;

public class PI_LibraryShowFragment extends PI_LibraryFragment<TvShow>
{
	public static PI_LibraryShowFragment newInstance(Bundle args)
	{
		PI_LibraryShowFragment f = new PI_LibraryShowFragment();
		f.setArguments(args);
		return f;
	}

	public PI_LibraryShowFragment() {}

	@Override
	public void checkUpdateTask() 
	{
		//TODO
	}

	@Override
	public GridPosterAdapter<TvShow> setupAdapter() 
	{
		return new GridPosterAdapter<TvShow>(getActivity(), new ArrayList<TvShow>(), refreshGridView(), lcm);
	}

	@Override
	public void displayContent() 
	{
		if(getDBWrapper().isThereShows())
		{
			new DBShowsTask(getActivity(), new DBAdapter() 
			{
				@Override
				public void onDBShows(List<TvShow> shows)
				{
					adapter.refreshItems(shows);
					getStatusView().hide().text(null);
				}
			}).fire();
		}
	}

	@Override
	public void onGridItemClick(AdapterView<?> arg0, View v, int position, long arg3) 
	{
		Bundle b = new Bundle();
		b.putSerializable(TraktoidConstants.BUNDLE_TRAKT_ITEM, adapter.getItem(position));
		launchActivity(ShowActivity.class, b);
	}

	@Override
	public void onRefreshClick() 
	{
		new TraktItemsTask<TvShow>(getActivity(), new TraktItemsListener<TvShow>() 
		{
			@Override
			public void onTraktItems(List<TvShow> shows) 
			{
				createShowsDialog(shows);
			}
		}, tm.userService().libraryShowsAll(TraktManager.getUsername()), true).fire();
	}

	public void createShowsDialog(final List<TvShow> shows)
	{
		final ArrayList<TvShow> selectedShows = new ArrayList<TvShow>();

		String[] items = new String[shows.size()];

		for(int i = 0; i < shows.size(); i++)
			items[i] = shows.get(i).title;

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Which show(s) do you want to refresh ?");
		builder.setMultiChoiceItems(items, null, new OnMultiChoiceClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) 
			{
				if(isChecked)
					selectedShows.add(shows.get(which));
				else
					selectedShows.remove(shows.get(which));
			}
		});

		builder.setPositiveButton("Go!", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if(selectedShows.size() > 0)
					new UpdateShowsTask(getActivity(), selectedShows).fire();
				else
					Toast.makeText(getActivity(), "Nothing selected...", Toast.LENGTH_SHORT).show();
			}
		});

		builder.setNeutralButton("All", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				new UpdateShowsTask(getActivity(), shows).fire();
			}
		});

		builder.setNegativeButton("Cancel", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});

		AlertDialog alert = builder.create();

		//avoid trying to show dialog if activity no longer exist
		if(!getActivity().isFinishing())
			alert.show();
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
	
//	@Override
//	public void onTrakItemUpdated(TvShow traktItem) 
//	{
//		Log.e("coucou","coucou tvshow");
//	}
}
