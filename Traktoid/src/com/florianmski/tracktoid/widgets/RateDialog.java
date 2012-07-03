/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.widgets;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.trakt.enumerations.Rating;

public class RateDialog extends Dialog {

//	public final static int WEAK_SAUCE = 0xFF444444;
//	public final static int TERRIBLE = 0xFF494040;
//	public final static int BAD = 0xFF4E393A;
//	public final static int POOR = 0xFF553232;
//	public final static int MEH = 0xFF5D292A;
//	public final static int FAIR = 0xFF672221;
//	public final static int GOOD = 0xFF6E191A;
//	public final static int GREAT = 0xFF761212;
//	public final static int SUPERB = 0xFF7C0C0b;
//	public final static int TOTALLY_NINJA = 0xFF7F0707;
//
//	public final static int[] rate = {
//		0xFF444444, 
//		0xFF494040,
//		0xFF4E393A,
//		0xFF553232,
//		0xFF5D292A,
//		0xFF672221,
//		0xFF6E191A,
//		0xFF761212,
//		0xFF7C0C0b,
//		0xFF7F0707};
//
//	public final static String[] rateNames = {
//		"Weak sauce :(", 
//		"Terrible",
//		"Bad",
//		"Poor",
//		"Meh",
//		"Fair",
//		"Good",
//		"Great",
//		"Superb",
//	"Totally ninja!"};

	public interface OnColorChangedListener 
	{
		void rateChanged(Rating r);
	}

	private OnColorChangedListener mListener;
	private Rating r;

	private static class ColorPickerView extends View 
	{
		private Paint mPaint;
		private Paint mCenterPaint;
		private Paint mNumberPaint;
		private Paint mTextPaint;
		private final int[] mColors;
		private OnColorChangedListener mListener;
		private TextView tv;
		private RectF rect;
		private PorterDuffXfermode duffXMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

		ColorPickerView(Context c, OnColorChangedListener listener, RatingColor r, TextView tv) 
		{
			super(c);
			mListener = listener;
			this.tv = tv;
			mColors = new int[] {
					0xFF444444, 0xFF444444, 
					0xFF494040, 0xFF494040, 
					0xFF4E393A, 0xFF4E393A, 
					0xFF553232, 0xFF553232, 
					0xFF5D292A, 0xFF5D292A, 
					0xFF672221, 0xFF672221, 
					0xFF6E191A, 0xFF6E191A,
					0xFF761212, 0xFF761212,
					0xFF7C0C0b, 0xFF7C0C0b,
					0xFF7F0707, 0xFF7F0707
			};
			float[] mPositions = new float[] {
					0f, 0.1f, 
					0.1f, 0.2f, 
					0.2f, 0.3f, 
					0.3f, 0.4f, 
					0.4f, 0.5f, 
					0.5f, 0.6f, 
					0.6f, 0.7f, 
					0.7f, 0.8f, 
					0.8f, 0.9f, 
					0.9f, 1f
			};
			Shader s = new SweepGradient(0, 0, mColors, mPositions);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(s);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(CENTER_RADIUS/3);

			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mCenterPaint.setColor(r.color);
			mCenterPaint.setStrokeWidth(5);

			mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mNumberPaint.setTextAlign(Paint.Align.CENTER);
			mNumberPaint.setColor(Color.parseColor("#DD000000"));
			mNumberPaint.setTextSize(TEXT_SIZE);

			mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.setTextAlign(Paint.Align.CENTER);
			mTextPaint.setStyle(Paint.Style.STROKE);
			mTextPaint.setColor(Color.WHITE);
			mTextPaint.setStrokeWidth(5);
			mTextPaint.setTextSize(TEXT_SIZE/2);
			
			float l = CENTER_X - mPaint.getStrokeWidth()*0.5f;
			rect = new RectF(-l, -l, l, l);
		}

		private boolean mTrackingCenter;
		private boolean mHighlightCenter;

