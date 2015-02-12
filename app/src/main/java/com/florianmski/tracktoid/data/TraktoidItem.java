package com.florianmski.tracktoid.data;

import com.florianmski.tracktoid.utils.CursorHelper;
import com.florianmski.tracktoid.data.database.columns.BaseEntityColumns;
import com.florianmski.tracktoid.data.database.columns.EpisodeColumns;
import com.florianmski.tracktoid.data.database.columns.ShowColumns;
import com.uwetrottmann.trakt.v2.entities.BaseEntity;
import com.uwetrottmann.trakt.v2.entities.BaseIds;
import com.uwetrottmann.trakt.v2.entities.ImageSizes;
import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.MoreImageSizes;

public abstract class TraktoidItem<E extends BaseEntity, I extends BaseIds>
{
    protected E traktItem;
    protected I ids;
    protected SyncInfos syncInfos;

    public TraktoidItem(E traktItem, I ids)
    {
        this.traktItem = traktItem;
        this.ids = ids;
    }

    public abstract TraktBase getTraktBase();

    public final E getTraktItem()
    {
        return traktItem;
    }

    public final I getIds()
    {
        return ids;
    }

    // if we have this item in db, syncInfos will not be null
    public boolean isLocal()
    {
        return syncInfos != null;
    }

    public final SyncInfos getSyncInfos()
    {
        return syncInfos;
    }

    protected static void unpackEntity(CursorHelper ch, BaseEntity baseEntity)
    {
        baseEntity.title        = ch.getString(BaseEntityColumns.TITLE);
        baseEntity.updated_at   = ch.getDate(BaseEntityColumns.UPDATED_AT);
    }

    protected static void unpackIds(CursorHelper ch, BaseIds ids)
    {
        ids.imdb    = ch.getString(ShowColumns.ID_IMDB);
        ids.tmdb    = ch.getInt(ShowColumns.ID_TMDB);
        ids.trakt   = ch.getInt(ShowColumns.ID_TRAKT);
    }

    protected static void unpackImagesFanart(CursorHelper ch, Images images)
    {
        MoreImageSizes fanart = new MoreImageSizes();
        fanart.full     = ch.getString(ShowColumns.IMAGE_FANART_FULL);
        fanart.medium   = ch.getString(ShowColumns.IMAGE_FANART_MEDIUM);
        fanart.thumb    = ch.getString(ShowColumns.IMAGE_FANART_THUMB);
        images.fanart   = fanart;
    }

    protected static void unpackImagesPoster(CursorHelper ch, Images images)
    {
        MoreImageSizes poster = new MoreImageSizes();
        poster.full     = ch.getString(ShowColumns.IMAGE_POSTER_FULL);
        poster.medium   = ch.getString(ShowColumns.IMAGE_POSTER_MEDIUM);
        poster.thumb    = ch.getString(ShowColumns.IMAGE_POSTER_THUMB);
        images.poster   = poster;
    }

    protected static void unpackImagesScreenshot(CursorHelper ch, Images images)
    {
        MoreImageSizes screenshot = new MoreImageSizes();
        screenshot.full     = ch.getString(EpisodeColumns.IMAGE_SCREENSHOT_FULL);
        screenshot.medium   = ch.getString(EpisodeColumns.IMAGE_SCREENSHOT_MEDIUM);
        screenshot.thumb    = ch.getString(EpisodeColumns.IMAGE_SCREENSHOT_THUMB);
        images.screenshot = screenshot;
    }

    protected static void unpackImagesBanner(CursorHelper ch, Images images)
    {
        ImageSizes banner = new ImageSizes();
        banner.full = ch.getString(ShowColumns.IMAGE_BANNER);
        images.banner = banner;
    }

    protected static void unpackImagesClearart(CursorHelper ch, Images images)
    {
        ImageSizes clearart = new ImageSizes();
        clearart.full = ch.getString(ShowColumns.IMAGE_CLEARART);
        images.clearart = clearart;
    }

    protected static void unpackImagesLogo(CursorHelper ch, Images images)
    {
        ImageSizes logo = new ImageSizes();
        logo.full = ch.getString(ShowColumns.IMAGE_LOGO);
        images.logo = logo;
    }

    protected static void unpackImagesThumb(CursorHelper ch, Images images)
    {
        ImageSizes thumb = new ImageSizes();
        thumb.full = ch.getString(ShowColumns.IMAGE_THUMB);
        images.thumb = thumb;
    }
}
