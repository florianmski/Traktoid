package com.florianmski.tracktoid.utils;

import android.annotation.TargetApi;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// from here http://www.curious-creature.org/2012/12/14/android-recipe-3-sliding-menu-layers-and-filters/
public class ColorFilterHelper
{
    private final boolean API_17 = Build.VERSION.SDK_INT >= 17;
    private final boolean API_16 = Build.VERSION.SDK_INT == 16;

    private ColorMatrix matrix = new ColorMatrix();
    private ColorMatrix matrixScale = new ColorMatrix();
    private ColorMatrix matrixSaturation = new ColorMatrix();
    private Paint paint = new Paint();

    public ColorFilterHelper() {}

    public void update(View v, float percent)
    {
        if (API_16)
            prepareLayerHack();

        manageLayers(v, percent);
        updateColorFilter(percent);
        updatePaint(v);

    }

    @TargetApi(17)
    private void updatePaint(View v)
    {
        if (API_17)
            v.setLayerPaint(paint);
        else
        {
            if (API_16)
            {
                if (sHackAvailable)
                {
                    try
                    {
                        sRecreateDisplayList.setBoolean(v, true);
                        sGetDisplayList.invoke(v, (Object[]) null);
                    }
                    catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) {}
                }
                else
                {
                    // This solution is slow
                    v.invalidate();
                }
            }

            // API level < 16 doesn't need the hack above, but the invalidate is required
            ((View) v.getParent()).postInvalidate(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        }
    }

    private void updateColorFilter(float percent)
    {
        float dark = 0.4f;
        float scale = (percent * (1 - dark)) + dark;
        matrixScale.setScale(scale, scale, scale, 1f);
        matrixSaturation.setSaturation(percent);
        matrix.setConcat(matrixScale, matrixSaturation);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);
    }

    private void manageLayers(View v, float percent)
    {
        boolean layer = percent >= 0.0f && percent < 1.0f;
        int layerType = layer ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;

        v.setLayerType(layerType, Build.VERSION.SDK_INT <= 16 ? paint : null);
    }

    private static boolean sHackReady;
    private static boolean sHackAvailable;
    private static Field sRecreateDisplayList;
    private static Method sGetDisplayList;

    private static void prepareLayerHack()
    {
        if (!sHackReady)
        {
            try
            {
                sRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
                sRecreateDisplayList.setAccessible(true);

                sGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class<?>) null);
                sGetDisplayList.setAccessible(true);

                sHackAvailable = true;
            }
            catch (NoSuchFieldException | NoSuchMethodException ignored) {}
            sHackReady = true;
        }
    }
}
