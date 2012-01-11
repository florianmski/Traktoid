package com.florianmski.tracktoid.ui.fragments;

import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.florianmski.tracktoid.adapters.lists.ListShoutsAdapter;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask;
import com.florianmski.tracktoid.trakt.tasks.get.ShoutsGetTask.ShoutsListener;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.Shout;
import com.jakewharton.trakt.entities.TvShowEpisode;

public class ShoutsFragment extends TraktFragment
{
	private final static int SPOILER = 0;
	private final static int NO_SPOILER = 1;

	private String tvdbId;
	private TvShowEpisode episode;

	private ListView lvShouts;
	private EditText edtShout;
	private Button btnSend;

	private QuickAction qa;

	private ListShoutsAdapter adapter;

	public ShoutsFragment() {}

	public ShoutsFragment(FragmentListener listener) 
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

		tvdbId = getActivity().getIntent().getStringExtra("tvdbId");
		episode = (TvShowEpisode)getActivity().getIntent().getExtras().get("episode");

		createGetShoutsTask();
		commonTask.execute();

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
							qa.show(btnSend);
						}
					}, 50);
				}
			}
		});

		qa = new QuickAction(getActivity(), QuickAction.HORIZONTAL);
		qa.addActionItem(new ActionItem(SPOILER, "My shout contain\n a spoiler!"));
		qa.addActionItem(new ActionItem(NO_SPOILER, "Stay calm! \nNo spoiler..."));

		qa.setOnActionItemClickListener(new OnActionItemClickListener() 
		{
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) 
			{
				TraktApiBuilder<Response> builder;
				
				if(episode == null)
					builder = tm.shoutService().show(Integer.valueOf(tvdbId)).shout(edtShout.getText().toString().trim()).spoiler(actionId == SPOILER);
				else
					builder = tm.shoutService().episode(Integer.valueOf(tvdbId)).season(episode.season).episode(episode.number).shout(edtShout.getText().toString().trim()).spoiler(actionId == SPOILER);
				
				new PostTask(tm, ShoutsFragment.this, builder, new PostListener() 
				{
					@Override
					public void onComplete(Response r, boolean success) 
					{
						if(success)
						{
							adapter.clear();
							createGetShoutsTask();

							//post the task 3sec later to let trakt the time to save the shout
							new Handler().postDelayed(new Runnable() 
							{
								@Override
								public void run() 
								{
									commonTask.execute();
								}
							}, 3000);
						}
					}
				}).execute();
			}
		});


	}

	private void createGetShoutsTask()
	{
		getStatusView().show().text("Loading shouts,\nPlease wait...");

		commonTask = new ShoutsGetTask(tm, this, episode, tvdbId, new ShoutsListener() 
		{
			@Override
			public void onShouts(List<Shout> shouts) 
			{
				if(adapter == null)
				{
					adapter = new ListShoutsAdapter(shouts, getActivity());
					lvShouts.setAdapter(adapter);
				}
				else
					adapter.reload(shouts);
				
				if(adapter.isEmpty())
					getStatusView().hide().text("No shouts :(\nBe the first! Come on!");
				else
					getStatusView().hide().text(null);

				edtShout.setText(null);
			}
		});
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
