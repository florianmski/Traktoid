package com.florianmski.tracktoid.data.database.utils;

import android.content.ContentValues;

import com.florianmski.tracktoid.utils.CVHelper;
import com.florianmski.tracktoid.utils.DateHelper;
import com.florianmski.tracktoid.data.database.columns.BaseEntityColumns;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.ImageColumns;
import com.florianmski.tracktoid.data.database.columns.MovieColumns;
import com.florianmski.tracktoid.data.database.columns.SeasonColumns;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.uwetrottmann.trakt.v2.entities.Airs;
import com.uwetrottmann.trakt.v2.entities.BaseEntity;
import com.uwetrottmann.trakt.v2.entities.BaseIds;
import com.uwetrottmann.trakt.v2.entities.Episode;
import com.uwetrottmann.trakt.v2.entities.EpisodeIds;
import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.MoreImageSizes;
import com.uwetrottmann.trakt.v2.entities.Movie;
import com.uwetrottmann.trakt.v2.entities.MovieIds;
import com.uwetrottmann.trakt.v2.entities.Season;
import com.uwetrottmann.trakt.v2.entities.SeasonIds;
import com.uwetrottmann.trakt.v2.entities.Show;
import com.uwetrottmann.trakt.v2.entities.ShowIds;

public class CVUtils
{
    public static ContentValues packShow(Show show)
    {
        return new ShowCV(show).getCV();
    }

    public static ContentValues packSeason(Season season, String showId)
    {
        return new SeasonCV(new SeasonCV.SeasonEntity(season), showId).getCV();
    }

    public static ContentValues packEpisode(Episode episode, String seasonId, String showId)
    {
        return new EpisodeCV(episode, seasonId, showId).getCV();
    }

    public static ContentValues packMovie(Movie movie)
    {
        return new MovieCV(movie).getCV();
    }

    public static class BaseCV<E extends BaseEntity, I extends BaseIds>
    {
        protected CVHelper cvHelper;
        protected E entity;
        protected I ids;

        private BaseCV(I ids)
        {
            this.cvHelper = new CVHelper();
            this.ids = ids;
        }

        protected BaseCV(E entity, I ids)
        {
            this(ids);
            this.entity = entity;
        }

        protected ContentValues getCV()
        {
            packEntity();
            packIds();
            packImages(entity.images);
            return cvHelper.get();
        }

        protected void packEntity()
        {
            cvHelper.put(BaseEntityColumns.TITLE, entity.title)
                    .put(BaseEntityColumns.VOTES, entity.votes)
                    .put(BaseEntityColumns.UPDATED_AT, DateHelper.getTimestamp(entity.updated_at));
        }

        protected void packIds()
        {
            if(ids != null)
            {
                cvHelper.put(ShowColumns.ID_IMDB, ids.imdb)
                        .put(ShowColumns.ID_TMDB, ids.tmdb)
                        .put(ShowColumns.ID_TRAKT, ids.trakt);
            }
        }

        protected void packImages(Images images)
        {
            if(images != null)
            {
                MoreImageSizes fanart = images.fanart;
                if(fanart != null)
                {
                    cvHelper.put(ImageColumns.FANART_FULL, fanart.full)
                            .put(ImageColumns.FANART_MEDIUM, fanart.medium)
                            .put(ImageColumns.FANART_THUMB, fanart.thumb);
                }

                MoreImageSizes poster = images.poster;
                if(poster != null)
                {
                    cvHelper.put(ImageColumns.POSTER_FULL, images.poster.full)
                            .put(ImageColumns.POSTER_MEDIUM, images.poster.medium)
                            .put(ImageColumns.POSTER_THUMB, images.poster.thumb);
                }

                MoreImageSizes screenshot = images.screenshot;
                if(screenshot != null)
                {
                    cvHelper.put(ImageColumns.SCREENSHOT_FULL, images.screenshot.full)
                            .put(ImageColumns.SCREENSHOT_MEDIUM, images.screenshot.medium)
                            .put(ImageColumns.SCREENSHOT_THUMB, images.screenshot.thumb);
                }

                if(images.banner != null)
                    cvHelper.put(ImageColumns.BANNER, images.banner.full);
                if(images.clearart != null)
                    cvHelper.put(ImageColumns.CLEARART, images.clearart.full);
                if(images.logo != null)
                    cvHelper.put(ImageColumns.LOGO, images.logo.full);
                if(images.thumb != null)
                    cvHelper.put(ImageColumns.THUMB, images.thumb.full);
            }
        }
    }

    public static class ShowCV extends BaseCV<Show, ShowIds>
    {
        private ShowCV(Show entity)
        {
            super(entity, entity.ids);
        }

        @Override
        protected void packEntity()
        {
            super.packEntity();

            cvHelper.put(ShowColumns.CERTIFICATION, entity.certification)
                    .put(ShowColumns.COUNTRY, entity.country)
                    .put(ShowColumns.FIRST_AIRED, DateHelper.getTimestamp(entity.first_aired))
                    .put(ShowColumns.GENRES, entity.genres)
                    .put(ShowColumns.HOMEPAGE, entity.homepage)
                    .put(ShowColumns.LANGUAGE, entity.language)
                    .put(ShowColumns.NETWORK, entity.network)
                    .put(ShowColumns.OVERVIEW, entity.overview)
                    .put(ShowColumns.RUNTIME, entity.runtime)
                    .put(ShowColumns.STATUS, entity.status != null ? entity.status.toString() : null)
                    .put(ShowColumns.TRAILER, entity.trailer)
                    .put(ShowColumns.YEAR, entity.year);

            Airs airs = entity.airs;
            if(airs != null)
            {
                cvHelper.put(ShowColumns.AIR_DAY, airs.day)
                        .put(ShowColumns.AIR_TIME, airs.time)
                        .put(ShowColumns.AIR_TIMEZONE, airs.timezone);
            }

            cvHelper.put(ShowColumns.PUBLIC_RATING, entity.rating);
        }

