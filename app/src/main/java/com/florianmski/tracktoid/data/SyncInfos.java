package com.florianmski.tracktoid.data;

import com.uwetrottmann.trakt.v2.enums.Rating;

import org.joda.time.DateTime;

public class SyncInfos
{
    public boolean watched;
    public DateTime lastWatchedAt;
    public boolean collected;
    public DateTime collectedAt;
    public boolean watchlisted;
    public DateTime watchlistedAt;
    public Rating rating;
    public DateTime ratedAt;
    public Integer plays;
}
