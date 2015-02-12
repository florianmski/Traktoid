package com.florianmski.tracktoid;

import android.content.Context;

public enum TraktoidTheme
{
    DEFAULT(R.color.primary, R.color.primaryDark),
    SHOW(R.color.primaryShow, R.color.primaryDarkShow),
    MOVIE(R.color.primaryMovie, R.color.primaryDarkMovie);

    private int colorRes;
    private int colorDarkRes;

    private TraktoidTheme(int colorRes, int colorDarkRes)
    {
        this.colorRes = colorRes;
        this.colorDarkRes = colorDarkRes;
    }

    private int getColor(Context context, int colorRes)
    {
        return context.getResources().getColor(colorRes);
    }

    public int getColor(Context context)
    {
        return getColor(context, colorRes);
    }

    public int getColorDark(Context context)
    {
        return getColor(context, colorDarkRes);
    }
}
