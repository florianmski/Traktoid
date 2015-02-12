package com.florianmski.tracktoid.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.MovieColumns;
import com.florianmski.tracktoid.data.database.columns.SeasonColumns;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

@Database(version = DatabaseSchematic.VERSION, fileName = DatabaseSchematic.FILE_NAME, className = "GeneratedDatabase", packageName = "com.florianmski.tracktoid.data.provider")
public final class DatabaseSchematic
{
    private DatabaseSchematic()
    {

    }

    public static final int VERSION = 1;
    public static final String FILE_NAME = "traktoid.db";

    // Tables

    @Table(ShowColumns.class)
    public static final String SHOWS = "shows";
    @Table(SeasonColumns.class)
    public static final String SEASONS = "seasons";
    @Table(EpisodeColumns.class)
    public static final String EPISODES = "episodes";
    @Table(MovieColumns.class)
    public static final String MOVIES = "movies";

    // Indexes

    // indexes for the shows table
    @ExecOnCreate
    public static String indexShowsId = createIndex(DatabaseSchematic.SHOWS, ShowColumns.ID_TRAKT);
    @ExecOnCreate
    public static String indexShowsTitle = createIndex(DatabaseSchematic.SHOWS, ShowColumns.TITLE);

    // indexes for the seasons table
    @ExecOnCreate
    public static String indexSeasonsId = createIndex(DatabaseSchematic.SEASONS, SeasonColumns.ID_TRAKT);
    @ExecOnCreate
    public static String indexSeasonsShowId = createIndex(DatabaseSchematic.SEASONS, SeasonColumns.SHOW_ID);
    @ExecOnCreate
    public static String indexSeasonsSeason = createIndex(DatabaseSchematic.SEASONS, SeasonColumns.NUMBER);

    // indexes for the episodes table
    @ExecOnCreate
    public static String indexEpisodesId = createIndex(DatabaseSchematic.EPISODES, EpisodeColumns.ID_TRAKT);
    @ExecOnCreate
    public static String indexEpisodesSeason = createIndex(DatabaseSchematic.EPISODES, EpisodeColumns.SEASON);
    @ExecOnCreate
    public static String indexEpisodesShowId = createIndex(DatabaseSchematic.EPISODES, EpisodeColumns.SHOW_ID);
    @ExecOnCreate
    public static String indexEpisodesEpisode = createIndex(DatabaseSchematic.EPISODES, EpisodeColumns.SEASON, EpisodeColumns.NUMBER);

    // indexes for the movies table
    @ExecOnCreate
    public static String indexMoviesId = createIndex(DatabaseSchematic.MOVIES, MovieColumns.ID_TRAKT);
    @ExecOnCreate
    public static String indexMoviesTitle = createIndex(DatabaseSchematic.MOVIES, MovieColumns.TITLE);

    // Triggers

    // set watchlist = 0 on season and show if an episode is watched
    @ExecOnCreate
    public static String triggerWatchlist = "CREATE TRIGGER " + DatabaseSchematic.EPISODES + "_" + "update" + "_" + EpisodeColumns.WATCHED + " "
            + "BEFORE UPDATE OF " + EpisodeColumns.WATCHED + " ON " + DatabaseSchematic.EPISODES + " "
            + "WHEN " + "new." + EpisodeColumns.WATCHED + " = 1 "
            + "BEGIN "
            + "UPDATE " + DatabaseSchematic.SHOWS + " "
                + "SET " + ShowColumns.WATCHLISTED + " = 0 "
                + "WHERE " + ShowColumns.ID_TRAKT + " = old." + EpisodeColumns.SHOW_ID + "; "
            + "UPDATE " + DatabaseSchematic.SEASONS + " "
                + "SET " + SeasonColumns.WATCHLISTED + " = 0 "
                + "WHERE " + SeasonColumns.ID_TRAKT + " = old." + EpisodeColumns.SEASON_ID + "; "
            + "END" + ";";


    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db)
    {

    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private static String createIndex(String table, String... columns)
    {
        return "CREATE INDEX " + table + "_" + joinColumns("_", columns) + " on " + table + "(" + joinColumns(",", columns) + ");";
    }

    private static String joinColumns(String separator, String... columns)
    {
        String join = "";
        for(String column : columns)
            join = separator + column;
        return join.substring(1);
    }
}
