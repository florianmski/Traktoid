package com.florianmski.tracktoid.data;

import android.database.Cursor;

import com.florianmski.tracktoid.utils.CursorHelper;
import com.florianmski.tracktoid.data.database.columns.MovieColumns;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.Movie;
import com.uwetrottmann.trakt.v2.entities.MovieIds;

import java.util.ArrayList;
import java.util.List;

public class WMovie extends TraktoidItem<Movie, MovieIds>
{
    public WMovie(Movie movie)
    {
        super(movie, movie.ids);
    }

    public static WMovie unpack(Cursor cursor)
    {
        Movie movie = new Movie();
        movie.ids = new MovieIds();
        movie.images = new Images();

        CursorHelper ch = new CursorHelper(cursor);
        unpackEntity(ch, movie);
        unpackIds(ch, movie.ids);
        unpackImagesFanart(ch, movie.images);
        unpackImagesPoster(ch, movie.images);
        unpackImagesBanner(ch, movie.images);
        unpackImagesClearart(ch, movie.images);
        unpackImagesLogo(ch, movie.images);
        unpackImagesThumb(ch, movie.images);

        movie.certification = ch.getString(MovieColumns.CERTIFICATION);
        movie.genres        = ch.getStringList(ShowColumns.GENRES);
        movie.homepage      = ch.getString(MovieColumns.HOMEPAGE);
        movie.language      = ch.getString(MovieColumns.LANGUAGE);
        movie.overview      = ch.getString(MovieColumns.OVERVIEW);
        movie.released      = ch.getDate(MovieColumns.RELEASED);
        movie.runtime       = ch.getInt(MovieColumns.RUNTIME);
        movie.tagline       = ch.getString(MovieColumns.TAGLINE);
        movie.trailer       = ch.getString(MovieColumns.TRAILER);
        movie.year          = ch.getInt(MovieColumns.YEAR);

        movie.rating         = ch.getDouble(MovieColumns.PUBLIC_RATING);
        movie.votes          = ch.getInt(MovieColumns.VOTES);

        movie.ids.slug    = ch.getString(MovieColumns.ID_SLUG);

        WMovie wMovie = new WMovie(movie);
        wMovie.syncInfos = new SyncInfos();
        wMovie.syncInfos.collectedAt       = ch.getDate(MovieColumns.COLLECTED_AT);
        wMovie.syncInfos.ratedAt           = ch.getDate(MovieColumns.RATED_AT);
        wMovie.syncInfos.watchlistedAt     = ch.getDate(MovieColumns.WATCHLISTED_AT);
        wMovie.syncInfos.lastWatchedAt     = ch.getDate(MovieColumns.LAST_WATCHED_AT);

        wMovie.syncInfos.collected         = ch.getBoolean(MovieColumns.COLLECTED);
        wMovie.syncInfos.plays             = ch.getInt(MovieColumns.PLAYS);
        wMovie.syncInfos.rating            = ch.getRating(MovieColumns.RATING);
        wMovie.syncInfos.watched           = ch.getBoolean(MovieColumns.WATCHED);
        wMovie.syncInfos.watchlisted       = ch.getBoolean(MovieColumns.WATCHLISTED);

        return wMovie;
    }

    public static List<WMovie> unpackList(Cursor cursor)
    {
        List<WMovie> movies = new ArrayList<WMovie>();
        if(cursor.moveToFirst())
        {
            do
                movies.add(unpack(cursor));
            while(cursor.moveToNext());
        }
        return movies;
    }

    public static List<WMovie> toList(List<Movie> items)
    {
        List<WMovie> traktObjects = new ArrayList<WMovie>();
        for(Movie item : items)
            traktObjects.add(new WMovie(item));
        return traktObjects;
    }

    @Override
    public TraktBase getTraktBase()
    {
        return TraktBase.fromMovie(this);
    }
}