        @Override
        protected void packIds()
        {
            super.packIds();

            cvHelper.put(ShowColumns.ID_SLUG, ids.slug)
                    .put(ShowColumns.ID_TVDB, ids.tvdb)
                    .put(ShowColumns.ID_TVRAGE, ids.tvrage);
        }
    }

    public static class SeasonCV extends BaseCV<SeasonCV.SeasonEntity, SeasonCV.SeasonEntityIds>
    {
        private String showId;

        protected SeasonCV(SeasonEntity entity, String showId)
        {
            super(entity, new SeasonEntityIds(entity.season.ids));
            setParentId(showId);
        }

        private void setParentId(String showId)
        {
            this.showId = showId;
        }

        public static class SeasonEntity extends BaseEntity
        {
            public Season season;

            public SeasonEntity(Season season)
            {
                this.season = season;

                this.title = season.number == 0 ? "Specials" : "Season " + season.number;
                //                TODO not in the API atm this.updated_at = season.updated_at;
                this.images = season.images;
            }
        }

        public static class SeasonEntityIds extends BaseIds
        {
            private Integer tvdb;
            private Integer tvrage;

            public SeasonEntityIds(SeasonIds ids)
            {
                this.tmdb = ids.tmdb;
                this.trakt = ids.trakt;
                this.tvdb = ids.tvdb;
                this.tvrage = ids.tvrage;
            }
        }

        @Override
        protected void packEntity()
        {
            // do not call super, season do not have a title or a updated_at field
            //            super.packEntity();

            cvHelper.put(SeasonColumns.NUMBER, entity.season.number)
                    .put(SeasonColumns.OVERVIEW, entity.season.overview)
                    .put(SeasonColumns.SHOW_ID, showId);

            cvHelper.put(SeasonColumns.PUBLIC_RATING, entity.season.rating)
                    .put(EpisodeColumns.VOTES, entity.season.votes);
        }

        @Override
        protected void packIds()
        {
            //            super.packIds();

            cvHelper.put(SeasonColumns.ID_TMDB, ids.tmdb)
                    .put(SeasonColumns.ID_TRAKT, ids.trakt)
                    .put(SeasonColumns.ID_TVDB, ids.tvdb)
                    .put(ShowColumns.ID_TVRAGE, ids.tvrage);
        }
    }

    public static class EpisodeCV extends BaseCV<Episode, EpisodeIds>
    {
        private String seasonId, showId;

        protected EpisodeCV(Episode entity, String seasonId, String showId)
        {
            super(entity, entity.ids);
            setParentIds(seasonId, showId);
        }

        private void setParentIds(String seasonId, String showId)
        {
            this.seasonId = seasonId;
            this.showId = showId;
        }

        @Override
        protected void packEntity()
        {
            super.packEntity();

            cvHelper.put(EpisodeColumns.FIRST_AIRED, DateHelper.getTimestamp(entity.first_aired))
                    .put(EpisodeColumns.NUMBER, entity.number)
                    .put(EpisodeColumns.NUMBER_ABS, entity.number_abs)
                    .put(EpisodeColumns.OVERVIEW, entity.overview)
                    .put(EpisodeColumns.SEASON, entity.season)
                    .put(EpisodeColumns.SEASON_ID, seasonId)
                    .put(EpisodeColumns.SHOW_ID, showId);

            cvHelper.put(EpisodeColumns.PUBLIC_RATING, entity.rating);
        }

        @Override
        protected void packIds()
        {
            super.packIds();

            cvHelper.put(EpisodeColumns.ID_TVDB, ids.tvdb)
                    .put(EpisodeColumns.ID_TVRAGE, ids.tvrage);
        }
    }

    public static class MovieCV extends BaseCV<Movie, MovieIds>
    {
        protected MovieCV(Movie entity)
        {
            super(entity, entity.ids);
        }

        @Override
        protected void packEntity()
        {
            super.packEntity();

            cvHelper
                    .put(MovieColumns.CERTIFICATION, entity.certification)
                    .put(MovieColumns.GENRES, entity.genres)
                    .put(MovieColumns.HOMEPAGE, entity.homepage)
                    .put(MovieColumns.LANGUAGE, entity.language)
                    .put(MovieColumns.OVERVIEW, entity.overview)
                    .put(MovieColumns.RELEASED, DateHelper.getTimestamp(entity.released))
                    .put(MovieColumns.RUNTIME, entity.runtime)
                    .put(MovieColumns.TAGLINE, entity.tagline)
                    .put(MovieColumns.TRAILER, entity.trailer)
                    .put(MovieColumns.YEAR, entity.year);

            cvHelper.put(MovieColumns.PUBLIC_RATING, entity.rating);
        }

        @Override
        protected void packIds()
        {
            super.packIds();

            cvHelper.put(MovieColumns.ID_SLUG, ids.slug);
        }
    }
}
