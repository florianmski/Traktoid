package com.florianmski.tracktoid.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.ui.fragments.season.PagerSeasonFragment;

public class PagerSeasonActivity extends TranslucentActivity
{
    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_pager_season;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String showId = getIntent().getStringExtra(TraktoidConstants.BUNDLE_SHOW_ID);
        String[] seasonIds = getIntent().getStringArrayExtra(TraktoidConstants.BUNDLE_IDS);
        int[] seasons = getIntent().getIntArrayExtra(TraktoidConstants.BUNDLE_SEASONS);
        int position = getIntent().getIntExtra(TraktoidConstants.BUNDLE_POSITION, 0);

        if(savedInstanceState == null)
        {
            PagerSeasonFragment f = PagerSeasonFragment.newInstance(showId, seasonIds, seasons, position);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_pager_season, f, null).commit();
        }
    }

    public static void launch(Activity a, CharSequence title, String showId, String[] seasonIds, int[] seasons, int position)
    {
        Bundle b = new Bundle();
        b.putString(TraktoidConstants.BUNDLE_TITLE, title.toString());
        b.putString(TraktoidConstants.BUNDLE_SHOW_ID, showId);
        b.putStringArray(TraktoidConstants.BUNDLE_IDS, seasonIds);
        b.putIntArray(TraktoidConstants.BUNDLE_SEASONS, seasons);
        b.putInt(TraktoidConstants.BUNDLE_POSITION, position);
        launchActivity(a, PagerSeasonActivity.class, b);
    }
}
