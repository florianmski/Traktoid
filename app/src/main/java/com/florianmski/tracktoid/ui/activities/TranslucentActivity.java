package com.florianmski.tracktoid.ui.activities;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.utils.Utils;
import com.florianmski.tracktoid.ui.widgets.DrawInsetsFrameLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class TranslucentActivity extends BaseActivity implements DrawInsetsFrameLayout.OnInsetsCallback
{
    private Rect insets = null;
    private List<DrawInsetsFrameLayout.OnInsetsCallback> insetsCallbacks = new ArrayList<DrawInsetsFrameLayout.OnInsetsCallback>();
    protected DrawInsetsFrameLayout difl;

    protected abstract int getContentViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addOnInsetsCallback(this);
    }

    @Override
    protected View getContentView()
    {
        final View v = LayoutInflater.from(this).inflate(getContentViewId(), null);

        // if we can, do the translucent thing, if we don't, do nothing
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            difl = new DrawInsetsFrameLayout(this);
            difl.setInsetStatusBarBackground(new ColorDrawable(Utils.getColorFromAttribute(this, R.attr.colorPrimaryDark)));
            difl.setInsetNavBarBackground(new ColorDrawable(getResources().getColor(R.color.black_transparent)));
            difl.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback()
            {
                @Override
                public void onInsetsChanged(Rect insets)
                {
                    if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
                    {
                        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                        p.setMargins(p.leftMargin, insets.top, p.rightMargin, p.bottomMargin);
                        v.requestLayout();
                    }

                    for(DrawInsetsFrameLayout.OnInsetsCallback callback : insetsCallbacks)
                        callback.onInsetsChanged(insets);
                }
            });

            FrameLayout fl = new FrameLayout(this);
            fl.addView(v);
            fl.addView(difl);

            return fl;
        }

        return v;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        removeOnInsetsCallback(this);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
    }

    public void addOnInsetsCallback(DrawInsetsFrameLayout.OnInsetsCallback callback)
    {
        insetsCallbacks.add(callback);
        if(insets != null)
            callback.onInsetsChanged(insets);
    }

    public void removeOnInsetsCallback(DrawInsetsFrameLayout.OnInsetsCallback callback)
    {
        insetsCallbacks.remove(callback);
    }

    @Override
    public void onInsetsChanged(Rect insets)
    {
        this.insets = insets;
        // expand the toolbar behind the status bar
        //        View toolbar = findViewById(R.id.toolbar);
        //        if(toolbar != null)
        //            toolbar.setPadding(insets.left, insets.top, insets.right, toolbar.getPaddingBottom());
    }

    @Override
    public void setTheme(TraktoidTheme theme)
    {
        super.setTheme(theme);

        // set statusbar color
        difl.setInsetStatusBarBackground(new ColorDrawable(theme.getColorDark(this)));
    }
}
