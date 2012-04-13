/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.image;

import com.jakewharton.trakt.entities.Images;

public class Image 
{
	private final static String tvdbUrl = "http://thetvdb.com/banners";
	private final static String tvdbSmallSize = "/_cache";
	private final static String tvdbBanner = "/graphical/%s-g.jpg";
	private final static String tvdbFanart = "/fanart/original/%s-1.jpg";
	private final static String tvdbPoster = "/posters/%s-1.jpg";
	private final static String tvdbSeason = "/seasons/%s-%s.jpg";
	
	public final static int BANNER = 0;
	public final static int FANART = 1;
	public final static int POSTER = 2;
	public final static int SEASON = 3;
	public final static int SCREEN = 4;
	public final static int CALENDAR = 5;
	
	public final static double RATIO_SCREEN = 0.562893082;
	public final static double RATIO_FANART = 358.0/636.0;
	public final static double RATIO_POSTER = 1.471014493;
	public final static double RATIO_BANNER = 55.0/300.0;
	
	private boolean smallSize = true;
	
	private String tvdb_id;
	private int imageType;
	private String url;
	private int season = -1;
	private int episode = -1;
	
	public static Image get(int type, Images images)
	{
		return new Image(type, images);
	}
	
	public Image(int type, Images images)
	{
		this.imageType = type;
		if(images != null)
		{
			switch(type)
			{
			case BANNER :
				//TODO
				this.url = getTraktImage(images.screen);
				break;
			case FANART :
				this.url = getTraktImage(images.fanart);
				break;
			case POSTER :
				this.url = getTraktImage(images.poster);
				break;
				//TODO
			case SEASON :
				this.url = getTraktImage(images.screen);
				break;
			case SCREEN :
				this.url = getTraktImage(images.screen);
				break;
				//TODO
			case CALENDAR :
				this.url = images.screen;
				break;
			}
		}
	}
	
	public Image(int type, String url)
	{
		this.imageType = type;
		this.url = url;
	}
	
	public Image size(boolean small)
	{
		this.smallSize = small;
		return this;
	}
	
	public Image(String tvdb_id, String traktURL, int imageType)
	{
		this.tvdb_id = tvdb_id;
		this.imageType = imageType;
		
		switch(imageType)
		{
//			case BANNER : 
//			case FANART : this.url = getTVDBImage(tvdb_id, imageType);
//				break;
			case BANNER : 
			case FANART :
			case CALENDAR :
			case POSTER : this.url = getTraktImage(traktURL, imageType);
				break;	
		}
		
	}
	
	/**
	 * For season poster ONLY
	 * 
	 * @param tvdb_id
	 * @param season
	 * @param smallSize
	 */
	public Image(String tvdb_id, int season)
	{
		this.tvdb_id = tvdb_id;
		this.imageType = SEASON;
		this.season = season;
		this.url = getSeasonPoster(tvdb_id, season);
	}
	
	/**
	 * For episode screen ONLY
	 * @param tvdb_id
	 * @param traktURL
	 * @param season
	 * @param episode
	 * @param smallSize
	 */
	public Image(String tvdb_id, String traktURL, int season, int episode)
	{
		this.tvdb_id = tvdb_id;
		this.imageType = SCREEN;
		this.season = season;
		this.episode = episode;
		this.url = getTraktImage(traktURL, imageType);
	}
	
	public Image()
	{
		
	}
	
	private String getTVDBImage(String tvdb_id, int imageType)
	{
		String url = tvdbUrl + (smallSize ? tvdbSmallSize : "");
		switch(imageType)
		{
			case BANNER : url = String.format(url += tvdbBanner, tvdb_id); break;
			case FANART : url = String.format(url += tvdbFanart, tvdb_id); break;
			case POSTER : url = String.format(url += tvdbPoster, tvdb_id); break;
		}
		return url;
	}
	
	private String getSeasonPoster(String tvdb_id, int season)
	{
		String url = tvdbUrl + (smallSize ? tvdbSmallSize : "");
		url = String.format(url += tvdbSeason, tvdb_id, season);
		return url;
	}
	
	private String getTraktImage(String traktURL, int imageType)
	{
		String url = traktURL;
		
		if(url == null)
			return null;
		
		if(smallSize)
		{
			switch(imageType)
			{
				case SCREEN :
				case CALENDAR :
				case FANART : url = traktURL.replace(".jpg", "-218.jpg"); break;
				case POSTER : url = traktURL.replace(".jpg", "-138.jpg"); break;
			}
		}
		return url;
	}
	
	private String getTraktImage(String traktURL)
	{
		String url = traktURL;
		
		if(url == null)
			return null;
		
		if(smallSize)
		{
			switch(imageType)
			{
				case SCREEN :
				case CALENDAR :
				case FANART : url = traktURL.replace(".jpg", "-218.jpg"); break;
				case POSTER : url = traktURL.replace(".jpg", "-138.jpg"); break;
			}
		}
		return url;
	}

	public String getTvdb_id() {
		return tvdb_id;
	}

	public int getImageType() {
		return imageType;
	}

	public boolean isSmallSize() {
		return smallSize;
	}

	public String getUrl() {
		return url;
	}

	public int getSeason() {
		return season;
	}

	public int getEpisode() {
		return episode;
	}

	public void setTvdb_id(String tvdbId) {
		tvdb_id = tvdbId;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}
}
