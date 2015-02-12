package com.florianmski.tracktoid.data.database.columns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface MovieColumns
{
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String ID = BaseEntityColumns.ID;

    // movie content

    @DataType(TEXT)
    String CERTIFICATION = "certification";

    @DataType(TEXT)
    String GENRES = "genres";

    @DataType(TEXT)
    String HOMEPAGE = "homepage";

    @DataType(TEXT)
    String LANGUAGE = "language";

    @DataType(TEXT)
    String OVERVIEW = "overview";

    @DataType(INTEGER)
    String RELEASED = "released";

    @DataType(INTEGER)
    String RUNTIME = "runtime";

    @DataType(TEXT)
    String TAGLINE = "tagline";

    @DataType(TEXT)
    String TITLE = BaseEntityColumns.TITLE;

    @DataType(TEXT)
    String TRAILER = "trailer";

    @DataType(INTEGER)
    String UPDATED_AT = BaseEntityColumns.UPDATED_AT;

    @DataType(INTEGER)
    String YEAR = "year";

    // movie user data

    @DataType(INTEGER) @DefaultValue("0")
    String COLLECTED = SyncColumns.COLLECTED;

    @DataType(INTEGER) @DefaultValue("NULL")
    String COLLECTED_AT = SyncColumns.COLLECTED_AT;

    @DataType(INTEGER) @DefaultValue("0")
    String HIDE = SyncColumns.HIDE;

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

    // movie ratings

    @DataType(REAL) @DefaultValue("0")
    String PUBLIC_RATING = "public_rating";

    @DataType(INTEGER) @DefaultValue("0")
    String VOTES = BaseEntityColumns.VOTES;

    // movie ids

    @DataType(TEXT)
    String ID_IMDB = IdColumns.IMDB;

    @DataType(TEXT)
    String ID_SLUG = IdColumns.SLUG;

    @Unique @DataType(INTEGER)
    String ID_TRAKT = IdColumns.TRAKT;

    @DataType(INTEGER)
    String ID_TMDB = IdColumns.TMDB;

    // movie images

    @DataType(TEXT)
    String IMAGE_FANART_FULL = ImageColumns.FANART_FULL;
    @DataType(TEXT)
    String IMAGE_FANART_MEDIUM = ImageColumns.FANART_MEDIUM;
    @DataType(TEXT)
    String IMAGE_FANART_THUMB = ImageColumns.FANART_THUMB;

    @DataType(TEXT)
    String IMAGE_POSTER_FULL = ImageColumns.POSTER_FULL;
    @DataType(TEXT)
    String IMAGE_POSTER_MEDIUM = ImageColumns.POSTER_MEDIUM;
    @DataType(TEXT)
    String IMAGE_POSTER_THUMB = ImageColumns.POSTER_THUMB;

    @DataType(TEXT)
    String IMAGE_BANNER = ImageColumns.BANNER;
    @DataType(TEXT)
    String IMAGE_CLEARART = ImageColumns.CLEARART;
    @DataType(TEXT)
    String IMAGE_LOGO = ImageColumns.LOGO;
    @DataType(TEXT)
    String IMAGE_THUMB = ImageColumns.THUMB;
}
