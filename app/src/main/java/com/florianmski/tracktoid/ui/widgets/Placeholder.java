package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.utils.Utils;

import java.util.Random;

public class Placeholder
{
    private final static Random r = new Random();

    private int color;
    private int colorDark;

    public Placeholder(int color, int colorDark)
    {
        this.color = color;
        this.colorDark = colorDark;
    }

    public Placeholder(Context context, TraktoidTheme theme)
    {
        this(theme.getColor(context), theme.getColorDark(context));
    }

    public ColorDrawable getDrawable()
    {
        return new ColorDrawable(Utils.interpolateColor(color, colorDark, r.nextFloat()));
    }
}
