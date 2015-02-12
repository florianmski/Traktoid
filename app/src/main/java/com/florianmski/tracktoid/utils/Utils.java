package com.florianmski.tracktoid.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;

import com.florianmski.tracktoid.TraktoidPrefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

public class Utils
{
    // from http://stackoverflow.com/a/11946496
    public static String sparseArrayToString(SparseArray<?> sparseArray)
    {
        StringBuilder result = new StringBuilder();
        if (sparseArray == null)
            return "null";

        result.append('{');
        for (int i = 0; i < sparseArray.size(); i++)
        {
            result.append(sparseArray.keyAt(i));
            result.append(" => ");
            if (sparseArray.valueAt(i) == null)
                result.append("null");
            else
                result.append(sparseArray.valueAt(i).toString());

            if (i < sparseArray.size() - 1)
                result.append(", ");
        }
        result.append('}');
        return result.toString();
    }

    public static long getPSTTimestamp()
    {
        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(timeZone);
        return calendar.getTimeInMillis() / 1000;
    }

    public static String getSeasonEpisodeString(int season, int episode)
    {
        return String.format("S%02dE%02d", season, episode);
    }

    public static String readInputStream(InputStream in) throws IOException
    {
        StringBuffer stream = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; )
            stream.append(new String(b, 0, n));

        return stream.toString();
    }

    // http://stackoverflow.com/questions/929103/convert-a-number-range-to-another-range-maintaining-ratio
    public static float linearConversion(float oldValue, float oldMin, float oldMax, float newMin, float newMax)
    {
        // check that oldValue is between oldMin and oldMax
        if (oldValue > oldMax)
            oldValue = oldMax;
        else if (oldValue < oldMin)
            oldValue = oldMin;

        float oldRange = (oldMax - oldMin);
        float NewRange = (newMax - newMin);
        return (((oldValue - oldMin) * NewRange) / oldRange) + newMin;
    }

    public static int booleanToInt(Boolean bool)
    {
        return (bool != null && bool) ? 1 : 0;
    }

    public static int getActionBarHeight(Context context)
    {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.actionBarSize});
        int height = styledAttributes.getDimensionPixelSize(0, 0);
        styledAttributes.recycle();
        return height;
    }

    // borrowed there https://github.com/astuetz/PagerSlidingTabStrip/blob/master/sample/src/com/astuetz/viewpager/extensions/sample/MainActivity.java
    // change color background of a view with a nice transition
    public static TransitionDrawable generateTransitionDrawable(Drawable oldBackground, int newColor)
    {
        // nothing to do
        if (oldBackground != null && oldBackground instanceof ColorDrawable && ((ColorDrawable) oldBackground).getColor() == newColor)
            return null;

        Drawable colorDrawable = new ColorDrawable(newColor);

        if (oldBackground == null)
            oldBackground = new ColorDrawable(Color.TRANSPARENT);

        return new TransitionDrawable(new Drawable[]{oldBackground, colorDrawable});
    }

    public static void changeColor(Drawable oldBackground, int newColor, final View v)
    {
        final Handler handler = new Handler();
        Drawable.Callback drawableCallback = new Drawable.Callback()
        {
            @Override
            public void invalidateDrawable(Drawable who)
            {
                v.setBackgroundDrawable(who);
            }

            @Override
            public void scheduleDrawable(Drawable who, Runnable what, long when)
            {
                handler.postAtTime(what, when);
            }

            @Override
            public void unscheduleDrawable(Drawable who, Runnable what)
            {
                handler.removeCallbacks(what);
            }
        };

        TransitionDrawable td = generateTransitionDrawable(oldBackground, newColor);
        if (td == null)
            return;

        // workaround for broken ActionBarContainer drawable handling on
        // pre-API 17 builds
        // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            td.setCallback(drawableCallback);
        else
            v.setBackgroundDrawable(td);

        td.startTransition(300);
    }

    public static int getColorFromAttribute(Context context, int attr)
    {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    private static float interpolate(float a, float b, float proportion)
    {
        return (a + ((b - a) * proportion));
    }

    // from http://stackoverflow.com/a/7871291 and found this idea in the EyeEm app
    public static int interpolateColor(int a, int b, float proportion)
    {
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(a, hsva);
        Color.colorToHSV(b, hsvb);
        for (int i = 0; i < 3; i++)
            hsvb[i] = interpolate(hsva[i], hsvb[i], proportion);
        return Color.HSVToColor(hsvb);
    }

    public static void writeToSD(String path, String filename) throws IOException
    {
        File sd = Environment.getExternalStorageDirectory();

        if (sd.canWrite())
        {
            File currentFile = new File(path, filename);
            File backupFile = new File(sd, filename);

            if (currentFile.exists())
            {
                FileChannel src = new FileInputStream(currentFile).getChannel();
                FileChannel dst = new FileOutputStream(backupFile).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

    public static void writeDBToSD(Context context, String dbName) throws IOException
    {
        writeToSD(context.getFilesDir().getPath().replace("files", "databases"), dbName);
    }

    public static String pluralize(int count, String singular)
    {
        return pluralize(count, singular, singular.concat("s"));
    }

    public static String pluralize(int count, String singular, String plural)
    {
        return (count == 1 ? singular : plural);
    }

    public static String join(Collection<String> s, String delimiter)
    {
        if (s == null || s.isEmpty())
            return null;
        Iterator<String> iterator = s.iterator();
        StringBuilder builder = new StringBuilder(iterator.next());
        while (iterator.hasNext())
            builder.append(delimiter).append(iterator.next());
        return builder.toString();
    }

    public static void eraseAppData(Context context)
    {
        // erase prefs
        TraktoidPrefs.INSTANCE.clear();

        // erase folders
        deleteDir(context.getCacheDir());
        deleteDir(context.getFilesDir());

        // erase databases
        for(String name : context.databaseList())
            context.deleteDatabase(name);
    }

    private static boolean deleteDir(File dir)
    {
        if(dir == null)
            return false;

        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (String child : children)
            {
                if (!deleteDir(new File(dir, child)))
                    return false;
            }
        }

        return dir.delete();
    }
}
