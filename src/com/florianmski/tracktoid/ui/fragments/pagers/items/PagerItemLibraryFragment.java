package com.florianmski.tracktoid.ui.fragments.pagers.items;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.GridPosterAdapter;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.image.Image;
import com.florianmski.tracktoid.ui.fragments.ShowFragment;
import com.jakewharton.trakt.entities.TvShow;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public abstract class PagerItemLibraryFragment extends PagerItemFragment
{
	protected final static int NB_COLUMNS_TABLET_PORTRAIT = 5;
	protected final static int NB_COLUMNS_TABLET_LANDSCAPE = 7;
	protected final static int NB_COLUMNS_PHONE_PORTRAIT = 3;
	protected final static int NB_COLUMNS_PHONE_LANDSCAPE = 5;

	protected GridView gd;
	protected QuickAction quickAction;

	protected int posterClickedPosition = -1;
	protected boolean hasSecondFragment;

	protected GridPosterAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	public abstract void checkUpdateTask();
	public abstract GridPosterAdapter setupAdapter();
	public abstract Intent onGridItemClick(AdapterView<?> arg0, View v, int position, long arg3);
	public abstract void onDBEmpty();
	public abstract void onDBNotEmpty();
	public abstract void onRefreshQAClick(QuickAction source, int pos, int actionId);
	public abstract void onDeleteQAClick(QuickAction source, int pos, int actionId);
	public abstract void onRateQAClick(QuickAction source, int pos, int actionId);
	public abstract void onRefreshClick();

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		getStatusView().show().text("Loading ,\nPlease wait...");
				
		checkUpdateTask();
		
		if(savedInstanceState != null && savedInstanceState.containsKey(TraktoidConstants.BUNDLE_HAS_MY_SHOW_FRAGMENT))
			hasSecondFragment = savedInstanceState.getBoolean(TraktoidConstants.BUNDLE_HAS_MY_SHOW_FRAGMENT);
		else
		{
			ShowFragment myShowFragment = (ShowFragment)getFragmentManager().findFragmentById(R.id.fragment_my_show);
			hasSecondFragment = (myShowFragment != null) && (myShowFragment.isVisible());
		}

		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] items = new String[] {"All", "Unwatched", "Loved"};
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.sherlock_spinner_item, items);
		spinnerAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() 
		{
			@Override
			public boolean onNavigationItemSelected(int filter, long itemId) 
			{
				adapter.setFilter(filter);
				return false;
			}
		});

		DatabaseWrapper dbw = getDBWrapper();
		boolean isDBEmpty = dbw.isEmpty();

		refreshGridView();

		gd.setAdapter(adapter = setupAdapter());

		if(isDBEmpty)
			onDBEmpty();
		else
			onDBNotEmpty();

		gd.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				Intent i = onGridItemClick(arg0, arg1, position, arg3);

				if(Utils.isLandscape(getActivity()))
				{
					if(hasSecondFragment)
					{
						((ShowFragment)(getFragmentManager().findFragmentById(R.id.fragment_my_show))).refreshFragment(i.getExtras());
					}
					else
					{
						ShowFragment msf = ShowFragment.newInstance(i.getExtras());

						getFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_my_show, msf)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();

						hasSecondFragment = true;
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
					onRefreshQAClick(source, pos, actionId);
					break;
				case 1 :
					onDeleteQAClick(source, pos, actionId);
					break;
				case 2:
					onRateQAClick(source, pos, actionId);
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

		toSave.putBoolean(TraktoidConstants.BUNDLE_HAS_MY_SHOW_FRAGMENT, hasSecondFragment);
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
		View v = inflater.inflate(R.layout.pager_item_library, null);

		gd = (GridView)v.findViewById(R.id.gridViewLibrary);

		return v;
	}

	public int refreshGridView()
	{
		int coeffDivision;
		int nbColumns;

		if(hasSecondFragment && Utils.isLandscape(getActivity()))
			coeffDivision = 2;
		else
			coeffDivision = 1;

		if(Utils.isTabletDevice(getActivity()))
		{
			if(!hasSecondFragment && Utils.isLandscape(getActivity()))
				nbColumns = NB_COLUMNS_TABLET_LANDSCAPE;
			else
				nbColumns = NB_COLUMNS_TABLET_PORTRAIT;	
		}
		else
		{
			if(!hasSecondFragment && Utils.isLandscape(getActivity()))
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
			int value = getSherlockActivity().getSupportActionBar().getHeight();
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
			onRefreshClick();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBeforeTraktRequest()
	{
		getSherlockActivity().invalidateOptionsMenu();
	}

	@Override
	public void onAfterTraktRequest(boolean success) 
	{
		getSherlockActivity().invalidateOptionsMenu();
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
