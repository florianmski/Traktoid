package com.florianmski.tracktoid.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class AlphaToggleButton extends ToggleButton
{
	private final static int ALPHA_ON = 255;
	private final static int ALPHA_OFF = ALPHA_ON/3;
	
	public AlphaToggleButton(Context context) 
	{
		super(context);
		init();
	}
	
	public AlphaToggleButton(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init();
	}

	public AlphaToggleButton(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init();
	}

	public void init()
	{
		setText(null);
		setTextOn(null);
		setTextOff(null);
		getBackground().setAlpha(ALPHA_OFF);
		setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				getBackground().setAlpha(isChecked ? ALPHA_ON : ALPHA_OFF);
			}
		});
	}

}
