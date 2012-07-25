package com.florianmski.tracktoid.ui.fragments.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.florianmski.tracktoid.ui.activities.StartActivity;
import com.florianmski.tracktoid.ui.fragments.TraktFragment;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.entities.Response;

public class LoginFragment extends TraktFragment
{	
	private EditText edtUsername;
	private EditText edtEmail;
	private EditText edtPassword;
	private Button btnGo;
	
	private boolean join;

	public static LoginFragment newInstance(Bundle args)
	{
		LoginFragment f = new LoginFragment();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		join = getArguments().getBoolean(TraktoidConstants.BUNDLE_JOIN);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		edtPassword.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
		        if (actionId == EditorInfo.IME_ACTION_DONE) 
		        {
		        	InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		            btnGo.performClick();
		            return true;	
		        }
		        return false;
		    }
		});
		
		btnGo.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final String username = edtUsername.getText().toString().trim();
				final String email = edtEmail.getText().toString().trim();
				final String password = Utils.SHA1(edtPassword.getText().toString().trim());

				if(username.equals(""))
					edtUsername.setError("really?");
				else if(join && email.equals(""))
					edtEmail.setError("really?");
				else if(password.equals(""))
					edtPassword.setError("really?");
				else if(username.length() < 3 || username.length() > 20)
					edtUsername.setError("Choose a username between 3 and 20 characters");
				else if(password.length() < 4)
					edtPassword.setError("Seriously? Come on! At least 4 characters please...");
				else
				{
					tm.setAuthentication(username, password);

					TraktApiBuilder<?> builder = join ? tm.accountService().create(username, password, email) : tm.accountService().test();
					
					new PostTask(getActivity(), builder, new PostListener() 
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
								getActivity().setResult(StartActivity.RESULT_LOGIN);
								getActivity().finish();
							}
							else
							{
								if(r != null && r.error != null)
								{
									if(r.error.toUpperCase().contains("USERNAME"))
										edtUsername.setError(r.error);
									else if(r.error.toUpperCase().contains("E-MAIL"))
										edtEmail.setError(r.error);
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
		View v = inflater.inflate(R.layout.fragment_join, null);

		edtUsername = (EditText)v.findViewById(R.id.editTextUsername);
		edtEmail = (EditText)v.findViewById(R.id.editTextEmail);
		edtPassword = (EditText)v.findViewById(R.id.editTextPassword);
		btnGo = (Button)v.findViewById(R.id.buttonGo);
		
		if(!join)
		{
			edtEmail.setVisibility(View.GONE);
			btnGo.setText("Sign In");
		}

		return v;
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {}

	@Override
	public void onSaveState(Bundle toSave) {}
}
