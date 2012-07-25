package com.florianmski.tracktoid.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.lists.ListNavigationAdapter;
import com.florianmski.tracktoid.trakt.tasks.post.CheckinPostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.LibraryActivity;
import com.florianmski.tracktoid.ui.activities.MenuActivity;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Response;

public class SlideFragment extends TraktFragment
{
	private BadgesView<?> bvWatchingNow;
	//TODO checkin view
	private TextView tvEpisodeTitle;
	private TextView tvEpisodeEpisode;
	private ImageView ivScreen;

	private ListView lvNavigation;
	private ListNavigationAdapter adapter;
	
	@SuppressWarnings("rawtypes")
	private TraktoidInterface traktItem;

	public static SlideFragment newInstance(Bundle args)
	{
		SlideFragment f = new SlideFragment();
		f.setArguments(args);
		return f;
	}

	public SlideFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		lvNavigation.setAdapter(adapter = new ListNavigationAdapter(getActivity()));
		lvNavigation.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
			{
				Bundle b = new Bundle();
				b.putBoolean(TraktoidConstants.BUNDLE_SLIDE_OPEN, true);
				launchActivity(adapter.getItem(position).activityClass, b);
				getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				if(getActivity().getClass() != LibraryActivity.class)
					getActivity().finish();
				else
				{
					new Handler().postDelayed(new Runnable() 
					{
						@Override
						public void run() 
						{
							((MenuActivity)getActivity()).getAnimationLayout().closeSidebar(false);							
						}
					}, 1000);
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_slide, null);

		bvWatchingNow = (BadgesView<?>)v.findViewById(R.id.badgesLayoutWatchingNow);
		tvEpisodeTitle = (TextView)v.findViewById(R.id.textViewTitle);
		tvEpisodeEpisode = (TextView)v.findViewById(R.id.textViewEpisode);
		ivScreen = (ImageView)v.findViewById(R.id.imageViewScreen);
		
		lvNavigation = (ListView)v.findViewById(R.id.listViewNavigation);

		bvWatchingNow.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Cancel the checkin ?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
				{
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(DialogInterface dialog, int id) 
					{
						CheckinPostTask.createTask(getActivity(), traktItem, false, new PostListener() 
						{	
							@Override
							public void onComplete(Response r, boolean success) 
							{
								bvWatchingNow.setVisibility(View.INVISIBLE);
							}
						}).fire();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int id) 
					{
						dialog.cancel();
					}
				});
				builder.create().show();
			}
		});

		return v;
	}
	
	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
