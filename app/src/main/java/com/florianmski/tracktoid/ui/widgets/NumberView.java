package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class NumberView extends View
{
    private Paint circlePaint;
    private Paint textPaint;
    private Rect textBounds;
    private Bitmap bitmapOut;
    private Canvas tempCanvas;

    private int color = Color.RED;
    private int number = 0;

    public NumberView(Context context)
    {
        this(context, null);
    }

    public NumberView(Context context, AttributeSet attrs)
    {
        this(context, null, 0);
    }

    public NumberView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(color);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        textBounds = new Rect();
    }

    private void initBitmap(int width, int height)
    {
        bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(bitmapOut);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(tempCanvas == null)
            initBitmap(canvas.getWidth(), canvas.getHeight());

        tempCanvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2, circlePaint);
        String text = String.valueOf(number);
        textPaint.setTextSize(getHeight() / 2);
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        tempCanvas.drawText(text, tempCanvas.getWidth()/2, tempCanvas.getHeight()/2 - textBounds.exactCenterY(), textPaint);

        canvas.drawBitmap(bitmapOut, 0, 0, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        initBitmap(w, h);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    public void setColor(int color)
    {
        this.color = color;
        circlePaint.setColor(color);
        invalidate();
    }

    public void setNumber(int number)
    {
        this.number = number;
        invalidate();
    }
}
