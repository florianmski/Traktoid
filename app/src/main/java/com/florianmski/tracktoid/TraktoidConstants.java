package com.florianmski.tracktoid;

public final class TraktoidConstants 
{
	private final static String SEPARATOR = ".";
		
    public final static String BUNDLE_SEASON = get("Season");
    public final static String BUNDLE_SEASONS = get("Seasons");
    public final static String BUNDLE_SHOW_ID = get("ShowId");
	public final static String BUNDLE_POSITION = get("Position");
    public final static String BUNDLE_TITLE = get("Title");
    public final static String BUNDLE_SUBTITLE = get("Subtitle");
	public final static String BUNDLE_IDS = get("Ids");
    public final static String BUNDLE_ID = get("Id");
	public final static String BUNDLE_TABLE = get("Table");
    public final static String BUNDLE_EPISODE = get("Episode");
    public final static String BUNDLE_HEADER = get("header");
	
	private static String get(String text)
	{
		return BuildConfig.APPLICATION_ID + SEPARATOR + text;
	}
}
