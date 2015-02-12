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

public interface SeasonColumns
{
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String ID = BaseEntityColumns.ID;

    // season content

    String EPISODES_AIRED = "episodes_aired";

    @DataType(INTEGER)
    String NUMBER = "number";

    @DataType(TEXT)
    String OVERVIEW = "overview";

    @DataType(INTEGER) @References(table = DatabaseSchematic.SHOWS, column = ShowColumns.ID_TRAKT)
    String SHOW_ID = "show_id";

    // season user data

    String EPISODES_WATCHED = "episodes_watched";

    String EPISODES_COLLECTED = "episodes_collected";

    String LAST_COLLECTED_AT = "last_collected_at";

    String LAST_WATCHED_AT = SyncColumns.LAST_WATCHED_AT;

    @DataType(INTEGER) @DefaultValue("NULL")
    String RATED_AT = SyncColumns.RATED_AT;

    @DataType(INTEGER) @DefaultValue("NULL")
    String RATING = SyncColumns.RATING;

    String PLAYS = SyncColumns.PLAYS;

    @DataType(INTEGER) @DefaultValue("0")
    String WATCHLISTED = SyncColumns.WATCHLISTED;

    @DataType(INTEGER) @DefaultValue("NULL")
    String WATCHLISTED_AT = SyncColumns.WATCHLISTED_AT;

    // season ratings

    @DataType(REAL) @DefaultValue("0")
    String PUBLIC_RATING = "public_rating";

    @DataType(INTEGER) @DefaultValue("0")
    String VOTES = "votes";

    // season ids

    @Unique @DataType(INTEGER)
    String ID_TRAKT = IdColumns.TRAKT;

    @DataType(INTEGER)
    String ID_TMDB = IdColumns.TMDB;

    @DataType(INTEGER)
    String ID_TVDB = IdColumns.TVDB;

    @DataType(INTEGER)
    String ID_TVRAGE = IdColumns.TVRAGE;

    // season images

    @DataType(TEXT)
    String IMAGE_POSTER_FULL = ImageColumns.POSTER_FULL;
    @DataType(TEXT)
    String IMAGE_POSTER_MEDIUM = ImageColumns.POSTER_MEDIUM;
    @DataType(TEXT)
    String IMAGE_POSTER_THUMB = ImageColumns.POSTER_THUMB;

    @DataType(TEXT)
    String IMAGE_THUMB = ImageColumns.THUMB;
}
