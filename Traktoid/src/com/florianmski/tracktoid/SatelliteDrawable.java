package com.florianmski.tracktoid;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class SatelliteDrawable extends Drawable
{
	private final static int SIZE = 75;
	private final static int STROKE_WIDTH = 4;
	
	private int resId;
	private Resources res;
	private Paint circleBgPaint, circleStrokePaint;
	private Bitmap b;
	
	public SatelliteDrawable(int resId, Resources res)
	{
		this.resId = resId;
		this.res = res;
		
		init();
	}
	
	private void init()
	{
		circleBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleBgPaint.setColor(res.getColor(R.color.list_background_color));
		circleBgPaint.setStyle(Style.FILL);
		
		circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleStrokePaint.setColor(res.getColor(R.color.list_pressed_color));
		circleStrokePaint.setStyle(Style.STROKE);
		circleStrokePaint.setStrokeWidth(STROKE_WIDTH);
		
		b = BitmapFactory.decodeResource(res, resId);
		int height = b.getHeight() >= b.getWidth() ? SIZE/2 : (int) ((SIZE/2)*(b.getHeight()*1.0/b.getWidth()*1.0));
		int width = b.getWidth() >= b.getHeight() ? SIZE/2 : (int) ((SIZE/2)*(b.getWidth()*1.0/b.getHeight()*1.0));
		b = Bitmap.createScaledBitmap(b, width, height, false);
	}
	
	@Override
	public void draw(Canvas canvas) 
	{
		canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, circleBgPaint);
		canvas.drawCircle(SIZE/2, SIZE/2, (SIZE-STROKE_WIDTH)/2, circleStrokePaint);
		
		canvas.drawBitmap(b, (SIZE-b.getWidth())/2, (SIZE-b.getHeight())/2, null);
//		b.recycle();
	}

	@Override
    public int getOpacity() 
    {
        return PixelFormat.TRANSLUCENT;
    }
    
    @Override
    public int getIntrinsicWidth() 
    {
        return SIZE;
    }

    @Override
    public int getIntrinsicHeight() 
    {
    	return SIZE;
    }

	@Override
	public void setAlpha(int alpha) {}

	@Override
	public void setColorFilter(ColorFilter cf) {}

}
