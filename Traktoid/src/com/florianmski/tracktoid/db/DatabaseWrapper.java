package com.florianmski.tracktoid.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.florianmski.tracktoid.Utils;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.CalendarDate.CalendarTvShowEpisode;
import com.jakewharton.trakt.entities.Images;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.jakewharton.trakt.entities.TvShowSeason.Episodes;
import com.jakewharton.trakt.enumerations.DayOfTheWeek;
import com.jakewharton.trakt.enumerations.Rating;

public class DatabaseWrapper 
{
	// Begin constants:

	private static final String DATABASE_NAME = "tvshows.db";
	private static final int DATABASE_VERSION = 3;

	public static final String KEY_ID = "_id";
	public static final int COLUMN_KEY_ID = 0;

	/************************** TvShow table *******************************/
	private static final String TVSHOWS_TABLE = "tvshows";

	public static final String KEY_TVSHOW_TITLE = "title";
	public static final int COLUMN_TVSHOW_TITLE = 1;

	public static final String KEY_TVSHOW_YEAR = "year";
	public static final int COLUMN_TVSHOW_YEAR = 2;

	public static final String KEY_TVSHOW_URL = "url";
	public static final int COLUMN_TVSHOW_URL = 3;

	public static final String KEY_TVSHOW_FIRST_AIRED = "first_aired";
	public static final int COLUMN_TVSHOW_FIRST_AIRED = 4;

	public static final String KEY_TVSHOW_COUNTRY = "country";
	public static final int COLUMN_TVSHOW_COUNTRY = 5;

	public static final String KEY_TVSHOW_OVERVIEW = "overview";
	public static final int COLUMN_TVSHOW_OVERVIEW = 6;

	public static final String KEY_TVSHOW_RUNTIME = "runtime";
	public static final int COLUMN_TVSHOW_RUNTIME = 7;

	public static final String KEY_TVSHOW_NETWORK = "network";
	public static final int COLUMN_TVSHOW_NETWORK = 8;

	public static final String KEY_TVSHOW_AIR_DAY = "air_day";
	public static final int COLUMN_TVSHOW_AIR_DAY = 9;

	public static final String KEY_TVSHOW_AIR_TIME = "air_time";
	public static final int COLUMN_TVSHOW_AIR_TIME = 10;

	public static final String KEY_TVSHOW_CERTIFICATION = "certification";
	public static final int COLUMN_TVSHOW_CERTIFICATION = 11;

	public static final String KEY_TVSHOW_IMDB_ID = "imdb_id";
	public static final int COLUMN_TVSHOW_IMDB_ID = 12;

	public static final String KEY_TVSHOW_TVDB_ID = "tvdb_id";
	public static final int COLUMN_TVSHOW_TVDB_ID = 13;

	public static final String KEY_TVSHOW_TVRAGE_ID = "tvrage_id";
	public static final int COLUMN_TVSHOW_TVRAGE_ID = 14;

	public static final String KEY_TVSHOW_POSTER = "poster";
	public static final int COLUMN_TVSHOW_POSTER = 15;

	public static final String KEY_TVSHOW_FANART = "fanart";
	public static final int COLUMN_TVSHOW_FANART = 16;

	public static final String KEY_TVSHOW_PERCENTAGE = "percentage";
	public static final int COLUMN_TVSHOW_PERCENTAGE = 17;

	public static final String KEY_TVSHOW_VOTES = "votes";
	public static final int COLUMN_TVSHOW_VOTES = 18;

	public static final String KEY_TVSHOW_LOVED = "loved";
	public static final int COLUMN_TVSHOW_LOVED = 19;

	public static final String KEY_TVSHOW_HATED = "hated";
	public static final int COLUMN_TVSHOW_HATED = 20;

	public static final String KEY_TVSHOW_RATING = "rating";
	public static final int COLUMN_TVSHOW_RATING = 21;

	public static final String KEY_TVSHOW_IN_WATCHLIST = "in_watchlist";
	public static final int COLUMN_TVSHOW_IN_WATCHLIST = 22;

	public static final String KEY_TVSHOW_EPISODES_WATCHED = "episodes_watched";
	public static final int COLUMN_TVSHOW_EPISODES_WATCHED = 23;

	public static final String KEY_TVSHOW_EPISODES = "episodes";
	public static final int COLUMN_TVSHOW_EPISODES = 24;

	public static final String KEY_TVSHOW_PROGRESS = "progress";
	public static final int COLUMN_TVSHOW_PROGRESS = 25;

	public static final String KEY_TVSHOW_IN_COLLECTION = "in_collection";
	public static final int COLUMN_TVSHOW_IN_COLLECTION = 26;

	//	private final static String SELECT_TVSHOW = 
	//			KEY_ID + "," +
	//					KEY_TVSHOW_TITLE + "," +
	//					KEY_TVSHOW_YEAR + "," +
	//					KEY_TVSHOW_URL + "," +
	//					KEY_TVSHOW_FIRST_AIRED + "," +
	//					KEY_TVSHOW_COUNTRY + "," +
	//					KEY_TVSHOW_OVERVIEW + "," +
	//					KEY_TVSHOW_RUNTIME + "," +
	//					KEY_TVSHOW_NETWORK + "," +
	//					KEY_TVSHOW_AIR_DAY + "," +
	//					KEY_TVSHOW_AIR_TIME + "," +
	//					KEY_TVSHOW_CERTIFICATION + "," +
	//					KEY_TVSHOW_IMDB_ID + "," +
	//					KEY_TVSHOW_TVDB_ID + "," +
	//					KEY_TVSHOW_TVRAGE_ID + "," +
	//					KEY_TVSHOW_POSTER + "," +
	//					KEY_TVSHOW_FANART + "," +
	//					KEY_TVSHOW_PERCENTAGE + "," +
	//					KEY_TVSHOW_VOTES + "," +
	//					KEY_TVSHOW_LOVED + "," +
	//					KEY_TVSHOW_HATED + "," +
	//					KEY_TVSHOW_RATING + "," +
	//					KEY_TVSHOW_IN_WATCHLIST + "," +
	//					KEY_TVSHOW_EPISODES_WATCHED + "," +
	//					KEY_TVSHOW_EPISODES + "," +
	//					KEY_TVSHOW_PROGRESS + "," +
	//					KEY_TVSHOW_IN_COLLECTION;

	private static final String TVSHOWS_TABLE_CREATE = "create table " +
			TVSHOWS_TABLE + " (" + 
			KEY_ID + " integer primary key, " + 
			KEY_TVSHOW_TITLE + " text not null, " +
			KEY_TVSHOW_YEAR + " integer, " +
			KEY_TVSHOW_URL + " text, " +
			KEY_TVSHOW_FIRST_AIRED + " integer, " +
			KEY_TVSHOW_COUNTRY + " text, " +
			KEY_TVSHOW_OVERVIEW + " text, " +
			KEY_TVSHOW_RUNTIME + " integer, " +
			KEY_TVSHOW_NETWORK + " text, " +
			KEY_TVSHOW_AIR_DAY + " text," +
			KEY_TVSHOW_AIR_TIME + " text, " +
			KEY_TVSHOW_CERTIFICATION + " text, " +
			KEY_TVSHOW_IMDB_ID + " text, " +
			KEY_TVSHOW_TVDB_ID + " integer, " +
			KEY_TVSHOW_TVRAGE_ID + " integer, " +
			KEY_TVSHOW_POSTER + " text, " +
			KEY_TVSHOW_FANART + " text, " +
			KEY_TVSHOW_PERCENTAGE + " integer, " +
			KEY_TVSHOW_VOTES + " integer, " +
			KEY_TVSHOW_LOVED + " integer, " +
			KEY_TVSHOW_HATED + " integer, " +
			KEY_TVSHOW_RATING + " text, " +
			KEY_TVSHOW_IN_WATCHLIST + " boolean default 0, " + 
			KEY_TVSHOW_EPISODES_WATCHED + " integer default 0, " + 
			KEY_TVSHOW_EPISODES + " integer default 0, " + 
			KEY_TVSHOW_PROGRESS + " integer default 0, " + 
			KEY_TVSHOW_IN_COLLECTION + " boolean default 0 " + // No comma in the end!
			");";


	/************************** Seasons table *******************************/
	private static final String SEASONS_TABLE = "seasons";

	public static final String KEY_SEASON_SEASON = "season";
	public static final int COLUMN_SEASON_SEASON = 1;

	public static final String KEY_SEASON_EPISODES = "episodes";
	public static final int COLUMN_SEASON_EPISODES = 2;

	public static final String KEY_SEASON_EPISODES_WATCHED = "episodes_watched";
	public static final int COLUMN_SEASON_EPISODES_WATCHED = 3;

	public static final String KEY_SEASON_URL = "url";
	public static final int COLUMN_SEASON_URL = 4;

