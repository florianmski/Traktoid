package com.jakewharton.trakt.entities;

import java.util.Date;
import java.util.List;

import com.jakewharton.trakt.TraktEntity;

public class Update implements TraktEntity {
	private static final long serialVersionUID = 3173582780588552743L;
	
	public static class Timestamps implements TraktEntity {
		private static final long serialVersionUID = 7566759237943367353L;
		
		public Date start;
        public Date current;
    }

    public Timestamps timestamps;
    public List<TvShow> shows;
    public List<Movie> movies;
}
