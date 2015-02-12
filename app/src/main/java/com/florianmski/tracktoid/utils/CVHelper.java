package com.florianmski.tracktoid.utils;

import android.content.ContentValues;

import org.joda.time.DateTime;

import java.util.List;

public class CVHelper
{
    private ContentValues cv;

    public CVHelper()
    {
        this.cv = new ContentValues();
    }

    public CVHelper(ContentValues cv)
    {
        this.cv = cv;
    }

    public CVHelper put(String column, Integer data)
    {
        cv.put(column, data);
        return this;
    }

    public CVHelper put(String column, Long data)
    {
        cv.put(column, data);
        return this;
    }

    public CVHelper put(String column, Double data)
    {
        cv.put(column, data);
        return this;
    }

    public CVHelper put(String column, Boolean data)
    {
        cv.put(column, Utils.booleanToInt(data));
        return this;
    }

    public CVHelper put(String column, String data)
    {
        cv.put(column, data);
        return this;
    }

    public CVHelper put(String column, DateTime data)
    {
        cv.put(column, getTimestamp(data));
        return this;
    }

    public CVHelper put(String column, List<String> data)
    {
        cv.put(column, Utils.join(data, ","));
        return this;
    }

    public CVHelper putAll(ContentValues contentValues)
    {
        cv.putAll(contentValues);
        return this;
    }

    public CVHelper putNull(String column)
    {
        cv.putNull(column);
        return this;
    }

    public ContentValues get()
    {
        return cv;
    }

    private Long getTimestamp(DateTime d)
    {
        return d == null ? null : d.getMillis();
    }
}
