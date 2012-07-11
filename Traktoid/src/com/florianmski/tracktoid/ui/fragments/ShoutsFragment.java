package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListShoutsAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask.ShoutsListener;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Shout;

public class ShoutsFragment<T extends TraktoidInterface<T>> extends TraktFragment
{
	private final static int SPOILER = 0;
	private final static int NO_SPOILER = 1;

	private T traktItem;

	private ListView lvShouts;
	private EditText edtShout;
	private Button btnSend;
	private ListShoutsAdapter adapter;
	
	private ArrayList<Shout> shouts;

	public static ShoutsFragment newInstance(Bundle args)
	{
		ShoutsFragment f = new ShoutsFragment();
		f.setArguments(args);
		return f;
	}

	public ShoutsFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		traktItem = (T)getArguments().get(TraktoidConstants.BUNDLE_TRAKT_ITEM);

		setTitle("Shouts : " + traktItem.getTitle());

		if(savedInstanceState != null)
			setAdapter();
		else
		{
			createGetShoutsTask().fire();
		}

		lvShouts.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				adapter.revealSpoiler(position);
			}
		});

		btnSend.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				//empty shout
				if(edtShout.getText().toString().trim().equals(""))
					Toast.makeText(getActivity(), "Empty shout!", Toast.LENGTH_LONG).show();
				else
				{
					//close keyboard if it is open
					//because it does weird things with popup position
					InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(edtShout.getWindowToken(), 0);

					btnSend.postDelayed(new Runnable() 
					{
						@Override
						public void run() 
						{
//							qa.show(btnSend);
						}
					}, 50);
				}
			}
		});

		//TODO better way to ask if spoiler or not
//		qa = new QuickAction(getActivity(), QuickAction.HORIZONTAL);
//		qa.addActionItem(new ActionItem(SPOILER, "My shout contain\n a spoiler!"));
//		qa.addActionItem(new ActionItem(NO_SPOILER, "Stay calm! \nNo spoiler..."));
//
//		qa.setOnActionItemClickListener(new OnActionItemClickListener() 
//		{
//			@Override
//			public void onItemClick(QuickAction source, int pos, int actionId) 
//			{
//				new ShoutsPostTask<T>(ShoutsFragment.this, traktItem, edtShout.getText().toString().trim(), actionId == SPOILER, new PostListener() 
//				{
//					@Override
//					public void onComplete(Response r, boolean success) 
//					{
//						if(success)
//						{
//							adapter.clear();
//
//							//post the task 3sec later to let trakt the time to save the shout
//							new Handler().postDelayed(new Runnable() 
//							{
//								@Override
//								public void run() 
//								{
//									createGetShoutsTask().fire();
//								}
//							}, 3000);
//						}
//					}
//				}).fire();
//			}
//		});


	}

	private ShoutsGetTask<T> createGetShoutsTask()
	{
		getStatusView().show().text("Loading shouts,\nPlease wait...");

		return new ShoutsGetTask<T>(getActivity(), traktItem, new ShoutsListener() 
		{
			@Override
			public void onShouts(List<Shout> shouts) 
			{
				ShoutsFragment.this.shouts = (ArrayList<Shout>) shouts;
				setAdapter();
			}
		});
	}
	
	private void setAdapter()
	{		
		if(adapter == null)
		{
			adapter = new ListShoutsAdapter(shouts, getActivity());
			lvShouts.setAdapter(adapter);
		}
		else
			adapter.refreshItems(shouts);

		if(adapter.isEmpty())
			getStatusView().hide().text("No shouts :(\nBe the first! Come on!");
		else
			getStatusView().hide().text(null);

		edtShout.setText(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_shouts, null);

		lvShouts = (ListView)v.findViewById(R.id.listViewShouts);
		edtShout = (EditText)v.findViewById(R.id.editTextShout);
		btnSend = (Button)v.findViewById(R.id.buttonSend);

		return v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState(Bundle savedInstanceState) 
	{
		shouts = (ArrayList<Shout>) savedInstanceState.get(TraktoidConstants.BUNDLE_RESULTS);
	}

	@Override
	public void onSaveState(Bundle toSave) 
	{
		toSave.putSerializable(TraktoidConstants.BUNDLE_RESULTS, shouts);
	}
}