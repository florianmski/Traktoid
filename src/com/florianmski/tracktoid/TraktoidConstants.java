package com.florianmski.tracktoid;

public final class TraktoidConstants 
{
	private final static String PACKAGE_NAME = "com.florianmski.tracktoid";
	private final static String SEPARATOR = ".";
	
	public final static String BUNDLE_ = get("");
	
	public final static String BUNDLE_TVDB_ID = get("TvdbId");
	public final static String BUNDLE_SEASON_ID = get("SeasonId");
	public final static String BUNDLE_SHOW = get("Show");
	public final static String BUNDLE_RESULTS = get("Results");
	public final static String BUNDLE_POSITION = get("Position");
	public final static String BUNDLE_TITLE = get("Title");
	public final static String BUNDLE_HAS_MY_SHOW_FRAGMENT = get("HasMyShowFragment");
	public final static String BUNDLE_EPISODE = get("Episode");
	public final static String BUNDLE_SEASON = get("Season");
	public final static String BUNDLE_TVSHOW = get("TvShow");
	
	private final static String get(String text)
	{
		return PACKAGE_NAME + SEPARATOR + text;
	}
	
	public final static String PREF_USERNAME = "editTextUsername";
	public final static String PREF_PASSWORD = "editTextPassword";
	public final static String PREF_SHA1 = "sha1";
	
	public final static String KEY_BUGSENSE = "http://www.bugsense.com/api/acra?api_key=";
}
