package com.florianmski.tracktoid.ui.fragments;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.adapters.lists.ListSearchAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.SearchTask;
import com.florianmski.tracktoid.trakt.tasks.get.SearchTask.SearchListener;
import com.florianmski.tracktoid.ui.activities.TraktItemsActivity;
import com.florianmski.tracktoid.ui.fragments.BaseFragment.TaskListener;

public class SearchFragment extends TraktFragment implements TaskListener
{
	public final static int 
	SHOWS = 0, 
	MOVIES = 1;
	//	EPISODES = 2, 
	//	USERS = 3, 
	//	PEOPLES = 4;

	private List<?> items;

	private ListView lvSearch;
	private EditText edtSearch;

	private int searchType = SHOWS;
	private OnMenuItemClickListener searchListener = new OnMenuItemClickListener() 
	{
		@Override
		public boolean onMenuItemClick(MenuItem item) 
		{
			searchType = item.getOrder();
			getSherlockActivity().invalidateOptionsMenu();
			launchTask();
			return false;
		}
	};

	public static SearchFragment newInstance(Bundle args)
	{
		SearchFragment f = new SearchFragment();
		f.setArguments(args);
		return f;
	}

	public SearchFragment() {}

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

		lvSearch.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				Bundle b = new Bundle();
				b.putSerializable(TraktoidConstants.BUNDLE_RESULTS, (Serializable) ((RootAdapter<?>) (lvSearch.getAdapter())).getItems());
				b.putInt(TraktoidConstants.BUNDLE_POSITION, position);
				launchActivity(TraktItemsActivity.class, b);
			}
		});

		edtSearch.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if (actionId == EditorInfo.IME_ACTION_DONE) 
				{
					InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					launchTask();
					return true;	
				}
				return false;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_search, null);

		lvSearch = (ListView)v.findViewById(android.R.id.list);
		edtSearch = (EditText)v.findViewById(R.id.editTextSearch);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		SubMenu searchMenu = menu.addSubMenu(0, R.id.action_bar_search, 0, "Filter");
		searchMenu.add(Menu.NONE, Menu.NONE, 0, "Shows").setOnMenuItemClickListener(searchListener);
		searchMenu.add(Menu.NONE, Menu.NONE, 1, "Movies").setOnMenuItemClickListener(searchListener);
		//		searchMenu.add(Menu.NONE, Menu.NONE, 2, "Episodes").setOnMenuItemClickListener(searchListener);
		//		searchMenu.add(Menu.NONE, Menu.NONE, 3, "Users").setOnMenuItemClickListener(searchListener);
		//		searchMenu.add(Menu.NONE, Menu.NONE, 4, "Peoples").setOnMenuItemClickListener(searchListener);

		MenuItem rateItem = searchMenu.getItem();
		rateItem.setTitle(searchMenu.getItem(searchType).getTitle())
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) 
	//	{
	//		switch(item.getItemId())
	//		{
	//		case R.id.action_bar_search:
	//
	//			return true;
	//		}
	//		return super.onOptionsItemSelected(item);
	//	}

	public void launchTask()
	{
		String query = edtSearch.getText().toString().trim();
		if(!query.equals(""))
		{
			task = new SearchTask(getActivity(), searchType, query, new SearchListener() 
			{
				@Override
				public void onSearch(List<?> results) 
				{
					SearchFragment.this.items = results;
					setAdapter();
				}
			});

			if(lvSearch.getAdapter() != null)
				((RootAdapter<?>)lvSearch.getAdapter()).clear();
			getStatusView().show().text("Searching for \"" + query + "\",\nPlease wait...");
			task.fire();
		}
	}

	public void setAdapter()
	{
		if(items != null)
		{
			lvSearch.setAdapter(ListSearchAdapter.createAdapter(getSherlockActivity(), items, searchType));
			if(items.isEmpty())
				getStatusView().hide().text("Nothing found");
			else
				getStatusView().hide().text(null);
		}
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}

	@Override
	public void onCreateTask() {}

	@Override
	public void onTaskIsDone() 
	{
		setAdapter();
	}

	@Override
	public void onTaskIsRunning() {}
}