	public static final String KEY_SEASON_TVSHOW_ID = "tvshow_id";
	public static final int COLUMN_SEASON_TVSHOW_ID = 5;

	public static final String KEY_SEASON_IN_COLLECTION = "in_collection";
	public static final int COLUMN_SEASON_IN_COLLECTION = 6;

	//	private final static String SELECT_SEASON =
	//			KEY_ID + "," +
	//					KEY_SEASON_SEASON + "," +
	//					KEY_SEASON_EPISODES + "," +
	//					KEY_SEASON_EPISODES_WATCHED + "," +
	//					KEY_SEASON_URL + "," +
	//					KEY_SEASON_TVSHOW_ID + "," +
	//					KEY_SEASON_IN_COLLECTION;

	private static final String SEASONS_TABLE_CREATE = "create table " +
			SEASONS_TABLE + " (" + 
			KEY_ID + " integer primary key, " + 
			KEY_SEASON_SEASON + " integer, " +
			KEY_SEASON_EPISODES + " integer, " +
			KEY_SEASON_EPISODES_WATCHED + " integer default 0, " +
			KEY_SEASON_URL + " text, " + 
			KEY_SEASON_TVSHOW_ID + " integer REFERENCES " + TVSHOWS_TABLE + " (" + KEY_TVSHOW_TVDB_ID + "), " +
			KEY_SEASON_IN_COLLECTION + " boolean default 0 " + // No comma in the end! 
			");";


	/************************** Episodes table *******************************/
	private static final String EPISODES_TABLE = "episodes";

	public static final String KEY_EPISODE_SEASON = "season";
	public static final int COLUMN_EPISODE_SEASON = 1;

	public static final String KEY_EPISODE_EPISODE = "episode";
	public static final int COLUMN_EPISODE_EPISODE = 2;

	public static final String KEY_EPISODE_TITLE = "title";
	public static final int COLUMN_EPISODE_TITLE = 3;

	public static final String KEY_EPISODE_OVERVIEW = "overview";
	public static final int COLUMN_EPISODE_OVERVIEW = 4;

	public static final String KEY_EPISODE_FIRST_AIRED = "first_aired";
	public static final int COLUMN_EPISODE_FIRST_AIRED = 5;

	public static final String KEY_EPISODE_URL = "url";
	public static final int COLUMN_EPISODE_URL = 6;

	public static final String KEY_EPISODE_SCREEN = "screen";
	public static final int COLUMN_EPISODE_SCREEN = 7;

	public static final String KEY_EPISODE_PERCENTAGE = "percentage";
	public static final int COLUMN_EPISODE_PERCENTAGE = 8;

	public static final String KEY_EPISODE_VOTES = "votes";
	public static final int COLUMN_EPISODE_VOTES = 9;

	public static final String KEY_EPISODE_LOVED = "loved";
	public static final int COLUMN_EPISODE_LOVED = 10;

	public static final String KEY_EPISODE_HATED = "hated";
	public static final int COLUMN_EPISODE_HATED = 11;

	public static final String KEY_EPISODE_WATCHED = "watched";
	public static final int COLUMN_EPISODE_WATCHED = 12;

	public static final String KEY_EPISODE_SEASON_ID = "season_id";
	public static final int COLUMN_EPISODE_SEASON_ID = 13;

	public static final String KEY_EPISODE_IN_WATCHLIST = "in_watchlist";
	public static final int COLUMN_EPISODE_IN_WATCHLIST = 14;

	public static final String KEY_EPISODE_IN_COLLECTION = "in_collection";
	public static final int COLUMN_EPISODE_IN_COLLECTION = 15;

	public static final String KEY_EPISODE_RATING = "rating";
	public static final int COLUMN_EPISODE_RATING = 16;


	private final static String SELECT_EPISODE = 
			EPISODES_TABLE+"."+KEY_ID + "," +
					EPISODES_TABLE+"."+KEY_SEASON_SEASON + "," +
					KEY_EPISODE_EPISODE + "," +
					KEY_EPISODE_TITLE + "," +
					KEY_EPISODE_OVERVIEW + "," +
					KEY_EPISODE_FIRST_AIRED + "," +
					EPISODES_TABLE+"."+KEY_EPISODE_URL + "," +
					KEY_EPISODE_SCREEN + "," +
					KEY_EPISODE_PERCENTAGE + "," +
					KEY_EPISODE_VOTES + "," +
					KEY_EPISODE_LOVED + "," +
					KEY_EPISODE_HATED + "," +
					KEY_EPISODE_WATCHED + "," +
					KEY_EPISODE_SEASON_ID + "," +
					KEY_EPISODE_IN_WATCHLIST + "," +
					EPISODES_TABLE+"."+KEY_EPISODE_IN_COLLECTION + "," +
					KEY_EPISODE_RATING;

	private static final String EPISODES_TABLE_CREATE = "create table " +
			EPISODES_TABLE + " (" + 
			KEY_ID + " integer primary key, " + 
			KEY_EPISODE_SEASON + " integer, " +
			KEY_EPISODE_EPISODE + " integer, " +
			KEY_EPISODE_TITLE + " text, " +
			KEY_EPISODE_OVERVIEW + " text, " +
			KEY_EPISODE_FIRST_AIRED + " integer, " +
			KEY_EPISODE_URL + " text, " +
			KEY_EPISODE_SCREEN + " text, " +
			KEY_EPISODE_PERCENTAGE + " integer, " +
			KEY_EPISODE_VOTES + " integer, " +
			KEY_EPISODE_LOVED + " integer, " +
			KEY_EPISODE_HATED + " integer, " +
			KEY_EPISODE_WATCHED + " boolean default 0, " +
			KEY_EPISODE_SEASON_ID + "  REFERENCES " + SEASONS_TABLE + " (" + KEY_SEASON_URL + "), " +	
			KEY_EPISODE_IN_WATCHLIST + " boolean default 0, " + 
			KEY_EPISODE_IN_COLLECTION + " boolean default 0, " + 
			KEY_EPISODE_RATING + " text " + // No comma in the end!
			");";

	/************************** Movie table *******************************/
	private static final String MOVIES_TABLE = "movies";

	public static final String KEY_MOVIE_TITLE = "title";
	public static final int COLUMN_MOVIE_TITLE = 1;

	public static final String KEY_MOVIE_YEAR = "year";
	public static final int COLUMN_MOVIE_YEAR = 2;

	public static final String KEY_MOVIE_RELEASED = "realeased";
	public static final int COLUMN_MOVIE_RELEASED = 3;

	public static final String KEY_MOVIE_URL = "url";
	public static final int COLUMN_MOVIE_URL = 4;

	public static final String KEY_MOVIE_TRAILER = "trailer";
	public static final int COLUMN_MOVIE_TRAILER = 5;

	public static final String KEY_MOVIE_RUNTIME = "runtime";
	public static final int COLUMN_MOVIE_RUNTIME = 6;

	public static final String KEY_MOVIE_TAGLINE = "tagline";
	public static final int COLUMN_MOVIE_TAGLINE = 7;

	public static final String KEY_MOVIE_OVERVIEW = "overview";
	public static final int COLUMN_MOVIE_OVERVIEW = 8;

	public static final String KEY_MOVIE_CERTIFICATION = "certification";
	public static final int COLUMN_MOVIE_CERTIFICATION = 9;

	public static final String KEY_MOVIE_IMDB_ID = "imdb_id";
	public static final int COLUMN_MOVIE_IMDB_ID = 10;

	public static final String KEY_MOVIE_TMDB_ID = "tmdb_id";
	public static final int COLUMN_MOVIE_TMDB_ID = 11;

	public static final String KEY_MOVIE_RT_ID = "rt_id";
	public static final int COLUMN_MOVIE_RT_ID = 12;

	public static final String KEY_MOVIE_LAST_UPDATED = "last_updated";
	public static final int COLUMN_MOVIE_LAST_UPDATED = 13;

	public static final String KEY_MOVIE_POSTER = "poster";
	public static final int COLUMN_MOVIE_POSTER = 14;

	public static final String KEY_MOVIE_FANART = "fanart";
	public static final int COLUMN_MOVIE_FANART = 15;

	public static final String KEY_MOVIE_PERCENTAGE = "percentage";
	public static final int COLUMN_MOVIE_PERCENTAGE = 16;

	public static final String KEY_MOVIE_WATCHED = "watched";
	public static final int COLUMN_MOVIE_WATCHED = 17;

	public static final String KEY_MOVIE_RATING = "rating";
	public static final int COLUMN_MOVIE_RATING = 18;

	public static final String KEY_MOVIE_IN_WATCHLIST = "in_watchlist";
	public static final int COLUMN_MOVIE_IN_WATCHLIST = 19;

	public static final String KEY_MOVIE_IN_COLLECTION = "in_collection";
	public static final int COLUMN_MOVIE_IN_COLLECTION = 20;

