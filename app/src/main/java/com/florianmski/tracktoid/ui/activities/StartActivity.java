package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidPrefs;
import com.florianmski.tracktoid.AppMigration;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StartActivity extends Activity implements Observer<String>
{
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(AppMigration.INSTANCE.isMigrationNeeded())
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            AppMigration.INSTANCE.migrate(getApplicationContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this);
        }
        else
            onMigrationFinished();
    }

    private void onMigrationFinished()
    {
        if(TraktoidPrefs.INSTANCE.isUserLoggedIn() && TraktoidPrefs.INSTANCE.getUsername() != null)
            HomeActivity.launch(this);
        else
            LoginActivity.launch(this);

        finish();
    }

    @Override
    public void onCompleted()
    {
        progressDialog.dismiss();
        onMigrationFinished();
    }

    @Override
    public void onError(Throwable e)
    {
        // TODO
        e.printStackTrace();
    }

    @Override
    public void onNext(String s)
    {
        progressDialog.setMessage(s);
    }
}
