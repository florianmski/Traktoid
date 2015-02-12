package com.florianmski.tracktoid.utils;

import android.content.Context;
import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DateHelper
{
    private static final DateTimeFormatter ISO_8601_WITH_MILLIS;

    static
    {
        ISO_8601_WITH_MILLIS = ISODateTimeFormat.dateTimeParser().withZoneUTC();
    }

    public static DateTime now()
    {
        return DateTime.now(DateTimeZone.UTC);
    }

    public static DateTime get(long millis)
    {
        return new DateTime(millis, DateTimeZone.UTC);
    }

    public static String getDate(Context context, DateTime d)
    {
        if(d == null || d.getMillis() == 0)
            return "Unknown";
        else
            return DateUtils.formatDateTime(context, d.getMillis(), DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_DATE);
    }

    public static String getRuntime(int runtime)
    {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroNever()
                .appendHours()
                .appendSeparatorIfFieldsBefore("h")
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparatorIfFieldsBefore("m")
                .toFormatter();
        return formatter.print(new Period(runtime*60*1000));
    }

    public static Long getTimestamp(DateTime date)
    {
        return date == null ? null : date.getMillis();
    }
}