		@Override
		protected void onDraw(Canvas canvas) {
			int yPos = (int) ((canvas.getHeight() / 2) - ((mNumberPaint.descent() - mNumberPaint.ascent()) / 2)) ;
			int c = mCenterPaint.getColor();
			RatingColor r = RatingColor.fromValue(c);

			canvas.translate(CENTER_X, CENTER_X);

			canvas.drawOval(rect, mPaint);
			canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
			mNumberPaint.setXfermode(duffXMode);
			canvas.drawText(String.valueOf(r.r.toString()), 0, yPos, mNumberPaint);
			mNumberPaint.setXfermode(null);
			//need to redraw
			canvas.drawText(String.valueOf(r.r.toString()), 0, yPos, mNumberPaint);

			if(!r.name.equals(tv.getText().toString()))
			{
				tv.startAnimation(AnimationUtils.makeInAnimation(getContext(), true));
				tv.setText(r.name);
			}

			if (mTrackingCenter) {
				mCenterPaint.setStyle(Paint.Style.STROKE);

				if (mHighlightCenter) {
					mCenterPaint.setAlpha(0xFF);
				} else {
					mCenterPaint.setAlpha(0x80);
				}
				canvas.drawCircle(0, 0,
						CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
						mCenterPaint);

				mCenterPaint.setStyle(Paint.Style.FILL);
				mCenterPaint.setColor(c);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
		}

		private static final int SIZE = 300;
		private static final int CENTER_X = SIZE;
		private static final int CENTER_Y = CENTER_X;
		private static final int TEXT_SIZE = CENTER_X;
		private static final int CENTER_RADIUS = (int) (CENTER_X*2f/3);

		private static final float PI = 3.1415926f;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX() - CENTER_X;
			float y = event.getY() - CENTER_Y;
			boolean inCenter = FloatMath.sqrt(x*x + y*y) <= CENTER_RADIUS;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTrackingCenter = inCenter;
				if (inCenter) {
					mHighlightCenter = true;
					invalidate();
					break;
				}
			case MotionEvent.ACTION_MOVE:
				if (mTrackingCenter) {
					if (mHighlightCenter != inCenter) {
						mHighlightCenter = inCenter;
						invalidate();
					}
				} else {
					float angle = (float)java.lang.Math.atan2(y, x);
					// need to turn angle [-PI ... PI] into unit [0....1]
					float unit = angle/(2*PI);
					if (unit < 0) {
						unit += 1;
					}
					mCenterPaint.setColor(mColors[(int) (unit*2*10)]);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTrackingCenter) {
					if (inCenter) {
						mListener.rateChanged(RatingColor.fromValue(mCenterPaint.getColor()).r);
					}
					mTrackingCenter = false;    // so we draw w/o halo
					invalidate();
				}
				break;
			}
			return true;
		}
	}
	
	private LinearLayout layout;

	public RateDialog(Context context,	OnColorChangedListener listener, Rating r) 
	{
		super(context, android.R.style.Theme_Black_NoTitleBar);

		this.mListener = listener;
		this.r = r;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener() 
		{
			public void rateChanged(Rating r) 
			{
				mListener.rateChanged(r);
				Animation a = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
				a.setAnimationListener(new AnimationListener() 
				{
					@Override
					public void onAnimationStart(Animation animation) {}
					
					@Override
					public void onAnimationRepeat(Animation animation) {}
					
					@Override
					public void onAnimationEnd(Animation animation) 
					{
						dismiss();
					}
				});
				layout.startAnimation(a);
			}
		};

		layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(android.view.Gravity.CENTER); 

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(10, 0, 10, 5);

		TextView tv = new TextView(getContext());
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setTextSize(50);

		ColorPickerView mColorPickerView = new ColorPickerView(getContext(), l, RatingColor.fromValue(r), tv);        
		layout.addView(mColorPickerView, layoutParams);
		layout.addView(tv, layoutParams);

		getWindow().setBackgroundDrawable(new ColorDrawable(0xDD000000));
		layout.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
		setContentView(layout);
	}
	
	public enum RatingColor
	{
		WeakSauce("Weak sauce :(", Rating.WeakSauce, 0xFF444444),
	    Terrible("Terrible", Rating.Terrible, 0xFF494040),
	    Bad("Bad", Rating.Bad, 0xFF4E393A),
	    Poor("Poor", Rating.Poor, 0xFF553232),
	    Meh("Meh", Rating.Meh, 0xFF5D292A),
	    Fair("Fair", Rating.Fair, 0xFF672221),
	    Good("Good", Rating.Good, 0xFF6E191A),
	    Great("Great", Rating.Great, 0xFF761212),
	    Superb("Superb", Rating.Superb, 0xFF7C0C0b),
	    TotallyNinja("Totally ninja!", Rating.TotallyNinja, 0xFF7F0707);

	    public final String name;
	    public final Rating r;
	    public final int color;

	    private RatingColor(String name, Rating r, int color) 
	    {
	        this.name = name;
	        this.r = r;
	        this.color = color;
	    }

	    @Override
	    public String toString() 
	    {
	        return this.name;
	    }

	    private static final Map<Rating, RatingColor> RATING_MAPPING = new HashMap<Rating, RatingColor>();
	    private static final SparseArray<RatingColor> COLOR_MAPPING = new SparseArray<RatingColor>();

	    static 
	    {
	        for (RatingColor via : RatingColor.values())
	        	RATING_MAPPING.put(via.r, via);
	        for (RatingColor via : RatingColor.values())
	        	COLOR_MAPPING.put(via.color, via);
	    }

	    public static RatingColor fromValue(Rating value) 
	    {
	    	RatingColor res = RATING_MAPPING.get(value);
	    	if(res != null)
	    		return res;
	    	else if(value == Rating.Love)
	    		return RatingColor.TotallyNinja;
	    	else if(value == Rating.Hate)
	    		return RatingColor.WeakSauce;
	    	else
	    		return RatingColor.Meh;
	    }
	    
	    public static RatingColor fromValue(int color) 
	    {
	    	RatingColor res = COLOR_MAPPING.get(color);
	        return res == null ? RatingColor.Meh : res;
	    }
	}
}
