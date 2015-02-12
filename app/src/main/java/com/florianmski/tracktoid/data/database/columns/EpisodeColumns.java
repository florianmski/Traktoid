package com.florianmski.tracktoid.data.database.columns;

import com.florianmski.tracktoid.data.database.DatabaseSchematic;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface EpisodeColumns
{
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String ID = BaseEntityColumns.ID;

    @DataType(INTEGER)
    String FIRST_AIRED = "first_aired";

    @DataType(INTEGER)
    String NUMBER = "number";

    @DataType(INTEGER)
    String NUMBER_ABS = "number_abs";

    @DataType(TEXT)
    String OVERVIEW = "overview";

    @DataType(INTEGER)
    String SEASON = "season";

    @DataType(INTEGER) @References(table = DatabaseSchematic.SEASONS, column = SeasonColumns.ID_TRAKT)
    String SEASON_ID = "season_id";

    @DataType(INTEGER) @References(table = DatabaseSchematic.SHOWS, column = ShowColumns.ID_TRAKT)
    String SHOW_ID = "show_id";

    @DataType(TEXT)
    String TITLE = BaseEntityColumns.TITLE;

    @DataType(INTEGER)
    String UPDATED_AT = BaseEntityColumns.UPDATED_AT;

    // episode ratings

    @DataType(REAL) @DefaultValue("0")
    String PUBLIC_RATING = "public_rating";

    @DataType(INTEGER) @DefaultValue("0")
    String VOTES = BaseEntityColumns.VOTES;

    // episode ids

    @DataType(TEXT)
    String ID_IMDB = IdColumns.IMDB;

    @Unique @DataType(INTEGER)
    String ID_TRAKT = IdColumns.TRAKT;

    @DataType(INTEGER)
    String ID_TMDB = IdColumns.TMDB;

    @DataType(INTEGER)
    String ID_TVDB = IdColumns.TVDB;

    @DataType(INTEGER)
    String ID_TVRAGE = IdColumns.TVRAGE;

    // episode user data

    @DataType(INTEGER) @DefaultValue("0")
    String COLLECTED = SyncColumns.COLLECTED;

    @DataType(INTEGER) @DefaultValue("NULL")
    String COLLECTED_AT = SyncColumns.COLLECTED_AT;

    @DataType(INTEGER) @DefaultValue("NULL")
    String LAST_WATCHED_AT = SyncColumns.LAST_WATCHED_AT;

    @DataType(INTEGER) @DefaultValue("0")
    String PLAYS = SyncColumns.PLAYS;

    @DataType(INTEGER) @DefaultValue("NULL")
    String RATED_AT = SyncColumns.RATED_AT;

    @DataType(INTEGER) @DefaultValue("NULL")
    String RATING = SyncColumns.RATING;

    @DataType(INTEGER) @DefaultValue("0")
    String WATCHED = SyncColumns.WATCHED;

    @DataType(INTEGER) @DefaultValue("0")
    String WATCHLISTED = SyncColumns.WATCHLISTED;

    @DataType(INTEGER) @DefaultValue("NULL")
    String WATCHLISTED_AT = SyncColumns.WATCHLISTED_AT;

    // episode images

    @DataType(TEXT)
    String IMAGE_SCREENSHOT_FULL = ImageColumns.SCREENSHOT_FULL;
    @DataType(TEXT)
    String IMAGE_SCREENSHOT_MEDIUM = ImageColumns.SCREENSHOT_MEDIUM;
    @DataType(TEXT)
    String IMAGE_SCREENSHOT_THUMB = ImageColumns.SCREENSHOT_THUMB;
}
