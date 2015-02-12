package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.ImagesTest;
import com.florianmski.tracktoid.image.Type;

public class TraktImageView extends ImageView
{
    private Type t;

    public TraktImageView(Context context)
    {
        super(context);
    }

    public TraktImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TraktImageView);
        String type = attributes.getString(R.styleable.TraktImageView_type);
        if(type != null)
        {
            t = Type.fromValue(type);
            setType(t);
        }

        attributes.recycle();

        // crop slightly the image but avoid having gap due to ratio imprecision
        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(t == null)
            setMeasuredDimension(width, height);
        else
        {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            if(widthMode == MeasureSpec.EXACTLY)
                setMeasuredDimension(width, (int) (width*getRatio()));
            else if(heightMode == MeasureSpec.EXACTLY)
                setMeasuredDimension((int) (height/getRatio()), height);
            else
                setMeasuredDimension(width, height);
        }
    }

    private double getRatio()
    {
        switch(t)
        {
            case BANNER:
                return ImagesTest.RATIO_BANNER;
            case FANART:
                return ImagesTest.RATIO_FANART;
            case POSTER:
                return ImagesTest.RATIO_POSTER;
            case SCREENSHOT:
                return ImagesTest.RATIO_SCREENSHOT;
            case HEADSHOT:
                return 1;
            default:
                return 0;
        }
    }

    public void setType(Type t)
    {
        this.t = t;
        requestLayout();
    }

    public Type getType()
    {
        return t;
    }
}