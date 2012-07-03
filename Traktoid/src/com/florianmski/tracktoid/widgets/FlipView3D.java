package com.florianmski.tracktoid.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class FlipView3D extends FrameLayout
{
	public final static int FRONT = 0, BACK = 1;
	private final static int ROTATE_TO_RIGHT = 1, ROTATE_TO_LEFT = -1;
	
	private SwapListener listener;
	private boolean flipped = false;
	private int direction = ROTATE_TO_LEFT;
//	private View frontView, backView;
	
	public FlipView3D(Context context) 
	{
		super(context);
	}
	
	public FlipView3D(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}

	public FlipView3D(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
//		frontView = this.findViewById(R.id.front);
//		backView = this.findViewById(R.id.back);
	}

	public void setOnSwapListener(SwapListener listener)
	{
		this.listener = listener;
	}
	
	public boolean isFlipped()
	{
		return flipped;
	}
	
	public void rotate() 
	{
		// Find the center of the container
		final float centerX = this.getWidth() / 2.0f;
		final float centerY = this.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotation = new Rotate3dAnimation(flipped ? direction*180 : 0, direction*90, centerX, centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateDecelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView());

		this.startAnimation(rotation);
	}

	/**
	 * This class listens for the end of the first half of the animation.
	 * It then posts a new action that effectively swaps the views when the container
	 * is rotated 90 degrees and thus invisible.
	 */
	private final class DisplayNextView implements Animation.AnimationListener 
	{
		public void onAnimationStart(Animation animation) {}

		public void onAnimationEnd(Animation animation) 
		{
			FlipView3D.this.post(new SwapViews());
			flipped =! flipped;
		}

		public void onAnimationRepeat(Animation animation) {}
	}

	/**
	 * This class is responsible for swapping the views and start the second
	 * half of the animation.
	 */
	private final class SwapViews implements Runnable 
	{
		public void run() 
		{
			final float centerX = FlipView3D.this.getWidth() / 2.0f;
			final float centerY = FlipView3D.this.getHeight() / 2.0f;
			Rotate3dAnimation rotation;

			if(listener != null)
				listener.onSwap(flipped);

			rotation = new Rotate3dAnimation(direction*90, flipped ? direction*180 : 0, centerX, centerY, 310.0f, false);
			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());

			FlipView3D.this.startAnimation(rotation);
		}
	}
	
	public interface SwapListener
	{
		public void onSwap(boolean flipped);
	}

}
