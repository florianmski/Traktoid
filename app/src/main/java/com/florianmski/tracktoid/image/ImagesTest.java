package com.florianmski.tracktoid.image;

import android.content.Context;

import com.florianmski.tracktoid.ui.widgets.TraktImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.uwetrottmann.trakt.v2.entities.ImageSizes;
import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.MoreImageSizes;


public class ImagesTest
{
    protected Context context;

    public final static double RATIO_BANNER = 55.0/300.0;
    public final static double RATIO_FANART = 358.0/636.0;
    public final static double RATIO_POSTER = 1.471014493;
    public final static double RATIO_SCREENSHOT = 0.562893082;

    public static RequestCreator load(Context context, TraktImageView tiv, Images images)
    {
        return Picasso.with(context).load(getImageType(tiv, images).get());
    }

    private static ImageType getImageType(TraktImageView tiv, Images images)
    {
        ImageType.Size targetSize = new ImageType.Size(tiv.getMeasuredWidth(), tiv.getMeasuredHeight());

        switch(tiv.getType())
        {
            case AVATAR:
                break;
            case BANNER:
                break;
            case FANART:
                return new Fanart(targetSize, images);
            case HEADSHOT:
                break;
            case POSTER:
                return new Poster(targetSize, images);
            case SCREENSHOT:
                return new Screenshot(targetSize, images);
        }
        return null;
    }

    public static String getUrl(Type type, Images images)
    {
        switch(type)
        {
            case AVATAR:
                return getUrl(images.avatar);
            case BANNER:
                return getUrl(images.banner);
            case FANART:
                if(images.fanart == null)
                    return getUrl(images.screenshot);
                else
                    return getUrl(images.fanart);
            case HEADSHOT:
                return getUrl(images.headshot);
            case POSTER:
                return getUrl(images.poster);
            case SCREENSHOT:
                if(images.screenshot == null)
                    return getUrl(images.fanart);
                else
                    return getUrl(images.screenshot);
        }
        return null;
    }

    protected static String getUrl(ImageSizes imageSizes)
    {
        MoreImageSizes moreImageSizes = new MoreImageSizes();
        moreImageSizes.full = imageSizes.full;
        return getUrl(moreImageSizes);
    }

    protected static String getUrl(MoreImageSizes moreImageSizes)
    {
        if(moreImageSizes != null)
        {
            if(moreImageSizes.full != null)
                return moreImageSizes.full;
            else if(moreImageSizes.medium != null)
                return moreImageSizes.medium;
            else if(moreImageSizes.thumb != null)
                return moreImageSizes.thumb;
        }
        return null;
    }
}
