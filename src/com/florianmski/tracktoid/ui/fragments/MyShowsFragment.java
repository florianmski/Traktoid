package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.OnNavigationListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.GridPosterAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.db.tasks.DBAdapter;
import com.florianmski.tracktoid.db.tasks.DBShowsTask;
import com.florianmski.tracktoid.image.Image;
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

public class MyShowsFragment extends TraktFragment
{
	private final static int NB_COLUMNS_TABLET_PORTRAIT = 5;
	private final static int NB_COLUMNS_TABLET_LANDSCAPE = 7;
	private final static int NB_COLUMNS_PHONE_PORTRAIT = 3;
	private final static int NB_COLUMNS_PHONE_LANDSCAPE = 5;

	private GridView gd;
	private QuickAction quickAction;

	private int posterClickedPosition = -1;
	private boolean hasMyShowFragment;

	private GridPosterAdapter adapter;

	public static MyShowsFragment newInstance(Bundle args)
	{
		MyShowsFragment f = new MyShowsFragment();
		f.setArguments(args);
		return f;
	}
	
	public MyShowsFragment() {}

	public MyShowsFragment(FragmentListener listener) 
	{
		super(listener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		getStatusView().show().text("Loading shows,\nPlease wait...");
				
		TraktTask updateTask = tm.getCurrentTask();
		if(updateTask != null && updateTask instanceof UpdateShowsTask)
			updateTask.reconnect(this);
		
		if(savedInstanceState != null && savedInstanceState.containsKey(TraktoidConstants.BUNDLE_HAS_MY_SHOW_FRAGMENT))
			hasMyShowFragment = savedInstanceState.getBoolean(TraktoidConstants.BUNDLE_HAS_MY_SHOW_FRAGMENT);
		else
		{
			MyShowFragment myShowFragment = (MyShowFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_my_show);
			hasMyShowFragment = (myShowFragment != null) && (myShowFragment.isVisible());
		}

		getSupportActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] items = new String[] {"All shows", "Unwatched shows", "Loved shows"};
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.abs__simple_spinner_item, items);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		getSupportActivity().getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() 
		{
			@Override
			public boolean onNavigationItemSelected(int filter, long itemId) 
			{
				adapter.setFilter(filter);
				return false;
			}
		});

		DatabaseWrapper dbw = new DatabaseWrapper(getActivity());
		dbw.open();
		boolean isDBEmpty = dbw.isEmpty();
		dbw.close();		

		refreshGridView();

		adapter = new GridPosterAdapter(getActivity(), new ArrayList<TvShow>(), refreshGridView());
		gd.setAdapter(adapter);

		if(isDBEmpty)
		{
			if(!tm.isUpdateTaskRunning())
				tm.addToQueue(new ShowsTask(tm, this, new ShowsListener() 
				{
					@Override
					public void onShows(ArrayList<TvShow> shows) 
					{
						createShowsDialog(shows);						
					}
				}, tm.userService().libraryShowsAll(TraktManager.getUsername()), true));
		}
		else
			new DBShowsTask(getActivity(), new DBAdapter() 
			{
				@Override
				public void onDBShows(List<TvShow> shows)
				{
					adapter.updateShows(shows);
					getStatusView().hide().text(null);
				}
			}).execute();

		gd.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Intent i = new Intent(getActivity(), MyShowActivity.class);
				i.putExtra(TraktoidConstants.BUNDLE_SHOW, (TvShow)adapter.getItem(position));
				getActivity().setIntent(i);

				if(Utils.isLandscape(getActivity()))
				{
					if(hasMyShowFragment)
					{
						((MyShowFragment)(getSupportFragmentManager().findFragmentById(R.id.fragment_my_show))).refreshFragment(i.getExtras());
					}
					else
					{
						MyShowFragment msf = MyShowFragment.newInstance(i.getExtras());

						getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_my_show, msf)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();

						hasMyShowFragment = true;
						refreshGridView();
					}					
				}
				else
					startActivity(i);

			}
		});

		quickAction = new QuickAction(getActivity());

		ActionItem aiRefresh = new ActionItem();
		aiRefresh.setTitle("Refresh");
		aiRefresh.setIcon(getResources().getDrawable(R.drawable.ab_icon_refresh));

		ActionItem aiDelete = new ActionItem();
		aiDelete.setTitle("Delete");
		aiDelete.setIcon(getResources().getDrawable(R.drawable.ab_icon_delete));

		ActionItem aiRating = new ActionItem();
		aiRating.setTitle("Rate");
		aiRating.setIcon(getResources().getDrawable(R.drawable.ab_icon_rate));

		quickAction.addActionItem(aiRefresh);
		quickAction.addActionItem(aiDelete);
		//not necessary, disable it for the moment
