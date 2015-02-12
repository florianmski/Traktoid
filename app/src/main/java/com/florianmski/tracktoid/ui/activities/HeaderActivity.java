package com.florianmski.tracktoid.ui.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.florianmski.tracktoid.utils.ColorFilterHelper;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.utils.Utils;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemScrollFragment;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;
import com.squareup.picasso.Picasso;

public abstract class HeaderActivity extends TranslucentActivity
{
    protected TraktImageView tivFanart;
    private ColorDrawable actionBarBackground;
    private final ColorFilterHelper cfh = new ColorFilterHelper();

    @Override
    protected int getContentViewId()
    {
        return R.layout.activity_header;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle activityBundle = getIntent().getExtras();
        String header = activityBundle.getString(TraktoidConstants.BUNDLE_HEADER);

        tivFanart = (TraktImageView) findViewById(R.id.tivFanart);
        int color = Utils.getColorFromAttribute(this, R.attr.colorPrimary);

        Picasso.with(this)
                .load(header)
                .placeholder(new ColorDrawable(color))
                .into(tivFanart);

    }

    public void setGroupView(final ItemScrollFragment fragment, final int deltaPoster)
    {
        tivFanart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                tivFanart.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int paddingTop = tivFanart.getHeight() - deltaPoster;
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                // if fanart take more than 1/2 of the screen, put the groupView on the fanart halfway
                if(tivFanart.getHeight() > (metrics.heightPixels)/2)
                    paddingTop -= (tivFanart.getHeight()/2);

                ViewGroup viewGroup = fragment.getGroupView();

                fragment.setGroupViewPadding(viewGroup.getPaddingLeft(),
                        paddingTop,
                        viewGroup.getPaddingRight(),
                        viewGroup.getPaddingBottom());
            }
        });
    }

    public void onScrollChanged(ViewGroup viewGroup, int t)
    {
        int height = viewGroup.getPaddingTop();

        // 0 when not translated
        // 1 when scrollView hit the top
        float percentTranslated = Utils.linearConversion(t, 0, height, 0, 1);
        // translate fanart
        tivFanart.setTranslationY(-t * 0.5f);
        // update fanart saturation
        cfh.update(tivFanart, 1 - percentTranslated);
        setActionBarAppearance(percentTranslated);
    }

    private void setActionBarAppearance(float percentTranslated)
    {
        int alpha = (int) (percentTranslated * 255);
        // make action bar bg and text visible
        actionBarBackground.setAlpha(alpha);
        int color = Color.argb(alpha, 255, 255, 255);
        getToolbar().setTitleTextColor(color);
        getToolbar().setSubtitleTextColor(color);
    }

    @Override
    public void setTheme(TraktoidTheme theme)
    {
        super.setTheme(theme);

        // default actionbar appearance without scrolling
        actionBarBackground = new ColorDrawable(theme.getColor(this));
        getSupportActionBar().setBackgroundDrawable(actionBarBackground);
        setActionBarAppearance(0);
    }
}
