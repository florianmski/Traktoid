package com.florianmski.tracktoid.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListShoutsAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask.ShoutsListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.trakt.tasks.post.ShoutsPostTask;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.Shout;

public class ShoutsFragment<T extends TraktoidInterface<T>> extends TraktFragment
{
	private T traktItem;

	private ListView lvShouts;
	private EditText edtShout;
	private ImageView btnSend;
	private ToggleButton tbSpoiler;
	private ListShoutsAdapter adapter;

	private ArrayList<Shout> shouts;

	public static ShoutsFragment<?> newInstance(Bundle args)
	{
		@SuppressWarnings("rawtypes")
		ShoutsFragment<?> f = new ShoutsFragment();
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

		setTitle(traktItem.getTitle());

		if(savedInstanceState != null)
			setAdapter();
		else
			createGetShoutsTask(true).fire();

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
					new ShoutsPostTask<T>(getSherlockActivity(), traktItem, edtShout.getText().toString().trim(), tbSpoiler.isChecked(), new PostListener() 
					{
						@Override
						public void onComplete(Response r, boolean success) 
						{
							if(success)
							{
								//post the task 3sec later to let trakt the time to save the shout
								new Handler().postDelayed(new Runnable() 
								{
									@Override
									public void run() 
									{
										createGetShoutsTask(adapter.getCount() == 0).fire();
									}
								}, 3000);
							}
						}
					}).fire();
				}
			}
		});
	}

	private ShoutsGetTask<T> createGetShoutsTask(boolean displayLoading)
	{
		if(displayLoading)
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
		btnSend = (ImageView)v.findViewById(R.id.buttonSend);
		tbSpoiler = (ToggleButton)v.findViewById(R.id.toggleButtonSpoiler);

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