//		quickAction.addActionItem(aiRating);

		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() 
		{			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) 
			{
				switch(pos)
				{
				case 0 :
					ArrayList<TvShow> showsSelected = new ArrayList<TvShow>();
					showsSelected.add((TvShow)adapter.getItem(posterClickedPosition));
					tm.addToQueue(new UpdateShowsTask(tm, MyShowsFragment.this, showsSelected));
					break;
				case 1 :
					tm.addToQueue(new RemoveShowTask(tm, MyShowsFragment.this, (TvShow)adapter.getItem(posterClickedPosition)));
					break;
				case 2:
					final CharSequence[] items = {"Totally ninja!", "Week sauce :(", "Unrate"};
					final Rating[] ratings = {Rating.Love, Rating.Hate, Rating.Unrate};

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("Rate");
					builder.setItems(items, new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int item) 
						{
							tm.addToQueue(new RateTask(tm, MyShowsFragment.this, (TvShow)adapter.getItem(posterClickedPosition), ratings[item]));
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					break;
				}
			}
		});

		gd.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long arg3) 
			{
				onShowQuickAction(v, position);
				return false;
			}

		});		
	}

	@Override
	public void onSaveInstanceState(Bundle toSave) 
	{
		super.onSaveInstanceState(toSave);

		toSave.putBoolean(TraktoidConstants.BUNDLE_HAS_MY_SHOW_FRAGMENT, hasMyShowFragment);
	}

	public TvShow getFirstShow()
	{
		if(adapter.isEmpty())
			return null;
		else
			return (TvShow) adapter.getItem(0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_my_shows, null);

		gd = (GridView)v.findViewById(R.id.gridViewShows);

		return v;
	}

	public int refreshGridView()
	{
		int coeffDivision;
		int nbColumns;

		if(hasMyShowFragment && Utils.isLandscape(getActivity()))
			coeffDivision = 2;
		else
			coeffDivision = 1;

		if(Utils.isTabletDevice(getActivity()))
		{
			if(!hasMyShowFragment && Utils.isLandscape(getActivity()))
				nbColumns = NB_COLUMNS_TABLET_LANDSCAPE;
			else
				nbColumns = NB_COLUMNS_TABLET_PORTRAIT;	
		}
		else
		{
			if(!hasMyShowFragment && Utils.isLandscape(getActivity()))
				nbColumns = NB_COLUMNS_PHONE_LANDSCAPE;
			else
				nbColumns = NB_COLUMNS_PHONE_PORTRAIT;	
		}

		gd.setNumColumns(nbColumns);

		if(adapter != null)
			adapter.setHeight(calculatePosterHeight(coeffDivision, nbColumns));

		return calculatePosterHeight(coeffDivision, nbColumns);
	}

	public void onShowQuickAction(View v, int position) 
	{
		//maybe add a setTag() function in quickAction to avoid this
		posterClickedPosition = position;
		quickAction.show(v);
	}

	private int calculatePosterHeight(int coeffDivision, int nbColumns)
	{
		int width = (getActivity().getWindowManager().getDefaultDisplay().getWidth()/(coeffDivision*nbColumns));
		return (int) (width*Image.RATIO_POSTER);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		if(!tm.isUpdateTaskRunning())
		{
			menu.add(0, R.id.action_bar_refresh, 0, "Refresh")
			.setIcon(R.drawable.ab_icon_refresh)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		else
		{
//			int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
			int value = getSupportActivity().getSupportActionBar().getHeight();
			ProgressBar pbRefresh = new ProgressBar(getActivity());
			pbRefresh.setIndeterminate(true);
			RelativeLayout rl = new RelativeLayout(getActivity());
			Log.e("test","value : "+value);
			rl.setLayoutParams(new LayoutParams(value, value));
			pbRefresh.setLayoutParams(new RelativeLayout.LayoutParams(value, value));
			rl.addView(pbRefresh);

			menu.add(0, R.id.action_bar_refresh, 0, "Refresh")
			.setActionView(rl)
			.setEnabled(false)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == R.id.action_bar_refresh) 
		{
			tm.addToQueue(new ShowsTask(tm, this, new ShowsListener() 
			{
				@Override
				public void onShows(ArrayList<TvShow> shows) 
				{
					createShowsDialog(shows);
				}
			}, tm.userService().libraryShowsAll(TraktManager.getUsername()), true));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBeforeTraktRequest()
	{
		getSupportActivity().invalidateOptionsMenu();
	}

	@Override
	public void onAfterTraktRequest(boolean success) 
	{
		getSupportActivity().invalidateOptionsMenu();
	}

	@Override
	public void onShowUpdated(TvShow show)
	{		
		if(adapter != null)
			adapter.updateShow(show);
	}


	@Override
	public void onShowRemoved(TvShow show)
	{
		if(adapter != null)
			adapter.removeShow(show);
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
					tm.addToQueue(new UpdateShowsTask(tm, MyShowsFragment.this, selectedShows));
				else
					Toast.makeText(getActivity(), "Nothing selected...", Toast.LENGTH_SHORT).show();
			}
		});

		builder.setNeutralButton("All", new OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				tm.addToQueue(new UpdateShowsTask(tm, MyShowsFragment.this, shows));
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
	public void onResume()
	{
		super.onResume();
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
