package com.florianmski.tracktoid.ui.widgets;

import android.graphics.BlurMaskFilter;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

public class BlurSpan extends CharacterStyle implements UpdateAppearance
{
    private float radius;
    private BlurMaskFilter filter;

    public BlurSpan(float radius)
    {
        setRadius(radius);
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
        filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
    }

    public float getRadius()
    {
        return radius;
    }

    @Override
    public void updateDrawState(TextPaint tp)
    {
        tp.setMaskFilter(filter);
    }
}
