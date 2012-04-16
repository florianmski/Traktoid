package com.florianmski.tracktoid.ui.fragments.pagers.items;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.android.QuickAction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.GridPosterAdapter;
import com.florianmski.tracktoid.adapters.GridShowPosterAdapter;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBShowsTask;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.trakt.tasks.RemoveShowTask;
import com.florianmski.tracktoid.trakt.tasks.TraktTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShowsTask.ShowsListener;
import com.florianmski.tracktoid.trakt.tasks.get.UpdateShowsTask;
import com.florianmski.tracktoid.trakt.tasks.post.RateTask;
import com.florianmski.tracktoid.ui.activities.phone.MyShowActivity;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

public class ShowsLibraryFragment extends PagerItemLibraryFragment
{
	public static ShowsLibraryFragment newInstance(Bundle args)
	{
		ShowsLibraryFragment f = new ShowsLibraryFragment();
		f.setArguments(args);
		return f;
	}

	public ShowsLibraryFragment() {}

	@Override
	public void checkUpdateTask() 
	{
		TraktTask updateTask = tm.getCurrentTask();
		if(updateTask != null && updateTask instanceof UpdateShowsTask)
			updateTask.reconnect(this);
	}

	@Override
	public GridPosterAdapter<TvShow> setupAdapter() 
	{
		return new GridShowPosterAdapter(getActivity(), new ArrayList<TvShow>(), refreshGridView());
	}

	@Override
	public void displayContent() 
	{
//		if(!getDBWrapper().isThereShows())
//			onRefreshClick();
//		else
		if(getDBWrapper().isThereShows())
		{
			new DBShowsTask(getActivity(), new DBAdapter() 
			{
				@Override
				public void onDBShows(List<TvShow> shows)
				{
					adapter.updateItems(shows);
					getStatusView().hide().text(null);
				}
			}).fire();
		}
	}

	@Override
	public Intent onGridItemClick(AdapterView<?> arg0, View v, int position, long arg3) 
	{
		Intent i = new Intent(getActivity(), MyShowActivity.class);
		i.putExtra(TraktoidConstants.BUNDLE_SHOW, (TvShow)adapter.getItem(position));
		getActivity().setIntent(i);

		return i;
	}

	@Override
	public void onRefreshQAClick(QuickAction source, int pos, int actionId) 
	{
		ArrayList<TvShow> showsSelected = new ArrayList<TvShow>();
		showsSelected.add((TvShow)adapter.getItem(posterClickedPosition));
		tm.addToQueue(new UpdateShowsTask(tm, ShowsLibraryFragment.this, showsSelected));
	}

	@Override
	public void onDeleteQAClick(QuickAction source, int pos, int actionId) 
	{
		tm.addToQueue(new RemoveShowTask(tm, ShowsLibraryFragment.this, (TvShow)adapter.getItem(posterClickedPosition)));
	}

	@Override
	public void onRateQAClick(QuickAction source, int pos, int actionId) 
	{
		final CharSequence[] items = {"Totally ninja!", "Week sauce :(", "Unrate"};
		final Rating[] ratings = {Rating.Love, Rating.Hate, Rating.Unrate};

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Rate");
		builder.setItems(items, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int item) 
			{
				tm.addToQueue(new RateTask(tm, ShowsLibraryFragment.this, (TvShow)adapter.getItem(posterClickedPosition), ratings[item]));
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onRefreshClick() 
	{
		tm.addToQueue(new ShowsTask(tm, this, new ShowsListener() 
		{
			@Override
			public void onShows(ArrayList<TvShow> shows) 
			{
				createShowsDialog(shows);
			}
		}, tm.userService().libraryShowsAll(TraktManager.getUsername()), true));
	}

	@Override
	public void onShowUpdated(TvShow show)
	{		
		if(adapter != null)
			adapter.updateItem(show);
	}

	@Override
	public void onShowRemoved(TvShow show)
	{
		if(adapter != null)
			adapter.removeItem(show);
	}

	public void createShowsDialog(final ArrayList<TvShow> shows)
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
					tm.addToQueue(new UpdateShowsTask(tm, ShowsLibraryFragment.this, selectedShows));
				else
					Toast.makeText(getActivity(), "Nothing selected...", Toast.LENGTH_SHORT).show();
			}
		});

		builder.setNeutralButton("All", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				tm.addToQueue(new UpdateShowsTask(tm, ShowsLibraryFragment.this, shows));
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
	public void onRestoreState(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		// TODO Auto-generated method stub
	}
}
