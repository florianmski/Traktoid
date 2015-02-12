package com.florianmski.tracktoid.data;


import com.florianmski.tracktoid.data.database.utils.CVUtils;
import com.uwetrottmann.trakt.v2.entities.BaseEntity;
import com.uwetrottmann.trakt.v2.entities.BaseIds;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.Movie;
import com.uwetrottmann.trakt.v2.entities.Season;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.enums.Rating;

import org.joda.time.DateTime;

public class TraktBase
{
    // item content
    public DateTime firstAired;
    public String homepage;
    public Images images;
    public String overview;
    public Integer runtime;
    public String title;
    public DateTime updatedAt;

    // item user data
    public boolean watched;
    public DateTime watchedAt;
    public boolean collected;
    public DateTime collectedAt;
    public boolean watchlisted;
    public DateTime watchlistedAt;
    public Rating rating;
    public DateTime ratedAt;
    public Integer plays;

    // item ratings
    public Double publicRating;
    public Integer votes;

    // item ids
    public String imdb;
    public String slug;
    public Integer trakt;
    public Integer tmdb;
    public Integer tvdb;
    public Integer tvrage;

    private TraktBase(BaseEntity traktItem, BaseIds ids)
    {
        this.images = traktItem.images;
        this.title = traktItem.title;
        this.updatedAt = traktItem.updated_at;

        this.imdb = ids.imdb;
        this.tmdb = ids.tmdb;
        this.trakt = ids.trakt;
    }

    private void setTraktoidItem(TraktoidItem traktoidItem)
    {
        // it's not a local item so we don't have any syncinfos available
        if(!traktoidItem.isLocal())
            return;

        // item user data
        this.watched = traktoidItem.syncInfos.watched;
        this.watchedAt = traktoidItem.syncInfos.lastWatchedAt;
        this.collected = traktoidItem.syncInfos.collected;
        this.collectedAt = traktoidItem.syncInfos.collectedAt;
        this.watchlisted = traktoidItem.syncInfos.watchlisted;
        this.watchlistedAt = traktoidItem.syncInfos.watchlistedAt;
        this.rating = traktoidItem.syncInfos.rating;
        this.ratedAt = traktoidItem.syncInfos.ratedAt;
        this.plays = traktoidItem.syncInfos.plays;
    }

    public static TraktBase fromShow(WShow wShow)
    {
        TraktBase traktBase = fromShow(wShow.getTraktItem());
        traktBase.setTraktoidItem(wShow);
        return traktBase;
    }

    public static TraktBase fromShow(Show show)
    {
        TraktBase traktBase = new TraktBase(show, show.ids);

        traktBase.firstAired = show.first_aired;
        traktBase.homepage = show.homepage;
        traktBase.overview = show.overview;
        traktBase.runtime = show.runtime;

        traktBase.slug = show.ids.slug;
        traktBase.tvdb = show.ids.tvdb;
        traktBase.tvrage = show.ids.tvrage;

        // item ratings
        traktBase.publicRating = show.rating;
        traktBase.votes = show.votes;

        return traktBase;
    }

    public static TraktBase fromSeason(WSeason wSeason)
    {
        TraktBase traktBase = fromSeason(wSeason.getTraktItem());
        traktBase.setTraktoidItem(wSeason);
        return traktBase;
    }

    public static TraktBase fromSeason(CVUtils.SeasonCV.SeasonEntity seasonEntity)
    {
        TraktBase traktBase = new TraktBase(seasonEntity, new CVUtils.SeasonCV.SeasonEntityIds(seasonEntity.season.ids));
        Season season = seasonEntity.season;

        //        traktBase.firstAired = season.first_aired;
        //        traktBase.homepage = season.homepage;
        traktBase.overview = season.overview;
        //        traktBase.runtime = season.runtime;

        traktBase.tvdb = season.ids.tvdb;

        // item ratings
        traktBase.publicRating = season.rating;
        traktBase.votes = season.votes;

        return traktBase;
    }

    public static TraktBase fromEpisode(WEpisode wEpisode)
    {
        TraktBase traktBase = fromEpisode(wEpisode.getTraktItem());
        traktBase.setTraktoidItem(wEpisode);
        return traktBase;
    }

    public static TraktBase fromEpisode(Episode episode)
    {
        TraktBase traktBase = new TraktBase(episode, episode.ids);
        traktBase.firstAired = episode.first_aired;
        //        traktBase.homepage = episode.homepage;
        traktBase.overview = episode.overview;
        //        traktBase.runtime = episode.runtime;

        traktBase.tvdb = episode.ids.tvdb;
        traktBase.tvrage = episode.ids.tvrage;

        // item ratings
        traktBase.publicRating = episode.rating;
        traktBase.votes = episode.votes;

        return traktBase;
    }

    public static TraktBase fromMovie(WMovie wMovie)
    {
        TraktBase traktBase = fromMovie(wMovie.getTraktItem());
        traktBase.setTraktoidItem(wMovie);
        return traktBase;
    }

    public static TraktBase fromMovie(Movie movie)
    {
        TraktBase traktBase = new TraktBase(movie, movie.ids);

        traktBase.firstAired = movie.released;
        traktBase.homepage = movie.homepage;
        traktBase.overview = movie.overview;
        traktBase.runtime = movie.runtime;

        traktBase.slug = movie.ids.slug;

        // item ratings
        traktBase.publicRating = movie.rating;
        traktBase.votes = movie.votes;

        return traktBase;
    }
}
