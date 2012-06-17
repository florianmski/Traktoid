package com.florianmski.tracktoid.image;

import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Images;


public class TraktImage 
{	
	public final static double RATIO_SCREEN = 0.562893082;
	public final static double RATIO_FANART = 358.0/636.0;
	public final static double RATIO_POSTER = 1.471014493;
	public final static double RATIO_BANNER = 55.0/300.0;

	private static Size size = Size.SMALL;

	private String url;
	private Type type;

	private TraktImage(String url, Type type, Size size)
	{
		this.url = url;
		this.type = type;
	}

	public static <T extends TraktoidInterface<T>> TraktImage getPoster(T traktItem)
	{
		return getImage(traktItem, Type.POSTER);
	}

	public static <T extends TraktoidInterface<T>> TraktImage getHeadshot(T traktItem)
	{
		return getImage(traktItem, Type.HEADSHOT);
	}

	public static <T extends TraktoidInterface<T>> TraktImage getScreen(T traktItem)
	{
		return getImage(traktItem, Type.SCREEN);
	}

	public static <T extends TraktoidInterface<T>> TraktImage getBanner(T traktItem)
	{
		return getImage(traktItem, Type.BANNER);
	}

	public static <T extends TraktoidInterface<T>> TraktImage getFanart(T traktItem)
	{
		return getImage(traktItem, Type.FANART);
	}

	private static <T extends TraktoidInterface<T>> TraktImage getImage(T traktItem, Type type)
	{
		return getImage(traktItem.getImages(), type);
	}

	private static <T extends TraktoidInterface<T>> TraktImage getImage(Images images, Type type)
	{
		String uncompressedImageUrl = getUrlByType(images, type);
		String finalUrl = getUrlBySize(uncompressedImageUrl, type, size);

		return new TraktImage(finalUrl, type, size);
	}

	private static String getUrlByType(Images images, Type type)
	{
		switch(type)
		{
		case BANNER:
			return images.banner;
		case FANART:
			if(images.fanart == null)
			{			
				type = Type.SCREEN;
				return images.screen;
			}
			else
				return images.fanart;
		case HEADSHOT:
			return images.headshot;
		case POSTER:
			return images.poster;
		case SCREEN:
			if(images.screen == null)
			{			
				type = Type.FANART;
				return images.fanart;
			}
			else
				return images.screen;
		}
		return null;
	}

	private static String getUrlBySize(String url, Type type, Size size)
	{
		String urlValue = null;

		if(url.contains("-940.jpg"))
			url = url.replace("-940.jpg", ".jpg");
		if(url.contains("-218.jpg"))
			url = url.replace("-218.jpg", ".jpg");
		if(url.contains("-300.jpg"))
			url = url.replace("-300.jpg", ".jpg");
		if(url.contains("-138.jpg"))
			url = url.replace("-138.jpg", ".jpg");

		switch(type)
		{
		case BANNER:
			switch(size)
			{
			case LARGE:
			case SMALL:
			case UNCOMRESSED:
				urlValue = "";
				break;
			}
			break;
		case FANART:
			switch(size)
			{
			case LARGE:
				urlValue = "-940";
				break;
			case SMALL:
				urlValue = "-218";
				break;
			case UNCOMRESSED:
				urlValue = "";
				break;
			}
			break;
		case HEADSHOT:
			switch(size)
			{
			case LARGE:
			case SMALL:
			case UNCOMRESSED:
				urlValue = "";
				break;
			}
			break;
		case POSTER:
			switch(size)
			{
			case LARGE:
				urlValue = "-300";
				break;
			case SMALL:
				urlValue = "-138";
				break;
			case UNCOMRESSED:
				urlValue = "";
				break;
			}
			break;
		case SCREEN:
			switch(size)
			{
			case SMALL:
				urlValue = "-218";
				break;
			case LARGE:
			case UNCOMRESSED:
				urlValue = "";
				break;
			}
			break;
		}

		if(urlValue != null && url != null)
			return url.replace(".jpg", urlValue+".jpg");
		else
			return url;
	}

	public static void setSize(Size size)
	{
		TraktImage.size = size;
	}

	public String getUrl()
	{
		return url;
	}
}
