package com.florianmski.tracktoid.widgets.coverflow;

import java.util.List;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.florianmski.tracktoid.image.TraktImage;
import com.jakewharton.trakt.entities.TvShow;

/**
 * This class is an adapter that provides base, abstract class for images
 * adapter.
 * 
 */
public class CoverFlowImageAdapter extends BaseAdapter 
{
	//TODO extends RootAdapter
	//TODO placeholder
	
	/** The height. */
	private float height = 0;

	/** The bitmap map. */

	private List<TvShow> shows;

	/**
	 * Gap between the image and its reflection.
	 */
	private float reflectionGap;

	/** The image reflection ratio. */
	private float imageReflectionRatio;

	/**
	 * Sets the width ratio.
	 * 
	 * @param imageReflectionRatio
	 *            the new width ratio
	 */
	public void setWidthRatio(final float imageReflectionRatio) 
	{
		this.imageReflectionRatio = imageReflectionRatio;
	}

	/**
	 * Sets the reflection gap.
	 * 
	 * @param reflectionGap
	 *            the new reflection gap
	 */
	public void setReflectionGap(final float reflectionGap) 
	{
		this.reflectionGap = reflectionGap;
	}

	public CoverFlowImageAdapter(List<TvShow> shows) 
	{
		this.shows = shows;
	}

	/**
	 * Set height for all pictures.
	 * 
	 * @param height
	 *            picture height
	 */
	public synchronized void setHeight(final float height) 
	{
		this.height = height;
	}

	@Override
	public int getCount() 
	{
		return shows.size();
	}
	
	@Override
	public final Bitmap getItem(final int position) 
	{
		return null;
	}

	@Override
	public final synchronized long getItemId(final int position) 
	{
		return position;
	}

	@Override
	public final synchronized ImageView getView(final int position, final View convertView, final ViewGroup parent) 
	{
		ImageView imageView;
		if (convertView == null) 
		{
			final Context context = parent.getContext();
			imageView = new ImageView(context);
			imageView.setLayoutParams(new CoverFlow.LayoutParams((int) (height*1.0/TraktImage.RATIO_POSTER), (int) height));
		} 
		else 
			imageView = (ImageView) convertView;

		TvShow show = shows.get(position);

		AQuery aq = new AQuery(imageView);

		BitmapAjaxCallback cb = new BitmapAjaxCallback()
		{
			@Override
			public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status)
			{
				if(bm != null)
					iv.setImageBitmap(createReflectedImages(bm));
			}
		};

		TraktImage i = TraktImage.getPoster(show);
		cb.url(i.getUrl()).fileCache(false).memCache(true);
		aq.id(imageView).image(cb);

		return imageView;
	}

	public Bitmap createReflectedImages(final Bitmap originalImage) 
	{
		final int width = originalImage.getWidth();
		final int height = originalImage.getHeight();
		final Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		final Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, (int) (height * imageReflectionRatio),
				width, (int) (height - height * imageReflectionRatio), matrix, false);
		final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (int) (height + height * imageReflectionRatio),
				Config.ARGB_8888);
		final Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(originalImage, 0, 0, null);
		final Paint deafaultPaint = new Paint();
		deafaultPaint.setColor(color.transparent);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		final Paint paint = new Paint();
		final LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

}
