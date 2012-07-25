package com.florianmski.tracktoid.ui.fragments.recommendations;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListRecommendationAdapter;
import com.florianmski.tracktoid.adapters.lists.ListRecommendationAdapter.DismissListener;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;
import com.florianmski.tracktoid.trakt.tasks.get.RecommendationsTask;
import com.florianmski.tracktoid.trakt.tasks.get.RecommendationsTask.RecommendationsListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.TraktItemsActivity;
import com.florianmski.tracktoid.ui.fragments.BaseFragment.TaskListener;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.DismissResponse;
import com.jakewharton.trakt.entities.Genre;
import com.jakewharton.trakt.entities.Response;

public abstract class RecommendationFragment<T extends TraktoidInterface<T>> extends TraktFragment implements TaskListener
{	
	protected final static int START_YEAR = 1919;
	protected final static int END_YEAR = 2019;

	protected ListView lvRecommendations;

	protected ListRecommendationAdapter<T> adapter;

	protected List<Genre> genres;
	protected List<T> items;
	protected Genre genre;
	protected int startYear = START_YEAR, endYear = END_YEAR;

	private OnMenuItemClickListener startYearListener = new OnMenuItemClickListener()
	{
		@Override
		public boolean onMenuItemClick(MenuItem item) 
		{
			startYear = Integer.valueOf(item.getTitle().toString());
			getSherlockActivity().invalidateOptionsMenu();
			return true;
		}
	};

	private OnMenuItemClickListener endYearListener = new OnMenuItemClickListener() 
	{
		@Override
		public boolean onMenuItemClick(MenuItem item) 
		{
			endYear = Integer.valueOf(item.getTitle().toString());
			getSherlockActivity().invalidateOptionsMenu();
			return true;
		}
	};

	public RecommendationFragment() {}

	public abstract TraktApiBuilder<DismissResponse> getDismissBuilder(String id);
	public abstract TraktApiBuilder<List<T>> getRecommendationBuilder(Genre genre);
	public abstract TraktApiBuilder<List<Genre>> getGenreBuilder();

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setTaskListener(this);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		lvRecommendations.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Bundle b = new Bundle();
				b.putSerializable(TraktoidConstants.BUNDLE_RESULTS, (ArrayList<T>) adapter.getItems());
				b.putInt(TraktoidConstants.BUNDLE_POSITION, position);
				launchActivity(TraktItemsActivity.class, b);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_recommendation, null);

		lvRecommendations = (ListView)v.findViewById(R.id.listViewRecommendation);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		if(genres != null)
		{
			SubMenu genresMenu = menu.addSubMenu(0, Menu.NONE, 0, genre == null ? "All genres" : genre.name);
			genresMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			for(final Genre g : genres)
			{
				genresMenu.add(0, Menu.NONE, 0, g.name).setOnMenuItemClickListener(new OnMenuItemClickListener() 
				{
					@Override
					public boolean onMenuItemClick(MenuItem item) 
					{
						genre = g;
						getSherlockActivity().invalidateOptionsMenu();
						return true;
					}
				});
			}

			menu.add(Menu.NONE, R.id.action_bar_send, 3, "Send")
			.setIcon(R.drawable.ab_icon_send)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		SubMenu startYearMenu = menu.addSubMenu(0, Menu.NONE, 1, "");
		startYearMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		SubMenu endYearMenu = menu.addSubMenu(0, Menu.NONE, 2, "");
		endYearMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		int size = END_YEAR - START_YEAR + 1;
		for(int i = 0; i < size; i++)
		{
			startYearMenu.add(0, Menu.NONE, 0, String.valueOf(START_YEAR+i)).setOnMenuItemClickListener(startYearListener);
			endYearMenu.add(0, Menu.NONE, 0, String.valueOf(END_YEAR-i)).setOnMenuItemClickListener(endYearListener);
		}

		startYearMenu.getItem().setTitle(String.valueOf(startYear));
		endYearMenu.getItem().setTitle(String.valueOf(endYear));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_bar_send:
			createGetRecommendationsTask(false, false).fire();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void setAdapter()
	{
		if(adapter == null)
			lvRecommendations.setAdapter(adapter = new ListRecommendationAdapter<T>(items, getActivity()));
		else
		{
			adapter.refreshItems(items);
			if(lvRecommendations.getAdapter() == null)
				lvRecommendations.setAdapter(adapter);
		}

		if(adapter.getCount() == 0)
			getStatusView().hide().text("No recommendations, strange...");
		else
			getStatusView().hide().text(null);

		adapter.setOnDismissListener(new DismissListener() 
		{
			@Override
			public void onDismiss(String id) 
			{
				new PostTask(getActivity(), getDismissBuilder(id), new PostListener() 
				{
					@Override
					public void onComplete(Response r, boolean success) 
					{
						createGetRecommendationsTask(false, false).fire();
					}
				}).fire();
			}
		});
	}

	protected BaseTask<?> createGetRecommendationsTask(boolean sendCachedContent, boolean silent)
	{
		String text = "Retrieving recommendations" + ((genre == null || genre.slug.equals("all-genres")) ? "" : " in \"" + genre.name + "\"") + ",\nPlease wait...";

		if(!silent)
		{
			// avoid to cover the list with text
			if(sendCachedContent)
				getStatusView().show().text(text);
			else
				Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
		}

		return task = new RecommendationsTask<T>(getActivity(),
				getGenreBuilder(), 
				getRecommendationBuilder(genre), 
				sendCachedContent,
				(genre == null),
				new RecommendationsListener<T>() 
				{
			@Override
			public void onRecommendations(List<Genre> genres, List<T> recommendations) 
			{
				RecommendationFragment.this.genres = genres;
				RecommendationFragment.this.items = recommendations;
				setAdapter();
				getSherlockActivity().invalidateOptionsMenu();
			}
				});
	}

	@Override
	public void onCreateTask() 
	{
		createGetRecommendationsTask(true, false);
		task.fire();
	}

	@Override
	public void onTaskIsDone() 
	{
		setAdapter();
	}

	@Override
	public void onTaskIsRunning() {}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
