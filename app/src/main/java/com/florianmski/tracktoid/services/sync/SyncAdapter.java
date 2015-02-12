package com.florianmski.tracktoid.services.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.florianmski.tracktoid.BuildConfig;
import com.florianmski.tracktoid.TraktoidPrefs;
import com.florianmski.tracktoid.data.provider.TraktoidProvider;
import com.florianmski.tracktoid.services.TraktoidService;
import com.florianmski.tracktoid.ui.fragments.login.LoginFragment;

public class SyncAdapter extends AbstractThreadedSyncAdapter
{
    public final static String ACCOUNT_NAME = "Traktoid Sync";

    public SyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs)
    {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        TraktoidService.start(getContext());
    }

    public static void requestImmediateSync()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        Account account = new Account(ACCOUNT_NAME, BuildConfig.APPLICATION_ID);
        ContentResolver.requestSync(account, TraktoidProvider.AUTHORITY, bundle);
    }
}
