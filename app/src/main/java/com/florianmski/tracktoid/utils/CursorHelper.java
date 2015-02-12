package com.florianmski.tracktoid.utils;

import android.database.Cursor;

import com.uwetrottmann.trakt.v2.enums.Rating;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

public class CursorHelper
{
    private Cursor cursor;

    public CursorHelper(Cursor cursor)
    {
        this.cursor = cursor;
    }

    public String getString(String column)
    {
        return cursor.getString(getIndex(column));
    }

    public String getString(String column, String defaultValue)
    {
        String data = getString(column);
        return data == null ? defaultValue : data;
    }

    public Integer getInt(String column)
    {
        int index = getIndex(column);
        return cursor.isNull(index) ? null : cursor.getInt(index);
    }

    public Integer getInt(String column, Integer defaultValue)
    {
        Integer data = getInt(column);
        return data == null ? defaultValue : data;
    }

    public Long getLong(String column)
    {
        int index = getIndex(column);
        return cursor.isNull(index) ? null : cursor.getLong(index);
    }

    public Long getLong(String column, Long defaultValue)
    {
        Long data = getLong(column);
        return data == null ? defaultValue : data;
    }

    public Double getDouble(String column)
    {
        int index = getIndex(column);
        return cursor.isNull(index) ? null : cursor.getDouble(index);
    }

    public Double getDouble(String column, Double defaultValue)
    {
        Double data = getDouble(column);
        return data == null ? defaultValue : data;
    }

    public boolean getBoolean(String column)
    {
        return getBoolean(column, false);
    }

    public boolean getBoolean(String column, boolean defaultValue)
    {
        Integer data = getInt(column);
        return data == null ? defaultValue : data != 0;
    }

    public DateTime getDate(String column)
    {
        return getDate(column, null);
    }

    public DateTime getDate(String column, DateTime defaultValue)
    {
        Long millis = getLong(column);
        return millis == null ? defaultValue : new DateTime(millis);
    }

    public Rating getRating(String column)
    {
        return getRating(column, null);
    }

    public Rating getRating(String column, Rating defaultValue)
    {
        Integer rating = getInt(column);
        return rating == null ? defaultValue : Rating.fromValue(rating);
    }

    public List<String> getStringList(String column)
    {
        return getStringList(column, null);
    }

    public List<String> getStringList(String column, List<String> defaultValue)
    {
        String list = getString(column);
        return list == null ? defaultValue : Arrays.asList(list.split(","));
    }

    private int getIndex(String column)
    {
        return cursor.getColumnIndexOrThrow(column);
    }
}