	//	private final static String SELECT_MOVIE = 
	//			KEY_ID + "," +
	//					KEY_MOVIE_TITLE + "," +
	//					KEY_MOVIE_YEAR + "," +
	//					KEY_MOVIE_RELEASED + "," +
	//					KEY_MOVIE_URL + "," +
	//					KEY_MOVIE_TRAILER + "," +
	//					KEY_MOVIE_RUNTIME + "," +
	//					KEY_MOVIE_TAGLINE + "," +
	//					KEY_MOVIE_OVERVIEW + "," +
	//					KEY_MOVIE_CERTIFICATION + "," +
	//					KEY_MOVIE_IMDB_ID + "," +
	//					KEY_MOVIE_TMDB_ID + "," +
	//					KEY_MOVIE_RT_ID + "," +
	//					KEY_MOVIE_LAST_UPDATED + "," +
	//					KEY_MOVIE_POSTER + "," +
	//					KEY_MOVIE_FANART + "," +
	//					KEY_MOVIE_PERCENTAGE + "," +
	//					KEY_MOVIE_WATCHED + "," +
	//					KEY_MOVIE_RATING + "," +
	//					KEY_MOVIE_IN_WATCHLIST + "," +
	//					KEY_MOVIE_IN_COLLECTION;

	private static final String MOVIES_TABLE_CREATE = "create table " +
			MOVIES_TABLE + " (" + 
			KEY_ID + " integer primary key, " + 
			KEY_MOVIE_TITLE + " text, " +
			KEY_MOVIE_YEAR + " integer, " +
			KEY_MOVIE_RELEASED + " integer, " +
			KEY_MOVIE_URL + " text, " +
			KEY_MOVIE_TRAILER + " text, " +
			KEY_MOVIE_RUNTIME + " text, " +
			KEY_MOVIE_TAGLINE + " text, " +
			KEY_MOVIE_OVERVIEW + " text, " +
			KEY_MOVIE_CERTIFICATION + " text, " +
			KEY_MOVIE_IMDB_ID + " text, " +
			KEY_MOVIE_TMDB_ID + " integer, " +
			KEY_MOVIE_RT_ID + " integer, " +
			KEY_MOVIE_LAST_UPDATED + " text, " +
			KEY_MOVIE_POSTER + " text, " +
			KEY_MOVIE_FANART + " text, " +
			KEY_MOVIE_PERCENTAGE + " integer, " +
			KEY_MOVIE_WATCHED + " boolean default 0, " +
			KEY_MOVIE_RATING + " text," +
			KEY_MOVIE_IN_WATCHLIST + " boolean default 0, " +
			KEY_MOVIE_IN_COLLECTION + " boolean default 0 " + // No comma in the end!
			");";

	/************************** Genre table *******************************/
	private static final String GENRES_TABLE = "genres";

	public static final String KEY_GENRE_URL = "url";
	public static final int COLUMN_GENRE_URL = 0;

	public static final String KEY_GENRE_NAME = "name";
	public static final int COLUMN_GENRE_NAME = 1;

	private static final String GENRES_TABLE_CREATE = "create table " +
			GENRES_TABLE + " (" + 
			KEY_GENRE_URL + " text, " +
			KEY_GENRE_NAME + " text, " +
			"PRIMARY KEY (" + KEY_GENRE_URL + "," + KEY_GENRE_NAME + ")" + // No comma in the end!
			");";

	//	/************************** People table *******************************/
	//	private static final String PEOPLES_TABLE = "genres";
	//
	//	public static final String KEY_PEOPLE_URL = "url";
	//	public static final int COLUMN_PEOPLE_URL = 1;
	//
	//	public static final String KEY_PEOPLE_JOB = "job";
	//	public static final int COLUMN_PEOPLE_JOB = 2;
	//
	//	public static final String KEY_PEOPLE_NAME = "name";
	//	public static final int COLUMN_PEOPLE_NAME = 3;
	//
	//	private static final String PEOPLES_TABLE_CREATE = "create table " +
	//			PEOPLES_TABLE + " (" + 
	//			KEY_ID + " integer primary key, " + 
	//			KEY_PEOPLE_URL + " text, " +
	//			KEY_PEOPLE_JOB + " text, " +
	//			KEY_PEOPLE_NAME + " text " + // No comma in the end!
	//			");";

	//TODO
	/*
		   "genres":["Action","Comedy"],
		   "people":{
		      "directors":[
		         {
		            "name":"David Fincher"
		         }
		      ],
		      "writers":[
		         {
		            "name":"Aaron Sorkin",
		            "job":"Screenplay"
		         }
		      ],
		      "producers":[
		         {
		            "name":"Scott Rudin",
		            "executive":false
		         }
		      ],
		      "actors":[
		         {
		            "name":"Jesse Eisenberg",
		            "character":"Mark Zuckerberg"
		         }
		      ]
		   },*/

	/******************************* Triggers ***********************************/

	/** Update season table */
	private static final String EPISODES_WATCHED_INSERT_TRIGGER = "episodes_watched_insert_trigger";

	//update watched episodes in season table (insert case)
	private static final String EPISODES_WATCHED_INSERT_TRIGGER_CREATE = 
			"CREATE TRIGGER " +	EPISODES_WATCHED_INSERT_TRIGGER + " " +
					"AFTER INSERT ON " + EPISODES_TABLE + " " +
					"WHEN " + "new."+KEY_EPISODE_WATCHED + "=1 " +
					"BEGIN " +
					"UPDATE " + SEASONS_TABLE + " " +
					"SET " + KEY_SEASON_EPISODES_WATCHED + " = " + KEY_SEASON_EPISODES_WATCHED + "+1 " +
					"WHERE " + SEASONS_TABLE+"."+KEY_SEASON_URL + " = " + "new."+KEY_EPISODE_SEASON_ID + "; " +
					"END" + ";";

	//update watched episodes in season table (update case when watched value is updated to 1)
	private static final String EPISODES_WATCHED_UPDATE_1_TRIGGER = "episodes_watched_update_1_trigger";

	private static final String EPISODES_WATCHED_UPDATE_1_TRIGGER_CREATE = 
			"CREATE TRIGGER " +	EPISODES_WATCHED_UPDATE_1_TRIGGER + " " +
					"AFTER UPDATE OF " + KEY_EPISODE_WATCHED + " ON " + EPISODES_TABLE + " " +
					"WHEN " + "new."+KEY_EPISODE_WATCHED + "=1 AND " + "old."+KEY_EPISODE_WATCHED + "=0 " +
					"BEGIN " +
					"UPDATE " + SEASONS_TABLE + " " +
					"SET " + KEY_SEASON_EPISODES_WATCHED + " = " + KEY_SEASON_EPISODES_WATCHED + "+1 " +
					"WHERE " + SEASONS_TABLE+"."+KEY_SEASON_URL + " = " + "new."+KEY_EPISODE_SEASON_ID + ";" +
					"END" + ";";

	//update watched episodes in season table (update case when watched value is updated to 0)
	private static final String EPISODES_WATCHED_UPDATE_0_TRIGGER = "episodes_watched_update_0_trigger";

	private static final String EPISODES_WATCHED_UPDATE_0_TRIGGER_CREATE = 
			"CREATE TRIGGER " +	EPISODES_WATCHED_UPDATE_0_TRIGGER + " " +
					"AFTER UPDATE OF " + KEY_EPISODE_WATCHED + " ON " + EPISODES_TABLE + " " +
					"WHEN " + "new."+KEY_EPISODE_WATCHED + "=0 AND " + "old."+KEY_EPISODE_WATCHED + "=1 " +
					"BEGIN " +
					"UPDATE " + SEASONS_TABLE + " SET " + KEY_SEASON_EPISODES_WATCHED + " = " + KEY_SEASON_EPISODES_WATCHED + "-1 " +
					"WHERE " + SEASONS_TABLE+"."+KEY_SEASON_URL + " = " + "new."+KEY_EPISODE_SEASON_ID + ";" +
					"END" + ";";

	/** Update tvshow table */
	//update number of watched episodes in tvshow table (don't count watched episode in specials) (update case only, it's based on season's trigger)
	private static final String EPISODES_WATCHED_UPDATE_TRIGGER = "episodes_watched_update_trigger";

	private static final String EPISODES_WATCHED_UPDATE_TRIGGER_CREATE = 
			"CREATE TRIGGER " +	EPISODES_WATCHED_UPDATE_TRIGGER + " " +
					"AFTER UPDATE OF " + KEY_SEASON_EPISODES_WATCHED + " ON " + SEASONS_TABLE + " " +
					"WHEN " + "new."+KEY_SEASON_SEASON + "!=0 " +
					"BEGIN " +
					"UPDATE " + TVSHOWS_TABLE + " " +
					"SET " + KEY_TVSHOW_EPISODES_WATCHED + " = " + KEY_TVSHOW_EPISODES_WATCHED + " + new." + KEY_SEASON_EPISODES_WATCHED + "- old." + KEY_SEASON_EPISODES_WATCHED + " " +
					"WHERE " + KEY_TVSHOW_TVDB_ID + " = " + "new."+KEY_SEASON_TVSHOW_ID + "; " +
					"END" + ";";

