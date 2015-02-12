package com.florianmski.tracktoid.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class CrossFadeDrawable extends Drawable
{
    private final static float BEFORE = -1f, CURRENT = 0f, AFTER = 1f;

    private Drawable drawableCurrent, drawableNext;

    private float positionOffset = 0f;

    public CrossFadeDrawable()
    {
        drawableCurrent = new ColorDrawable(Color.GREEN);
        drawableNext = new ColorDrawable(Color.BLUE);
    }

    // -1 -> before
    // 0 -> current
    // 1 -> after
    public void setPositionOffset(float positionOffset)
    {
        this.positionOffset = positionOffset;
        invalidateSelf();
    }

    public void setBefore(Drawable drawableBefore)
    {
        //this.drawableBefore = drawableBefore;
    }

    public void setCurrent(Drawable drawableCurrent)
    {
        //this.drawableCurrent = drawableCurrent;
    }

    public void setAfter(Drawable drawableAfter)
    {
        //this.drawableAfter = drawableAfter;
    }

    @Override
    public void draw(Canvas canvas)
    {
        if(drawableCurrent == null)
            return;

        int alpha = (int) (Math.abs(positionOffset) * 255);
        boolean done = (alpha == 0 || alpha == 255);

        if(done)
        {
            if(positionOffset == CURRENT)
                drawableCurrent.draw(canvas);
            else
                drawableNext.draw(canvas);
            return;
        }

        drawableCurrent.draw(canvas);
        setDrawableAlpha(canvas, drawableNext, alpha);

        //canvas.drawColor(Color.GREEN);
    }

    private void setDrawableAlpha(Canvas canvas, Drawable d, int alpha)
    {
        if(d != null)
        {
            d.setAlpha(255 - alpha);
            d.draw(canvas);
            d.setAlpha(0xFF);
        }
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(ColorFilter cf) {}

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }

    //    @Override
//    public int getIntrinsicWidth()
//    {
//        return drawableCurrent == null ? 0 : drawableCurrent.getIntrinsicWidth();
//    }
//
//    @Override
//    public int getIntrinsicHeight()
//    {
//        return drawableCurrent == null ? 0 : drawableCurrent.getIntrinsicHeight();
//    }
}