package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.florianmski.tracktoid.R;

public class RoundView extends View
{
    private final static float STROKE_SIZE = 1;
    private final static float HALF_STROKE_SIZE = STROKE_SIZE / 2;

    private Paint fillPaint;
    private Paint strokePaint;

    private RectF fillRect;
    private RectF strokeRect;

    public RoundView(Context context)
    {
        this(context, null);
    }

    public RoundView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RoundView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundView, 0, 0);
        int color = a.getColor(R.styleable.RoundView_android_color, Color.RED);
        a.recycle();

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(color);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.parseColor("#33000000"));
        strokePaint.setStrokeWidth(STROKE_SIZE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(fillRect == null)
            fillRect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());

        if(strokeRect == null)
            strokeRect = new RectF(HALF_STROKE_SIZE, HALF_STROKE_SIZE, canvas.getWidth() - HALF_STROKE_SIZE, canvas.getHeight() - HALF_STROKE_SIZE);

        canvas.drawOval(fillRect, fillPaint);
        canvas.drawOval(strokeRect, strokePaint);
    }
}