	/** don't count episodes in "specials" */
	//update number of episodes in tvshow table (don't count episode in specials) (insert case)
	private static final String EPISODES_INSERT_TRIGGER = "episodes_insert_trigger";

	private static final String EPISODES_INSERT_TRIGGER_CREATE = 
			"CREATE TRIGGER " +	EPISODES_INSERT_TRIGGER + " AFTER INSERT ON " + SEASONS_TABLE + " " +
					"WHEN " + "new."+KEY_SEASON_SEASON + "!=0 " + 
					"BEGIN " +
					"UPDATE " + TVSHOWS_TABLE + " SET " + KEY_TVSHOW_EPISODES + " = " + KEY_TVSHOW_EPISODES + "+ new." + KEY_SEASON_EPISODES + " " +
					"WHERE " + KEY_TVSHOW_TVDB_ID + " = " + "new."+KEY_SEASON_TVSHOW_ID + "; " +
					"END" + ";";

	//update number of episodes in tvshow table (don't count episode in specials) (update case)
	private static final String EPISODES_UPDATE_TRIGGER = "episodes_update_trigger";

	private static final String EPISODES_UPDATE_TRIGGER_CREATE = 
			"CREATE TRIGGER " +	EPISODES_UPDATE_TRIGGER + " " +
					"AFTER UPDATE OF " + KEY_SEASON_EPISODES + " ON " + SEASONS_TABLE + " " +
					"WHEN " + "new."+KEY_SEASON_SEASON + "!=0 " +
					"BEGIN " +
					"UPDATE " + TVSHOWS_TABLE + " SET " + KEY_TVSHOW_EPISODES + " = " + KEY_TVSHOW_EPISODES + "+ new." + KEY_SEASON_EPISODES + "- old." + KEY_SEASON_EPISODES + " " +
					"WHERE " + KEY_TVSHOW_TVDB_ID + " = " + "new."+KEY_SEASON_TVSHOW_ID + "; " +
					"END" + ";";


	/******************************************************************************/
	/**
	 * Helper class to create/open/migrate the database
	 */
	private static class DataBaseOpener extends SQLiteOpenHelper
	{
		private Context context;

		public DataBaseOpener(Context context, String name, CursorFactory factory, int version) 
		{
			super(context, name, factory, version); 
			this.context = context;
		}
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL(TVSHOWS_TABLE_CREATE);
			db.execSQL(SEASONS_TABLE_CREATE);
			db.execSQL(EPISODES_TABLE_CREATE);

			db.execSQL(MOVIES_TABLE_CREATE);
			db.execSQL(GENRES_TABLE_CREATE);

			db.execSQL(EPISODES_WATCHED_INSERT_TRIGGER_CREATE);
			db.execSQL(EPISODES_WATCHED_UPDATE_1_TRIGGER_CREATE);
			db.execSQL(EPISODES_WATCHED_UPDATE_0_TRIGGER_CREATE);

			db.execSQL(EPISODES_WATCHED_UPDATE_TRIGGER_CREATE);
			db.execSQL(EPISODES_UPDATE_TRIGGER_CREATE);
			db.execSQL(EPISODES_INSERT_TRIGGER_CREATE);
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, int oldVersion, final int newVersion) 
		{
			if(newVersion <= 2)
				upgradeFromV1ToV2(db);

			if(newVersion <= 3)
				upgradeFromV2ToV3(db);
		}

		private void upgradeFromV1ToV2(SQLiteDatabase db)
		{
			//adding new columns
			db.execSQL(
					"ALTER TABLE " + TVSHOWS_TABLE + " " +
							"ADD COLUMN " + KEY_TVSHOW_PROGRESS + ";");

			db.execSQL(
					"ALTER TABLE " + SEASONS_TABLE + " " +
							"ADD COLUMN " + KEY_SEASON_TVSHOW_ID + " integer " +
							"REFERENCES " + TVSHOWS_TABLE + " (" + KEY_TVSHOW_TVDB_ID + ");");

			db.execSQL(
					"ALTER TABLE " + EPISODES_TABLE + " " +
							"ADD COLUMN " + KEY_EPISODE_SEASON_ID + " text " +
							"REFERENCES " + SEASONS_TABLE + " (" + KEY_SEASON_URL + ");");

			//transfering data
			db.execSQL("UPDATE " + SEASONS_TABLE + " SET " + KEY_SEASON_TVSHOW_ID + " = (SELECT tvshow_id FROM tvshows_seasons WHERE season_id = " + KEY_SEASON_URL + ");");
			db.execSQL("UPDATE " + EPISODES_TABLE + " SET " + KEY_EPISODE_SEASON_ID + " = (SELECT season_id FROM seasons_episodes WHERE episode_id = " + KEY_EPISODE_URL + ");");

			//drop stupid tables
			db.execSQL("DROP TABLE tvshows_seasons;");
			db.execSQL("DROP TABLE seasons_episodes;");

			//drop and recreate same triggers
			db.execSQL("DROP TRIGGER " + EPISODES_WATCHED_INSERT_TRIGGER + ";");
			db.execSQL("DROP TRIGGER " + EPISODES_WATCHED_UPDATE_0_TRIGGER + ";");
			db.execSQL("DROP TRIGGER " + EPISODES_WATCHED_UPDATE_1_TRIGGER + ";");
			db.execSQL("DROP TRIGGER " + EPISODES_WATCHED_UPDATE_TRIGGER + ";");
			db.execSQL("DROP TRIGGER " + EPISODES_INSERT_TRIGGER + ";");
			db.execSQL("DROP TRIGGER " + EPISODES_UPDATE_TRIGGER + ";");

			db.execSQL(EPISODES_WATCHED_INSERT_TRIGGER_CREATE);
			db.execSQL(EPISODES_WATCHED_UPDATE_1_TRIGGER_CREATE);
			db.execSQL(EPISODES_WATCHED_UPDATE_0_TRIGGER_CREATE);
			db.execSQL(EPISODES_WATCHED_UPDATE_TRIGGER_CREATE);
			db.execSQL(EPISODES_UPDATE_TRIGGER_CREATE);
			db.execSQL(EPISODES_INSERT_TRIGGER_CREATE);

			DatabaseWrapper dbw = new DatabaseWrapper(context);

			for(TvShow s : dbw.getShows())
				dbw.refreshPercentage(s.tvdbId);

			dbw.close();
		}

