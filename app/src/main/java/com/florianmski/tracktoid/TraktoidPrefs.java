package com.florianmski.tracktoid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.florianmski.tracktoid.utils.DateHelper;

import org.joda.time.DateTime;

public enum TraktoidPrefs
{
    INSTANCE;

    private final String ACCESS_TOKEN = getKey("accessToken");
    private final String USERNAME = getKey("username");
    private final String LAST_SYNC_TIME = getKey("lastSyncTime");
    private final String PREVIOUS_MIGRATION_VERSION_CODE = getKey("previousMigrationVersionCode");

    private static SharedPreferences prefs;

    public static void create(Context context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUserLoggedIn()
    {
        return getAccessToken() != null;
    }

    public String getAccessToken()
    {
        return getString(ACCESS_TOKEN, null);
    }

    public void putAccessToken(String accessToken)
    {
        putString(ACCESS_TOKEN, accessToken);
    }

    public String getUsername()
    {
        return getString(USERNAME, null);
    }

    public void putUsername(String username)
    {
        putString(USERNAME, username);
    }

    public DateTime getLastSyncTime()
    {
        return DateHelper.get(getLong(LAST_SYNC_TIME, 0));
    }

    public int refreshPreviousMigrationVersionCode()
    {
        int versionCode = BuildConfig.VERSION_CODE;
        putInt(PREVIOUS_MIGRATION_VERSION_CODE, versionCode);
        return versionCode;
    }

    public int getPreviousMigrationVersionCode()
    {
        // if the previous migration version code is absent, set it to the build config version code
        int previousMigrationversionCode = getInt(PREVIOUS_MIGRATION_VERSION_CODE, -1);
        if(previousMigrationversionCode == -1)
            previousMigrationversionCode = refreshPreviousMigrationVersionCode();
        return previousMigrationversionCode;
    }

    public void putLastSyncTime(DateTime dateTime)
    {
        putLong(LAST_SYNC_TIME, dateTime.getMillis());
    }

    private String getString(String key, String defValue)
    {
        return prefs.getString(key, defValue);
    }

    private boolean getBoolean(String key, boolean defValue)
    {
        return prefs.getBoolean(key, defValue);
    }

    private int getInt(String key, int defValue)
    {
        return prefs.getInt(key, defValue);
    }

    private long getLong(String key, long defValue)
    {
        return prefs.getLong(key, defValue);
    }

    private double getDouble(String key, double defValue)
    {
        return Double.longBitsToDouble(getLong(key, Double.doubleToLongBits(defValue)));
    }

    private double getDouble(String key, long defValue)
    {
        return Double.longBitsToDouble(getLong(key, defValue));
    }

    private void putString(String key, String value)
    {
        prefs.edit().putString(key, value).apply();
    }

    private void putBoolean(String key, boolean value)
    {
        prefs.edit().putBoolean(key, value).apply();
    }

    private void putInt(String key, int value)
    {
        prefs.edit().putInt(key, value).apply();
    }

    private void putLong(String key, long value)
    {
        prefs.edit().putLong(key, value).apply();
    }

    private void putDouble(String key, double value)
    {
        putLong(key, Double.doubleToLongBits(value));
    }

    public void clear()
    {
        prefs.edit().clear().apply();
    }

    private String getKey(String key)
    {
        return String.format("%s.%s", BuildConfig.APPLICATION_ID, key);
    }
}
