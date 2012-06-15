package com.jakewharton.trakt.entities;

import java.util.Calendar;
import java.util.Date;

import com.florianmski.traktoid.TraktoidInterface;
import com.google.gson.annotations.SerializedName;
import com.jakewharton.trakt.TraktEntity;
import com.jakewharton.trakt.enumerations.Rating;

public class TvShowEpisode implements TraktEntity, TraktoidInterface<TvShowEpisode> {
	private static final long serialVersionUID = -1550739539663499211L;

	public Integer season;
	public String seasonId;
	public Integer number;
	public String title;
	public String overview;
	public String url;
	@SerializedName("first_aired") public Date firstAired;
	public Calendar inserted;
	public Integer plays;
	public Images images;
	public Ratings ratings;
	public Boolean watched;
	public Rating rating;
	@SerializedName("in_watchlist") public Boolean inWatchlist;
	@SerializedName("in_collection") public Boolean inCollection;

	public String tvdbId;

	/** @deprecated Use {@link #season} */
	@Deprecated
	public Integer getSeason() {
		return this.season;
	}
	/** @deprecated Use {@link #number} */
	@Deprecated
	public Integer getNumber() {
		return this.number;
	}
	/** @deprecated Use {@link #title} */
	@Deprecated
	public String getTitle() {
		return this.title;
	}
	/** @deprecated Use {@link #overview} */
	@Deprecated
	public String getOverview() {
		return this.overview;
	}
	/** @deprecated Use {@link #url} */
	@Deprecated
	public String getUrl() {
		return this.url;
	}
	/** @deprecated Use {@link #firstAired} */
	@Deprecated
	public Date getFirstAired() {
		return this.firstAired;
	}
	/** @deprecated Use {@link #inserted} */
	@Deprecated
	public Calendar getInserted() {
		return this.inserted;
	}
	/** @deprecated Use {@link #plays} */
	@Deprecated
	public Integer getPlays() {
		return this.plays;
	}
	/** @deprecated Use {@link #images} */
	@Deprecated
	public Images getImages() {
		return this.images;
	}
	/** @deprecated Use {@link #ratings} */
	@Deprecated
	public Ratings getRatings() {
		return this.ratings;
	}
	/** @deprecated Use {@link #watched} */
	@Deprecated
	public Boolean getWatched() {
		return this.watched;
	}
	/** @deprecated Use {@link #rating} */
	@Deprecated
	public Rating getRating() {
		return this.rating;
	}
	/** @deprecated Use {@link #inWatchlist} */
	@Deprecated
	public Boolean getInWatchlist() {
		return this.inWatchlist;
	}
	@Override
	public boolean isWatched() {
		return watched == null ? false : watched;
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
	public String getId() {
		return this.url;
	}
	@Override
	public int compareTo(TvShowEpisode e) {
		if(this.url.equals(e.url))
			return 0;
		else
		{
			if(this.season > e.season)
				return 1;
			else if(this.season < e.season)
				return -1;
			else
			{
				if(this.number > e.number)
					return 1;
				else
					return -1;
			}
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
	    if(!(o instanceof TvShowEpisode)) 
	    	return false;

	    TvShowEpisode other = (TvShowEpisode) o;
	    return this.url.equals(other.url);
	}
}