		private void upgradeFromV2ToV3(SQLiteDatabase db)
		{
			db.execSQL(MOVIES_TABLE_CREATE);
			db.execSQL(GENRES_TABLE_CREATE);

			//adding new columns
			db.execSQL(
					"ALTER TABLE " + TVSHOWS_TABLE + " " +
							"ADD COLUMN " + KEY_TVSHOW_IN_COLLECTION + " boolean default 0 " + ";");

			db.execSQL(
					"ALTER TABLE " + SEASONS_TABLE + " " +
							"ADD COLUMN " + KEY_TVSHOW_IN_COLLECTION + " boolean default 0 " + ";");

			db.execSQL(
					"ALTER TABLE " + EPISODES_TABLE + " " +
							"ADD COLUMN " + KEY_TVSHOW_IN_WATCHLIST + " boolean default 0 " + ";");

			db.execSQL(
					"ALTER TABLE " + EPISODES_TABLE + " " +
							"ADD COLUMN " + KEY_TVSHOW_IN_COLLECTION + " boolean default 0 " + ";");
		}
	}

	/******************************************************************************/
	private final Context context;
	private static SQLiteDatabase db = null;
	private static Integer nbOpenedInstances = Integer.valueOf(0);

	public DatabaseWrapper(Context context) 
	{
		this.context = context;
		this.open();
	}

	public void open() throws SQLException 
	{
		synchronized(nbOpenedInstances) 
		{
			if(nbOpenedInstances.intValue() == 0) 
			{
				DataBaseOpener dbOpener = new DataBaseOpener(this.context, DATABASE_NAME, null, DATABASE_VERSION);
				db = dbOpener.getWritableDatabase();
			}
			nbOpenedInstances++;
		}
	}

	public void close()
	{
		synchronized (nbOpenedInstances) 
		{
			if(nbOpenedInstances.intValue() > 0) 
				nbOpenedInstances--;
			if(nbOpenedInstances.intValue() == 0 && db.isOpen()) 
				db.close();
		}
	}

	/******************************************************************************/

	/**
	 *  Helper for inserting or updating an entry:
	 * @param table
	 * @param values
	 * @param id
	 */
	private void insertOrUpdate(String table, ContentValues values, String id) 
	{
		String key_id = null;

		if(table.equals(TVSHOWS_TABLE))
			key_id = KEY_TVSHOW_TVDB_ID;
		else if(table.equals(SEASONS_TABLE))
			key_id = KEY_SEASON_URL;
		else if(table.equals(EPISODES_TABLE))
			key_id = KEY_EPISODE_URL;
		else if(table.equals(MOVIES_TABLE))
			key_id = KEY_MOVIE_URL;

		int nbRowsAffected = 0;

		// Try to update the entry!
		if(id != null)
			nbRowsAffected = db.update(
					table,
					values,
					key_id + "=?",
					new String[]{id});

		// If nothing has been updated, insert a new entry
		if(nbRowsAffected == 0)
			db.insertOrThrow(table, null, values);
	}

	public void putNotNull(ContentValues cv, String key, Integer value)
	{
		if(value != null)
			cv.put(key, value);
	}

	public void putNotNull(ContentValues cv, String key, Boolean value)
	{
		if(value != null)
			cv.put(key, value);
	}

	public void putNotNull(ContentValues cv, String key, String value)
	{
		if(value != null)
			cv.put(key, value);
	}

	public void putNotNull(ContentValues cv, String key, Long value)
	{
		if(value != null)
			cv.put(key, value);
	}

	/************************** Shows methods *******************************/

	/**
	 *  Insert or update a tvshow
	 */
	public void insertOrUpdateShow(TvShow s) 
	{
		ContentValues values = new ContentValues();

		if(s.ratings != null)
		{
			putNotNull(values, KEY_TVSHOW_HATED, s.ratings.hated);
			putNotNull(values, KEY_TVSHOW_PERCENTAGE, s.ratings.percentage);
			putNotNull(values, KEY_TVSHOW_LOVED, s.ratings.loved);
			putNotNull(values, KEY_TVSHOW_VOTES, s.ratings.votes);			
		}

		if(s.rating != null)
			putNotNull(values, KEY_TVSHOW_RATING, s.rating.toString());

		if(s.airDay != null)
			putNotNull(values, KEY_TVSHOW_AIR_DAY, s.airDay.toString());
		else
			putNotNull(values, KEY_TVSHOW_AIR_DAY, "");

		putNotNull(values, KEY_TVSHOW_AIR_TIME, s.airTime);
		putNotNull(values, KEY_TVSHOW_CERTIFICATION, s.certification);
		putNotNull(values, KEY_TVSHOW_COUNTRY, s.country);

		if(s.images != null)
		{
			putNotNull(values, KEY_TVSHOW_FANART, s.images.fanart);
			putNotNull(values, KEY_TVSHOW_POSTER, s.images.poster);
		}

		if(s.firstAired != null)
			putNotNull(values, KEY_TVSHOW_FIRST_AIRED, String.valueOf(s.firstAired.getTime()));

		putNotNull(values, KEY_TVSHOW_IMDB_ID, s.imdbId);
		putNotNull(values, KEY_TVSHOW_IN_WATCHLIST, s.inWatchlist);
		putNotNull(values, KEY_TVSHOW_IN_COLLECTION, s.inCollection);
		putNotNull(values, KEY_TVSHOW_NETWORK, s.network);
		putNotNull(values, KEY_TVSHOW_OVERVIEW, s.overview);
		putNotNull(values, KEY_TVSHOW_RUNTIME, s.runtime);
		putNotNull(values, KEY_TVSHOW_TITLE, s.title);
		putNotNull(values, KEY_TVSHOW_TVDB_ID, s.tvdbId);
		putNotNull(values, KEY_TVSHOW_TVRAGE_ID, s.tvrageId);
		putNotNull(values, KEY_TVSHOW_URL, s.url);
		putNotNull(values, KEY_TVSHOW_YEAR, Integer.valueOf(s.year));

		//		putNotNull(values, KEY_TVSHOW_PROGRESS, s.getProgress());

		insertOrUpdate(TVSHOWS_TABLE, values, s.tvdbId);

		insertOrUpdateGenres(s.genres, s.url);
	}

	/**
	 *  Insert or update a list of tvshow
	 */
	public void insertOrUpdateShows(List<TvShow> shows) 
	{
		for(TvShow s : shows)
			insertOrUpdateShow(s);
	}

	private TvShow getShowFromCursor(Cursor c)
	{
		TvShow show = new TvShow();
		Images i = new Images();
		Ratings r = new Ratings();

		i.fanart = c.getString(COLUMN_TVSHOW_FANART);
		i.poster = c.getString(COLUMN_TVSHOW_POSTER);

		r.hated = c.getInt(COLUMN_TVSHOW_HATED);
		r.loved = c.getInt(COLUMN_TVSHOW_LOVED);
		r.percentage = c.getInt(COLUMN_TVSHOW_PERCENTAGE);
		r.votes = c.getInt(COLUMN_TVSHOW_VOTES);

		show.airDay = DayOfTheWeek.fromValue(c.getString(COLUMN_TVSHOW_AIR_DAY));
		show.airTime = c.getString(COLUMN_TVSHOW_AIR_TIME);
		show.certification = c.getString(COLUMN_TVSHOW_CERTIFICATION);
		show.country = c.getString(COLUMN_TVSHOW_COUNTRY);
		String date = c.getString(COLUMN_TVSHOW_FIRST_AIRED);
		try
		{
			long intDate = Long.valueOf(date);
			//new way
			show.firstAired = new Date(intDate);
		}
		catch(Exception e)
		{
			//old way
			//impossible to get a proper timestamp, there is a mess with the locale
			//tell the user to refresh
			//			try 
			//			{
			//				show.firstAired = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ROOT).parse(date);
			//			} catch (ParseException e1) 
			//			{
			//				e1.printStackTrace();
			//			}
		}
		show.network = c.getString(COLUMN_TVSHOW_NETWORK);
		show.overview = c.getString(COLUMN_TVSHOW_OVERVIEW);
		show.runtime = c.getInt(COLUMN_TVSHOW_RUNTIME);
		show.tvdbId = c.getString(COLUMN_TVSHOW_TVDB_ID);
		show.tvrageId = c.getString(COLUMN_TVSHOW_TVRAGE_ID);
		show.rating = c.getString(COLUMN_TVSHOW_RATING) == null ? null : Rating.fromValue(c.getString(COLUMN_TVSHOW_RATING));
		show.inWatchlist = c.getInt(COLUMN_TVSHOW_IN_WATCHLIST) != 0;
		show.inCollection = c.getInt(COLUMN_TVSHOW_IN_COLLECTION) != 0;
		show.images = i;
		show.imdbId = c.getString(COLUMN_TVSHOW_IMDB_ID);
		show.ratings = r;
		show.title = c.getString(COLUMN_TVSHOW_TITLE);
		show.url = c.getString(COLUMN_TVSHOW_URL);
		show.year = c.getInt(COLUMN_TVSHOW_YEAR);

		show.progress = c.getInt(COLUMN_TVSHOW_PROGRESS);

		show.genres = getGenres(show.url);

		return show;
	}

	public List<TvShow> getShows()
	{
		List<TvShow> shows = new ArrayList<TvShow>();
		Cursor c = db.rawQuery(
				"SELECT * " +
						"FROM " + TVSHOWS_TABLE + " " +
						"ORDER BY " + KEY_TVSHOW_TITLE, 
						null);
		c.moveToFirst();
		for(int i = 0; i < c.getCount(); i++)
		{
			shows.add(getShowFromCursor(c));
			c.moveToNext();
		}

		c.close();

		if(shows != null && shows.size() > 0)
			Collections.sort(shows);

		return shows;
	}

	public TvShow getShow(String tvdbId)
	{
		Cursor c = db.rawQuery(
				"SELECT * " +
						"FROM " + TVSHOWS_TABLE + " " +
						"WHERE " + KEY_TVSHOW_TVDB_ID + "=?", 
						new String[]{tvdbId});

		TvShow show = null;

		if(c.moveToFirst())
			show = getShowFromCursor(c);

		c.close();

		return show;
	}

	public void removeShow(String tvdbId)
	{
		//TODO redo this
		boolean showFound = db.delete(
				TVSHOWS_TABLE,
				KEY_TVSHOW_TVDB_ID + "=?",
				new String[]{tvdbId}) > 0;

				if(showFound) 
				{
					List<TvShowSeason> seasons = getSeasons(tvdbId, true, true);
					for(TvShowSeason season : seasons)
					{
						List<TvShowEpisode> episodes = season.episodes.episodes;
						db.delete(
								SEASONS_TABLE,
								KEY_SEASON_URL + "=?",
								new String[]{season.url});

						for(TvShowEpisode episode : episodes)
						{
							db.delete(
									EPISODES_TABLE,
									KEY_EPISODE_URL + "=?",
									new String[]{episode.url});
						}
					}

					//TODO
					//					removeGenres(url);
				}
	}

	public boolean showExist(String tvdbId)
	{
		Cursor c = db.rawQuery(
				"SELECT " + KEY_TVSHOW_TVDB_ID + " " +
						"FROM " + TVSHOWS_TABLE + " " +
						"WHERE " + KEY_TVSHOW_TVDB_ID + "=?", 
						new String[]{tvdbId});

		boolean exist = c.moveToFirst();

		c.close();

		return exist;
	}

	public void addOrRemoveShowInCollection(String tvdbId, boolean inCollection)
	{
		//TODO for all API methods like collection, watchlist, seen... check if show/movie/episode is already in db
		//else dl it
		TvShow show = getShow(tvdbId);
		if(show != null)
		{
			show.inCollection = inCollection;
			insertOrUpdateShow(show);

			ContentValues cv = new ContentValues();
			cv.put(KEY_EPISODE_IN_COLLECTION, inCollection);

			String[] whereArgs = null;
			if(inCollection)
				whereArgs = new String[]{tvdbId, String.valueOf(new Date().getTime())};
			else
				whereArgs = new String[]{tvdbId};

			db.update(
					EPISODES_TABLE, 
					cv, 
					KEY_EPISODE_SEASON_ID + " " +
							"IN (SELECT " + KEY_SEASON_URL + " " +
							"FROM " + SEASONS_TABLE + " " +
							"WHERE " + KEY_SEASON_TVSHOW_ID + "=?) " +
							//if we remove from collection, remove all
							//else if we add, add only episodes aired
							(inCollection ? "AND " + KEY_EPISODE_FIRST_AIRED + "< ?" : ""),
							whereArgs);
		}
	}

	public void markShowAsSeen(String tvdbId, boolean seen)
	{
		//TODO for all API methods like collection, watchlist, seen... check if show/movie/episode is already in db
		//else dl it
		TvShow show = getShow(tvdbId);
		if(show != null)
		{
			show.progress = seen ? 100 : 0;
			insertOrUpdateShow(show);

			ContentValues cv = new ContentValues();
			cv.put(KEY_EPISODE_WATCHED, seen);

			String[] whereArgs = null;
			if(seen)
				whereArgs = new String[]{tvdbId, String.valueOf(new Date().getTime())};
			else
				whereArgs = new String[]{tvdbId};

			db.update(
					EPISODES_TABLE, 
					cv, 
					KEY_EPISODE_SEASON_ID + " " +
							"IN (SELECT " + KEY_SEASON_URL + " " +
							"FROM " + SEASONS_TABLE + " " +
							"WHERE " + KEY_SEASON_TVSHOW_ID + "=?) " +
							//if we remove from collection, remove all
							//else if we add, add only episodes aired
							(seen ? "AND " + KEY_EPISODE_FIRST_AIRED + "< ?" : ""),
							whereArgs);
		}
	}

	/************************** Seasons methods *******************************/	

	/**
	 *  Insert or update a season
	 */
	public void insertOrUpdateSeason(TvShowSeason s, String tvshowId) 
	{
		ContentValues values = new ContentValues();

		if(s.episodes != null)
		{
			int episodes = s.episodes.episodes.size();
			putNotNull(values, KEY_SEASON_EPISODES, episodes);
		}

		int season = s.season;
		String url = s.url;

		putNotNull(values, KEY_SEASON_SEASON, season);
		putNotNull(values, KEY_SEASON_URL, url);
		putNotNull(values, KEY_SEASON_TVSHOW_ID, tvshowId);
		//TODO
		//		putNotNull(values, KEY_SEASON_IN_COLLECTION, s.inCollection);

		insertOrUpdate(SEASONS_TABLE, values, url);

	}

	/**
	 *  Insert or update a list of seasons
	 */
	public void insertOrUpdateSeasons(List<TvShowSeason> seasons, String tvshowId)
	{
		for(TvShowSeason s : seasons)
			insertOrUpdateSeason(s, tvshowId);
	}

	private TvShowSeason getSeasonFromCursor(Cursor c, String tvdbId, boolean getEpisodesToo)
	{
		TvShowSeason s = new TvShowSeason();
		Episodes episodes = new Episodes();

		if(getEpisodesToo)
			episodes.episodes = getEpisodes(c.getString(COLUMN_SEASON_URL));

		s.episodes = episodes;

		s.season = c.getInt(COLUMN_SEASON_SEASON);
		s.episodesWatched = c.getInt(COLUMN_SEASON_EPISODES_WATCHED);
		s.episodes.count = (c.getInt(COLUMN_SEASON_EPISODES));
		s.url = c.getString(COLUMN_SEASON_URL);
		//TODO
		//		s.inCollection = c.getString(COLUMN_SEASON_IN_COLLECTION);

		return s;
	}

	public List<TvShowSeason> getSeasons(String tvdbId, boolean getEpisodesToo, boolean orderByASC)
	{
		ArrayList<TvShowSeason> seasons = new ArrayList<TvShowSeason>();
		String sql = 
				"SELECT * " + 
						"FROM " + SEASONS_TABLE + " " +
						"WHERE " + KEY_SEASON_TVSHOW_ID	+ "=? " +
						"ORDER BY " + KEY_SEASON_SEASON + (orderByASC ? " ASC" : " DESC");
		Cursor c = db.rawQuery(sql, new String[]{tvdbId});
		c.moveToFirst();
		for(int i = 0; i < c.getCount(); i++)
		{
			seasons.add(getSeasonFromCursor(c, tvdbId, getEpisodesToo));
			c.moveToNext();
		}

		c.close();

		return seasons;
	}

	/************************** Episodes methods *******************************/

	/**
	 *  Insert or update an episode
	 */
	public void insertOrUpdateEpisode(TvShowEpisode e, String seasonId) 
	{
		ContentValues values = new ContentValues();

		String url = e.url;

		putNotNull(values, KEY_EPISODE_EPISODE, e.number);

		if(e.firstAired != null)
			putNotNull(values, KEY_EPISODE_FIRST_AIRED, e.firstAired.getTime());

		if(e.ratings != null)
		{
			putNotNull(values, KEY_EPISODE_HATED, e.ratings.hated);
			putNotNull(values, KEY_EPISODE_LOVED, e.ratings.loved);
			putNotNull(values, KEY_EPISODE_VOTES, e.ratings.votes);
			putNotNull(values, KEY_EPISODE_PERCENTAGE, e.ratings.percentage);
		}

		putNotNull(values, KEY_EPISODE_OVERVIEW, e.overview);

		if(e.images != null)
			putNotNull(values, KEY_EPISODE_SCREEN, e.images.screen);

		putNotNull(values, KEY_EPISODE_SEASON, e.season);
		putNotNull(values, KEY_EPISODE_TITLE, e.title);
		putNotNull(values, KEY_EPISODE_URL, url);
		putNotNull(values, KEY_EPISODE_WATCHED, e.watched);
		putNotNull(values, KEY_EPISODE_IN_WATCHLIST, e.inWatchlist);
		putNotNull(values, KEY_EPISODE_IN_COLLECTION, e.inCollection);

		if(e.rating != null)
			putNotNull(values, KEY_TVSHOW_RATING, e.rating.toString());

		putNotNull(values, KEY_EPISODE_SEASON_ID, seasonId);
		insertOrUpdate(EPISODES_TABLE, values, url);
	}

	/**
	 *  Insert or update an episode
	 *  /!\ be careful, this episode MUST BE ALREADY IN DB /!\
	 */
	//TODO find a better solution
	public boolean insertOrUpdateEpisode(TvShowEpisode e) 
	{
		//this episode is not in db, cancel insertion
		if(getEpisode(e.url) == null)
			return false;
		else
			insertOrUpdateEpisode(e, null);

		return true;
	}

	/**
	 *  Insert or update a list of seasons
	 */
	public void insertOrUpdateEpisodes(List<TvShowEpisode> episodes, String seasonId)
	{
		for(TvShowEpisode e : episodes)
			insertOrUpdateEpisode(e, seasonId);
	}

	private TvShowEpisode getEpisodeFromCursor(Cursor c, String tvdbId)
	{
		if(c.getCount() == 0)
			return null;

		TvShowEpisode e = new TvShowEpisode();

		Ratings r = new Ratings();
		r.hated = c.getInt(COLUMN_EPISODE_HATED);
		r.loved = c.getInt(COLUMN_EPISODE_LOVED);
		r.percentage = c.getInt(COLUMN_EPISODE_PERCENTAGE);
		r.votes = c.getInt(COLUMN_EPISODE_VOTES);

		Images i = new Images();
		i.screen = c.getString(COLUMN_EPISODE_SCREEN);

		e.number = c.getInt(COLUMN_EPISODE_EPISODE);
		e.firstAired = new Date(c.getLong(COLUMN_EPISODE_FIRST_AIRED));
		e.overview = c.getString(COLUMN_EPISODE_OVERVIEW);
		e.ratings = r;
		e.images = i;
		e.season = c.getInt(COLUMN_EPISODE_SEASON);
		e.seasonId = c.getString(COLUMN_EPISODE_SEASON_ID);
		e.title = c.getString(COLUMN_EPISODE_TITLE);
		e.url = c.getString(COLUMN_EPISODE_URL);
		e.watched = c.getInt(COLUMN_EPISODE_WATCHED) != 0;
		e.inWatchlist = c.getInt(COLUMN_EPISODE_IN_WATCHLIST) != 0;
		e.inCollection = c.getInt(COLUMN_EPISODE_IN_COLLECTION) != 0;
		e.rating = c.getString(COLUMN_EPISODE_RATING) == null ? null : Rating.fromValue(c.getString(COLUMN_EPISODE_RATING));

		e.tvdbId = tvdbId;

		return e;
	}

	public List<TvShowEpisode> getEpisodes(String seasonId)
	{
		String tvdbId = getTvdbIdWithSeasonId(seasonId);

		ArrayList<TvShowEpisode> episodes = new ArrayList<TvShowEpisode>();
		String sql = 
				"SELECT * " + 
						"FROM " + EPISODES_TABLE + " " +
						"WHERE " + KEY_EPISODE_SEASON_ID + "=? " + 
						"ORDER BY " + KEY_EPISODE_EPISODE;
		Cursor c = db.rawQuery(sql, new String[]{seasonId});
		c.moveToFirst();
		for(int i = 0; i < c.getCount(); i++)
		{
			episodes.add(getEpisodeFromCursor(c, tvdbId));
			c.moveToNext();
		}

		c.close();

		return episodes;
	}

	public String getTvdbIdWithSeasonId(String seasonId)
	{
		String sql = 
				"SELECT " + KEY_SEASON_TVSHOW_ID + " " +
						"FROM " + SEASONS_TABLE + " " +
						"WHERE " + KEY_SEASON_URL + "=? ";
		Cursor c = db.rawQuery(sql, new String[]{seasonId});

		if(c.moveToFirst())
			return c.getString(0);

		return null;
	}

	public String getTvdbIdWithEpisodeId(String episodeId)
	{
		String sql = 
				"SELECT " + KEY_EPISODE_SEASON_ID + " " +
						"FROM " + EPISODES_TABLE + " " +
						"WHERE " + KEY_EPISODE_URL + "=? ";
		Cursor c = db.rawQuery(sql, new String[]{episodeId});

		if(c.moveToFirst())
			return getTvdbIdWithSeasonId(c.getString(0));

		return null;
	}

	//	public TvShowEpisode getEpisode(String seasonId, int episode)
	//	{
	//		String sql = 
	//				"SELECT * " + 
	//						"FROM " + EPISODES_TABLE + " " +
	//						"WHERE " + KEY_EPISODE_SEASON_ID + "=? " + 
	//						"AND " + KEY_EPISODE_EPISODE + "=? " +
	//						"ORDER BY " + KEY_EPISODE_EPISODE;
	//		Cursor c = db.rawQuery(sql, new String[]{seasonId, String.valueOf(episode)});
	//		c.moveToFirst();
	//
	//		TvShowEpisode tvEpisode = getEpisodeFromCursor(c);
	//
	//		c.close();
	//
	//		return tvEpisode;
	//	}

	public TvShowEpisode getEpisode(String url)
	{
		String tvdbId = getTvdbIdWithEpisodeId(url);
		String sql = 
				"SELECT * " + 
						"FROM " + EPISODES_TABLE + " " +
						"WHERE " + KEY_EPISODE_URL + "=? " + 
						"ORDER BY " + KEY_EPISODE_EPISODE;
		Cursor c = db.rawQuery(sql, new String[]{url});

		TvShowEpisode tvEpisode = null;

		if(c.moveToFirst())
			tvEpisode = getEpisodeFromCursor(c, tvdbId);

		c.close();

		return tvEpisode;
	}

	public void markEpisodeAsWatched(boolean watched, String tvdbId, int season, int episode)
	{
		ContentValues cv = new ContentValues();
		cv.put(KEY_EPISODE_WATCHED, watched);

		db.update(
				EPISODES_TABLE, 
				cv, 
				KEY_EPISODE_SEASON + "=? AND " + KEY_EPISODE_EPISODE + "=? " +
						"AND " + KEY_EPISODE_SEASON_ID + " " +
						"IN (SELECT " + KEY_SEASON_URL + " " +
						"FROM " + SEASONS_TABLE + " " +
						"WHERE " + KEY_SEASON_TVSHOW_ID + "=?)", 
						new String[]{String.valueOf(season), String.valueOf(episode), tvdbId});
	}


	/************************** Other methods *******************************/

	public boolean isThereShows()
	{
		String sql = "SELECT * FROM " + TVSHOWS_TABLE;
		Cursor c = db.rawQuery(sql, null);
		return c.moveToFirst();
	}

	public boolean isThereMovies()
	{
		String sql = "SELECT * FROM " + MOVIES_TABLE;
		Cursor c = db.rawQuery(sql, null);
		return c.moveToFirst();
	}

	public TvShowEpisode getNextEpisode(String tvdbId)
	{
		String sql = 
				"SELECT " + SELECT_EPISODE + " " +
						"FROM " + EPISODES_TABLE + "," + SEASONS_TABLE + " " +
						"WHERE " + SEASONS_TABLE+"."+KEY_SEASON_URL + "=" + KEY_EPISODE_SEASON_ID + " " +
						"AND " + SEASONS_TABLE+"."+KEY_SEASON_SEASON + "!=? " +
						"AND " + KEY_EPISODE_WATCHED + "=? " + 
						"AND " + KEY_SEASON_TVSHOW_ID + "=? " +
						"ORDER BY " + EPISODES_TABLE+"."+KEY_EPISODE_SEASON + "," + KEY_EPISODE_EPISODE + " ASC LIMIT 1";

		Cursor c = db.rawQuery(sql, new String[]{String.valueOf(0), String.valueOf(0), tvdbId});
		c.moveToFirst();

		TvShowEpisode tvEpisode = getEpisodeFromCursor(c, tvdbId);

		c.close();

		return tvEpisode;		
	}

	//refresh a show percentage (based on episodes watched, episodes not aired yet, specials episodes...)
	public int refreshPercentage(String tvdbId)
	{
		String sql = 
				"SELECT " + KEY_TVSHOW_EPISODES + "," + KEY_TVSHOW_EPISODES_WATCHED + " " +
						"FROM " + TVSHOWS_TABLE + " " +
						"WHERE " + KEY_TVSHOW_TVDB_ID + "=?";

		Cursor c = db.rawQuery(sql , new String[]{tvdbId});
		c.moveToFirst();

		int numberOfEpisodes = c.getInt(0);
		int numberOfEpisodesSeen = c.getInt(1);

		String sql2 = 
				"SELECT count(*) " +
						"FROM " + EPISODES_TABLE + "," + SEASONS_TABLE + " " +
						"WHERE " + SEASONS_TABLE+"."+KEY_SEASON_URL + "=" + KEY_EPISODE_SEASON_ID + " " + 
						"AND " + KEY_SEASON_TVSHOW_ID + "=? " +
						"AND " + SEASONS_TABLE+"."+KEY_SEASON_SEASON + "!=? " +
						"AND " + KEY_EPISODE_FIRST_AIRED + ">=?";

		c.close();

		Cursor c2 = db.rawQuery(sql2 , new String[]{tvdbId, String.valueOf(0), String.valueOf(new Date().getTime())});
		c2.moveToFirst();
		numberOfEpisodes -= c2.getInt(0);

		c2.close();

		int realPercentage = (int) ((numberOfEpisodesSeen*1.0/numberOfEpisodes*1.0)*100);
		realPercentage = (realPercentage > 100) ? 100 : ((realPercentage < 0) ? 0 : realPercentage);

		ContentValues cv = new ContentValues();
		cv.put(KEY_TVSHOW_PROGRESS, realPercentage);

		db.update(
				TVSHOWS_TABLE, 
				cv, 
				KEY_TVSHOW_TVDB_ID + "=?", 
				new String[]{tvdbId});

		return realPercentage;
	}

	public ArrayList<CalendarDate> getFutureEpisodes()
	{
		ArrayList<CalendarDate> episodes = new ArrayList<CalendarDate>();
		String sql = 
				"SELECT * " + 
						"FROM " + EPISODES_TABLE + ", " + SEASONS_TABLE + ", " + TVSHOWS_TABLE + " " +
						"WHERE " + KEY_EPISODE_SEASON_ID + "=" + SEASONS_TABLE+"."+KEY_SEASON_URL + " " +
						"AND " + KEY_SEASON_TVSHOW_ID + "=" + KEY_TVSHOW_TVDB_ID + " " +
						"AND " + EPISODES_TABLE+"."+KEY_EPISODE_FIRST_AIRED + ">=?" +  " " +
						"ORDER BY " + EPISODES_TABLE+"."+KEY_EPISODE_FIRST_AIRED;

		Cursor c = db.rawQuery(sql, new String[]{String.valueOf(new Date().getTime())});
		c.moveToFirst();

		CalendarDate cd = null;
		for(int i = 0; i < c.getCount(); i++)
		{
			TvShow s = new TvShow();

			s.airTime = c.getString(c.getColumnIndex(KEY_TVSHOW_AIR_TIME));
			s.title = c.getString(c.getColumnIndex(KEY_TVSHOW_TITLE));
			s.tvdbId = c.getString(c.getColumnIndex(KEY_TVSHOW_TVDB_ID));
			s.network = c.getString(c.getColumnIndex(KEY_TVSHOW_NETWORK));

			s.images = new Images();
			s.images.poster = c.getString(c.getColumnIndex(KEY_TVSHOW_POSTER));

			TvShowEpisode e = getEpisodeFromCursor(c, s.tvdbId);
			e.images.screen = null;

			if(cd == null || !Utils.isSameDay(cd.date, e.firstAired))
			{
				cd = new CalendarDate();
				cd.date = e.firstAired;
				cd.episodes = new ArrayList<CalendarTvShowEpisode>();
				episodes.add(cd);
			}

			CalendarTvShowEpisode cde = new CalendarTvShowEpisode();

			cde.show = s;
			cde.episode = e;
			cd.episodes.add(cde);

			c.moveToNext();
		}

		c.close();

		return episodes;
	}

	/************************** Movies methods *******************************/

	/**
	 *  Insert or update a movie
	 */
	public void insertOrUpdateMovie(Movie m) 
	{
		ContentValues values = new ContentValues();

		if(m.ratings != null)
			putNotNull(values, KEY_MOVIE_PERCENTAGE, m.ratings.percentage);

		if(m.rating != null)
			putNotNull(values, KEY_MOVIE_RATING, m.rating.toString());

		putNotNull(values, KEY_MOVIE_CERTIFICATION, m.certification);

		if(m.images != null)
		{
			putNotNull(values, KEY_MOVIE_FANART, m.images.fanart);
			putNotNull(values, KEY_MOVIE_POSTER, m.images.poster);
		}

		putNotNull(values, KEY_MOVIE_IMDB_ID, m.imdbId);
		putNotNull(values, KEY_MOVIE_OVERVIEW, m.overview);
		putNotNull(values, KEY_MOVIE_RUNTIME, m.runtime);
		putNotNull(values, KEY_MOVIE_TITLE, m.title);
		putNotNull(values, KEY_MOVIE_URL, m.url);

		if(m.year != null)
			putNotNull(values, KEY_MOVIE_YEAR, Integer.valueOf(m.year));

		putNotNull(values, KEY_MOVIE_IN_COLLECTION, m.inCollection);
		putNotNull(values, KEY_MOVIE_IN_WATCHLIST, m.inWatchlist);

		if(m.released != null)
			putNotNull(values, KEY_MOVIE_RELEASED, m.released.getTime());

		putNotNull(values, KEY_MOVIE_TAGLINE, m.tagline);
		putNotNull(values, KEY_MOVIE_TMDB_ID, m.tmdbId);
		putNotNull(values, KEY_MOVIE_TRAILER, m.trailer);
		putNotNull(values, KEY_MOVIE_WATCHED, m.watched);

		//TODO
		//putNotNull(values, KEY_MOVIE_LAST_UPDATED, m.);
		//putNotNull(values, KEY_MOVIE_RT_ID, m.);

		insertOrUpdate(MOVIES_TABLE, values, m.url);

		insertOrUpdateGenres(m.genres, m.url);
	}

	/**
	 *  Insert or update a list of MOVIE
	 */
	public void insertOrUpdateMovies(List<Movie> movies) 
	{
		for(Movie m : movies)
			insertOrUpdateMovie(m);
	}

	private Movie getMovieFromCursor(Cursor c)
	{
		Movie movie = new Movie();
		Images i = new Images();
		Ratings r = new Ratings();

		i.fanart = c.getString(COLUMN_MOVIE_FANART);
		i.poster = c.getString(COLUMN_MOVIE_POSTER);

		r.percentage = c.getInt(COLUMN_MOVIE_PERCENTAGE);

		movie.certification = c.getString(COLUMN_MOVIE_CERTIFICATION);
		movie.imdbId = c.getString(COLUMN_MOVIE_IMDB_ID);
		movie.inCollection = c.getInt(COLUMN_MOVIE_IN_COLLECTION) != 0;
		movie.inWatchlist = c.getInt(COLUMN_MOVIE_IN_WATCHLIST) != 0;
		movie.overview = c.getString(COLUMN_MOVIE_OVERVIEW);
		movie.runtime = c.getInt(COLUMN_MOVIE_RUNTIME);
		movie.rating = c.getString(COLUMN_MOVIE_RATING) == null ? null : Rating.fromValue(c.getString(COLUMN_MOVIE_RATING));
		movie.inWatchlist = c.getInt(COLUMN_MOVIE_IN_WATCHLIST) != 0;
		movie.images = i;
		movie.imdbId = c.getString(COLUMN_MOVIE_IMDB_ID);
		movie.ratings = r;		
		movie.released = new Date(c.getLong(COLUMN_MOVIE_RELEASED));
		movie.tagline = c.getString(COLUMN_MOVIE_TAGLINE);
		movie.title = c.getString(COLUMN_MOVIE_TITLE);
		movie.tmdbId = c.getString(COLUMN_MOVIE_TMDB_ID);
		movie.trailer = c.getString(COLUMN_MOVIE_TRAILER);
		movie.url = c.getString(COLUMN_MOVIE_URL);
		movie.watched = c.getInt(COLUMN_MOVIE_WATCHED) != 0;
		movie.year = c.getInt(COLUMN_MOVIE_YEAR);

		movie.genres = getGenres(movie.url);

		return movie;
	}

	public List<Movie> getMovies()
	{
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = db.rawQuery(
				"SELECT * " +
						"FROM " + MOVIES_TABLE + " " +
						"ORDER BY " + KEY_MOVIE_TITLE, 
						null);
		c.moveToFirst();
		for(int i = 0; i < c.getCount(); i++)
		{
			movies.add(getMovieFromCursor(c));
			c.moveToNext();
		}

		c.close();

		Collections.sort(movies);

		return movies;
	}

	public Movie getMovie(String url)
	{
		Cursor c = db.rawQuery(
				"SELECT * " +
						"FROM " + MOVIES_TABLE + " " +
						"WHERE " + KEY_MOVIE_URL + "=?", 
						new String[]{url});

		Movie movie = null;

		if(c.moveToFirst())
			movie = getMovieFromCursor(c);

		c.close();

		return movie;
	}

	public void removeMovie(String url)
	{
		db.delete(
				MOVIES_TABLE,
				KEY_MOVIE_URL + "=?",
				new String[]{url});

		removeGenres(url);
	}

	public boolean movieExist(String imbdId)
	{
		Cursor c = db.rawQuery(
				"SELECT " + KEY_MOVIE_URL + " " +
						"FROM " + MOVIES_TABLE + " " +
						"WHERE " + KEY_MOVIE_IMDB_ID + "=?", 
						new String[]{imbdId});

		boolean exist = c.moveToFirst();

		c.close();

		return exist;
	}

	/************************** Genres methods *******************************/

	/**
	 *  Insert or update a genre
	 */
	public void insertOrUpdateGenre(String g, String url) 
	{
		ContentValues values = new ContentValues();

		putNotNull(values, KEY_GENRE_NAME, g);
		putNotNull(values, KEY_GENRE_URL, url);

		try
		{
			insertOrUpdate(GENRES_TABLE, values, null);
		}
		catch(SQLiteConstraintException e){}
	}

	/**
	 *  Insert or update a list of MOVIE
	 */
	public void insertOrUpdateGenres(List<String> genres, String url)  
	{
		for(String g : genres)
			insertOrUpdateGenre(g, url);
	}

	private String getGenreFromCursor(Cursor c)
	{
		return c.getString(COLUMN_GENRE_NAME);
	}

	public List<String> getGenres(String url)
	{
		List<String> genres = new ArrayList<String>();
		Cursor c = db.rawQuery(
				"SELECT * " + 
						"FROM " + GENRES_TABLE + " " +
						"WHERE " + KEY_GENRE_URL + "=?" +
						"ORDER BY " + KEY_GENRE_NAME, 
						new String[]{url});
		c.moveToFirst();
		for(int i = 0; i < c.getCount(); i++)
		{
			genres.add(getGenreFromCursor(c));
			Log.e("test test", "genre : " + genres.get(i));
			c.moveToNext();
		}

		c.close();

		return genres;
	}

	public void removeGenres(String url)
	{
		db.delete(
				GENRES_TABLE,
				KEY_GENRE_URL + "=?",
				new String[]{url});
	}
}
