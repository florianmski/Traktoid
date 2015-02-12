package com.florianmski.tracktoid.image;

import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.MoreImageSizes;

public class Screenshot extends ImageType
{
    public Screenshot(Size targetSize, Images images)
    {
        super(targetSize, images);
    }

    @Override
    protected TraktImageSizes getSizes()
    {
        return new TraktImageSizes()
                .add(TraktSize.FULL, 1920, 1080)
                .add(TraktSize.MEDIUM, 1280, 720)
                .add(TraktSize.THUMB, 853, 480);
    }

    @Override
    protected MoreImageSizes getChosenImages()
    {
        return check(images.screenshot) ? images.screenshot : images.fanart;
    }
}