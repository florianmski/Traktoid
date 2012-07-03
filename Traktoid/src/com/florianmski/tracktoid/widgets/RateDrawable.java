package com.florianmski.tracktoid.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.florianmski.tracktoid.widgets.RateDialog.RatingColor;
import com.jakewharton.trakt.enumerations.Rating;

public class RateDrawable extends Drawable
{
    private Paint trianglePaint;
    private Paint textPaint;
    
    private RatingColor r;
    private int size = 50;

    public RateDrawable(Rating r)
    {
    	this.r = RatingColor.fromValue(r);
    	int color = RatingColor.fromValue(r).color;
    	
    	float[] hsv = new float[3];
    	int darkerColor = color;
    	Color.colorToHSV(darkerColor, hsv);
    	hsv[2] *= 0.7f;
    	darkerColor = Color.HSVToColor(hsv);
    	
    	Shader s = new LinearGradient(size/2, size/2, 0, size, darkerColor, color, TileMode.MIRROR);
        trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        trianglePaint.setColor(color);
        trianglePaint.setShader(s);
        trianglePaint.setStyle(Style.FILL);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Align.CENTER);
        textPaint.setTextSize(size/2);
        textPaint.setStyle(Style.FILL);
        
//    	setBounds(0, 0, size, size);
    }

    @Override
    public void draw(Canvas canvas)
    {
        Path p = new Path();
        p.moveTo(size, 0);
        p.lineTo(0, 0);
        p.lineTo(size, size);
        p.close();
        canvas.drawPath(p, trianglePaint);
        
        canvas.rotate(45, size/2, size/2);
        canvas.drawText(r.r.toString(), size/2, size/3, textPaint);
    }

    @Override
    public void setAlpha(int alpha) 
    {
    	trianglePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) 
    {
    	trianglePaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() 
    {
        return PixelFormat.TRANSLUCENT;
    }
    
    @Override
    public int getIntrinsicWidth() 
    {
        return size;
    }

    @Override
    public int getIntrinsicHeight() 
    {
    	return size;
    }
}
