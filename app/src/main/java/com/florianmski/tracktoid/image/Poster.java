package com.florianmski.tracktoid.image;

import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.MoreImageSizes;

public class Poster extends ImageType
{
    public Poster(Size targetSize, Images images)
    {
        super(targetSize, images);
    }

    @Override
    protected TraktImageSizes getSizes()
    {
        return new TraktImageSizes()
                .add(TraktSize.FULL, 1000, 1500)
                .add(TraktSize.MEDIUM, 600, 900)
                .add(TraktSize.THUMB, 300, 450);
    }

    @Override
    protected MoreImageSizes getChosenImages()
    {
        return images.poster;
    }
}
