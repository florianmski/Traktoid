package com.florianmski.tracktoid.ui.fragments.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.phone.HomeActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.jakewharton.trakt.entities.Response;

public class SignInFragment extends TraktFragment
{
	//TODO when click on "ok" on the keyboard, send request
	
	private EditText edtUsername;
	private EditText edtPassword;
	private Button btnGo;

	public static SignInFragment newInstance(Bundle args)
	{
		SignInFragment f = new SignInFragment();
		f.setArguments(args);
		return f;
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

		btnGo.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final String username = edtUsername.getText().toString().trim();
				final String password = Utils.SHA1(edtPassword.getText().toString().trim());

				if(username.equals(""))
					edtUsername.setError("Is your username a ninja?");
				else if(password.equals(""))
					edtPassword.setError("Is your password a ninja?");
				else
				{
					tm.setAuthentication(username, password);

					new PostTask(SignInFragment.this, tm.accountService().test(), new PostListener() 
					{	
						@Override
						public void onComplete(Response r, boolean success) 
						{
							if(success)
							{
								SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
								prefs.edit()
								.putString(TraktoidConstants.PREF_USERNAME, username)
								.putString(TraktoidConstants.PREF_PASSWORD, password)
								.putBoolean(TraktoidConstants.PREF_SHA1, true)
								.commit();
								
								Toast.makeText(getActivity(), "Welcome home " + username + "!", Toast.LENGTH_LONG).show();
								startActivity(new Intent(getActivity(), HomeActivity.class));
								getActivity().finish();
							}
							else
							{
								Toast.makeText(getActivity(), "Authentication failed!", Toast.LENGTH_LONG).show();
								if(r != null && r.error != null)
								{
									if(r.error.toUpperCase().contains("USERNAME"))
										edtUsername.setError(r.error);
									else if(r.error.toUpperCase().contains("PASSWORD"))
										edtPassword.setError(r.error);
								}
							}
						}
					}).fire();
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_signin, null);

		edtUsername = (EditText)v.findViewById(R.id.editTextUsername);
		edtPassword = (EditText)v.findViewById(R.id.editTextPassword);
		btnGo = (Button)v.findViewById(R.id.buttonGo);

		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
