package com.jakewharton.trakt.entities;

import java.util.Date;
import java.util.List;

import com.florianmski.traktoid.TraktoidInterface;
import com.google.gson.annotations.SerializedName;
import com.jakewharton.trakt.TraktEntity;
import com.jakewharton.trakt.enumerations.DayOfTheWeek;
import com.jakewharton.trakt.enumerations.Rating;

public class TvShow extends MediaBase implements TraktEntity, TraktoidInterface<TvShow> {
	private static final long serialVersionUID = 862473930551420996L;

	@SerializedName("first_aired") public Date firstAired;
	public String country;
	public String overview;
	public Integer runtime;
	public String network;
	@SerializedName("air_day") public DayOfTheWeek airDay;
	@SerializedName("air_time") public String airTime;
	public String certification; //TODO: enum
	@SerializedName("tvdb_id") public String tvdbId;
	@SerializedName("tvrage_id") public String tvrageId;
	public List<TvShowEpisode> episodes;
	@SerializedName("top_episodes") public List<TvShowEpisode> topEpisodes;
	public List<TvShowSeason> seasons;
	public int progress;
	public Boolean inCollection;

	/** @deprecated Use {@link #firstAired} */
	@Deprecated
	public Date getFirstAired() {
		return this.firstAired;
	}
	/** @deprecated Use {@link #country} */
	@Deprecated
	public String getCountry() {
		return this.country;
	}
	/** @deprecated Use {@link #overview} */
	@Deprecated
	public String getOverview() {
		return this.overview;
	}
	/** @deprecated Use {@link #runtime} */
	@Deprecated
	public Integer getRuntime() {
		return this.runtime;
	}
	/** @deprecated Use {@link #network} */
	@Deprecated
	public String getNetwork() {
		return this.network;
	}
	/** @deprecated Use {@link #airDay} */
	@Deprecated
	public DayOfTheWeek getAirDay() {
		return this.airDay;
	}
	/** @deprecated Use {@link #airTime} */
	@Deprecated
	public String getAirTime() {
		return this.airTime;
	}
	/** @deprecated Use {@link #certification} */
	@Deprecated
	public String getCertification() {
		return this.certification;
	}
	/** @deprecated Use {@link #tvdbId} */
	@Deprecated
	public String getTvdbId() {
		return this.tvdbId;
	}
	/** @deprecated Use {@link #tvrageId} */
	@Deprecated
	public String getTvRageId() {
		return this.tvrageId;
	}
	/** @deprecated Use {@link #episodes} */
	@Deprecated
	public List<TvShowEpisode> getEpisodes() {
		return this.episodes;
	}
	/** @deprecated Use {@link #topEpisodes} */
	@Deprecated
	public List<TvShowEpisode> getTopEpisodes() {
		return this.topEpisodes;
	}
	/** @deprecated Use {@link #seasons} */
	@Deprecated
	public List<TvShowSeason> getSeasons() {
		return this.seasons;
	}
	@Override
	public Ratings getRatings() {
		return this.ratings;
	}
	@Override
	public Rating getRating() {
		return this.rating;
	}
	@Override
	public boolean isWatched() {
		return this.progress == 100;
	}
	@Override
	public boolean isInWatchlist() {
		return this.inWatchlist == null ? false : this.inWatchlist;
	}
	@Override
	public boolean isInCollection() {
		return this.inCollection == null ? false : this.inCollection;
	}
	@Override
	public Images getImages() {
		return this.images;
	}
	@Override
	public String getId() {
		return this.tvdbId;
	}
	
	@Override
	public int compareTo(TvShow s) 
	{
		if(this.url.equals(s.url))
			return 0;
		else
		{
			String thisTitle = this.title;
			String sTitle = s.title;

			if(this.title.startsWith("The "))
				thisTitle = thisTitle.replace("The ", "");
			else if(this.title.startsWith("A "))
				thisTitle = thisTitle.replace("A ", "");
			else if(this.title.startsWith("An "))
				thisTitle = thisTitle.replace("An ", "");

			if(s.title.startsWith("The "))
				sTitle = sTitle.replace("The ", "");
			else if(s.title.startsWith("A "))
				sTitle = sTitle.replace("A ", "");
			else if(s.title.startsWith("An "))
				sTitle = sTitle.replace("An ", "");

			return(thisTitle.compareTo(sTitle));
		}
	}
	@Override
	public int hashCode() {
		return this.url.hashCode();
	}
	@Override
	public boolean equals(Object o) {
	    if(o == null)                
	    	return false;
	    if(!(o instanceof TvShow)) 
	    	return false;

	    TvShow other = (TvShow) o;
	    return this.url.equals(other.url);
	}
}
