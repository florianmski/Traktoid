package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.ui.fragments.user.PagerUserFragment;

public class UserActivity extends TranslucentActivity
{
    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String userId = getIntent().getStringExtra(TraktoidConstants.BUNDLE_ID);
        int position = getIntent().getIntExtra(TraktoidConstants.BUNDLE_POSITION, 0);

        if(savedInstanceState == null)
        {
            PagerUserFragment f = PagerUserFragment.newInstance(userId, position);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_pager_user, f, null).commit();
        }
    }

    public static void launch(Activity a, String userId, int position)
    {
        Bundle b = new Bundle();
        b.putInt(TraktoidConstants.BUNDLE_POSITION, position);
        b.putString(TraktoidConstants.BUNDLE_ID, userId);
        launchActivity(a, UserActivity.class, b);
    }

    public static void launch(Activity a, String userId)
    {
        launch(a, userId, 0);
    }
}
