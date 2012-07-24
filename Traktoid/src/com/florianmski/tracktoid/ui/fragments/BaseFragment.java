package com.florianmski.tracktoid.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.florianmski.tracktoid.StatusView;
import com.florianmski.tracktoid.TraktBus;
import com.florianmski.tracktoid.db.DatabaseWrapper;
import com.florianmski.tracktoid.trakt.tasks.BaseTask;

public abstract class BaseFragment extends SherlockFragment
{
	private StatusView sv;
	private boolean restoreStateCalled = false;
	private DatabaseWrapper dbw = null;
	protected BaseTask<?> task;
	private TaskListener taskListener;

	public void launchActivity(Class<?> activityToLaunch, Bundle args)
	{
		Intent i = new Intent(getActivity(), activityToLaunch);
		if(args != null)
			i.putExtras(args);
		startActivity(i);
	}

	public void launchActivity(Class<?> activityToLaunch)
	{
		launchActivity(activityToLaunch, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getActionBar().setHomeButtonEnabled(true);

		TraktBus.getInstance().register(this);
		
		if(savedInstanceState != null)
		{
			onRestoreState(savedInstanceState);
			restoreStateCalled = true;
		}

		if(taskListener != null)
			setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		//in case the fragment use setRetainInstance(true)
		if(savedInstanceState != null && !restoreStateCalled)
		{
			onRestoreState(savedInstanceState);
			restoreStateCalled = true;
		}

		if(taskListener != null)
		{
			if(task == null)
			{
				//create and launch task
				taskListener.onCreateTask();
			}
			else
			{
				if(task.isDone())
				{
					//task is finish, do something
					taskListener.onTaskIsDone();
				}
				else
				{
					//task is running
					task.attach(getActivity());
					taskListener.onTaskIsRunning();
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle toSave)
	{
		super.onSaveInstanceState(toSave);
		onSaveState(toSave);
	}

	@Override
	public void onViewCreated(View v, Bundle savedInstanceState)
	{
		super.onViewCreated(v, savedInstanceState);

		sv = StatusView.instantiate(v);
	}

	public StatusView getStatusView()
	{
		return sv;
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();

		TraktBus.getInstance().unregister(this);
		
		if (dbw != null) 
		{
			dbw.close();
			dbw = null;
		}

		if(task != null)
		{
			task.detach();
			task.cancel();
		}
	}

	protected DatabaseWrapper getDBWrapper() 
	{
		if (dbw == null)
			dbw = new DatabaseWrapper(getActivity());
		return dbw;
	}

	public ActionBar getActionBar()
	{
		return getSherlockActivity().getSupportActionBar();
	}

	protected void setTitle(String title)
	{
		getActionBar().setTitle(title);
	}

	protected void setSubtitle(String subtitle)
	{
		getActionBar().setSubtitle(subtitle);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	public abstract void onRestoreState(Bundle savedInstanceState);
	public abstract void onSaveState(Bundle toSave);

	public void setTaskListener(TaskListener listener)
	{
		this.taskListener = listener;
	}

	public interface TaskListener
	{
		public void onCreateTask();
		public void onTaskIsDone();
		public void onTaskIsRunning();
	}
}
