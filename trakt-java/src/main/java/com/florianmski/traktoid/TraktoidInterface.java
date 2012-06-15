package com.florianmski.traktoid;

import java.io.Serializable;
import java.util.Date;

import com.jakewharton.trakt.entities.Images;
import com.jakewharton.trakt.entities.Ratings;
import com.jakewharton.trakt.enumerations.Rating;

public interface TraktoidInterface<T> extends Serializable, Comparable<T>
{
	public Date getFirstAired();
	public Ratings getRatings();
	public Rating getRating();
	public boolean isWatched();
	public boolean isInWatchlist();
	public boolean isInCollection();
	public Images getImages();
	public String getOverview();
	public String getId();
	public String getTitle();
}
