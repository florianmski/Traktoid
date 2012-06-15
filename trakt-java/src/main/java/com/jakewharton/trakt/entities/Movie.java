package com.jakewharton.trakt.entities;

import java.util.Date;

import com.florianmski.traktoid.TraktoidInterface;
import com.google.gson.annotations.SerializedName;
import com.jakewharton.trakt.TraktEntity;

public class Movie extends MediaBase implements TraktEntity, TraktoidInterface<Movie> {
    private static final long serialVersionUID = -1543214252495012419L;

    @SerializedName("tmdb_id") public String tmdbId;
    public Integer plays;
    @SerializedName("in_collection") public Boolean inCollection;
    public Date released;
    public String trailer;
    public Integer runtime;
    public String tagline;
    public String overview;
    public String certification; //TODO make enum
    public Boolean watched;

    /** @deprecated Use {@link #tmdbId} */
    @Deprecated
    public String getTmdbId() {
        return this.tmdbId;
    }
    /** @deprecated Use {@link #plays} */
    @Deprecated
    public Integer getPlays() {
        return this.plays;
    }
    /** @deprecated Use {@link #inCollection} */
    @Deprecated
    public Boolean getInCollection() {
        return this.inCollection;
    }
    /** @deprecated Use {@link #released} */
    @Deprecated
    public Date getReleased() {
        return this.released;
    }
    /** @deprecated Use {@link #trailer} */
    @Deprecated
    public String getTrailer() {
        return this.trailer;
    }
    /** @deprecated Use {@link #runtime} */
    @Deprecated
    public Integer getRuntime() {
        return this.runtime;
    }
    /** @deprecated Use {@link #tagline} */
    @Deprecated
    public String getTagline() {
        return this.tagline;
    }
    /** @deprecated Use {@link #overview} */
    @Deprecated
    public String getOverview() {
        return this.overview;
    }
    /** @deprecated Use {@link #certification} */
    @Deprecated
    public String getCertification() {
        return this.certification;
    }
    /** @deprecated Use {@link #watched} */
    @Deprecated
    public Boolean getWatched() {
        return this.watched;
    }
	@Override
	public Date getFirstAired() {
		return this.released;
	}
	@Override
	public boolean isWatched() {
		return this.watched == null ? false : this.watched;
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
		return this.imdbId;
	}
	
	@Override
	public int compareTo(Movie m) {
		if(this.url.equals(m.url))
			return 0;
		else
		{
			String thisTitle = this.title;
			String sTitle = m.title;

			if(this.title.startsWith("The "))
				thisTitle = thisTitle.replace("The ", "");
			else if(this.title.startsWith("A "))
				thisTitle = thisTitle.replace("A ", "");
			else if(this.title.startsWith("An "))
				thisTitle = thisTitle.replace("An ", "");

			if(m.title.startsWith("The "))
				sTitle = sTitle.replace("The ", "");
			else if(m.title.startsWith("A "))
				sTitle = sTitle.replace("A ", "");
			else if(m.title.startsWith("An "))
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
	    if(!(o instanceof Movie)) 
	    	return false;

	    Movie other = (Movie) o;
	    return this.url.equals(other.url);
	}
}
