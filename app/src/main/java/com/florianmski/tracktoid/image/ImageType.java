package com.florianmski.tracktoid.image;

import android.text.TextUtils;

import com.uwetrottmann.trakt.v2.entities.Images;
import com.uwetrottmann.trakt.v2.entities.MoreImageSizes;

import java.util.SortedSet;
import java.util.TreeSet;

public abstract class ImageType
{
    private final static String DEFAULT_URL = null;

    protected Size targetSize;
    protected Images images;

    protected abstract TraktImageSizes getSizes();
    protected abstract MoreImageSizes getChosenImages();

    public ImageType(Size targetSize, Images images)
    {
        this.targetSize = targetSize;
        this.images = images;
    }

    public String get()
    {
        return makeDecision(getChosenImages());
    }

    // TODO implement an intelligent system that get the best images considering
    // what we have in cache, user connection, size of screen and other factors
    protected String makeDecision(MoreImageSizes images)
    {
        if(images == null)
            return DEFAULT_URL;

        Size bestSize = getSizes().getBestSize(targetSize);
//        Log.e("test", "best size : " + bestSize.name);
//        Log.e("test", "target size : " + targetSize.width+"x"+targetSize.height);
//        Log.e("test", "best size : " + bestSize.width+"x"+bestSize.height);

        // TODO what if one of those urls is null?
//        switch(bestSize.name)
//        {
//            case FULL:
//            default:
//                return images.full;
//            case MEDIUM:
//                return images.medium;
//            case THUMB:
//                return images.thumb;
//        }

        // TODO return medium for now, doesn't seem to work
        return images.thumb;
    }

    protected boolean check(MoreImageSizes images)
    {
        return images != null
                && (!TextUtils.isEmpty(images.full)
                || !TextUtils.isEmpty(images.medium)
                || !TextUtils.isEmpty(images.thumb));
    }

    protected class TraktImageSizes
    {
        private SortedSet<Size> sizes = new TreeSet<>();

        public TraktImageSizes add(TraktSize traktSize, int width, int height)
        {
            sizes.add(new Size(traktSize, width, height));
            return this;
        }

        public Size getBestSize(Size targetSize)
        {
            for(Size size : sizes)
            {
                if(size.compareTo(targetSize) >= 0)
                    return size;
            }

            // by default get the highest res
            return sizes.last();
        }
    }

    protected static class Size implements Comparable<Size>
    {
        public TraktSize name;
        public int width;
        public int height;

        public Size(TraktSize name, int width, int height)
        {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public Size(int width, int height)
        {
            this(null, width, height);
        }

        @Override
        public int compareTo(Size another)
        {
            // TODO this only works if the ratio is the same
            if(height == another.height && width == another.height)
                return 0;
            else if(height >= another.height && width >= another.height)
                return 1;
            return -1;
        }
    }

    public enum TraktSize
    {
        FULL("full"),
        MEDIUM("medium"),
        THUMB("thumb");

        private String name;

        TraktSize(String name)
        {
            this.name = name;
        }
    }
}
