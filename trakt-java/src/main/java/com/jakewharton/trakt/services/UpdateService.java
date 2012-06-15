package com.jakewharton.trakt.services;

import com.google.gson.reflect.TypeToken;
import com.jakewharton.trakt.TraktApiBuilder;
import com.jakewharton.trakt.TraktApiService;
import com.jakewharton.trakt.entities.Update;

public class UpdateService extends TraktApiService {
	
	/**
     * Returns all movies updated since a timestamp. The server time is in PST. 
     * To establish a baseline timestamp, you can use the server/time method. 
     * It's recommended to store the timestamp so you can be efficient in using 
     * this method.
     *
     * @return Builder instance.
     */
    public MoviesBuilder movies() {
        return new MoviesBuilder(this);
    }

    /**
     * Returns all shows updated since a timestamp. The server time is in PST. 
     * To establish a baseline timestamp, you can use the server/time method. 
     * It's recommended to store the timestamp so you can be efficient in using 
     * this method.
     *
     * @return Builder instance.
     */
    public ShowsBuilder shows() {
        return new ShowsBuilder(this);
    }

    public static final class MoviesBuilder extends TraktApiBuilder<Update> {
        private static final String URI = "/movies/updated.json/" + FIELD_API_KEY + "/" + FIELD_TIMESTAMP;

        private MoviesBuilder(UpdateService service) {
            super(service, new TypeToken<Update>() {}, URI);
        }
        
        /**
         * Start with this timestamp and find anything updated since then.
         *
         * @param timestamp Value.
         * @return Builder instance.
         */
        public MoviesBuilder timestamp(long timestamp) {
            this.field(FIELD_TIMESTAMP, timestamp);
            return this;
        }
    }
    public static final class ShowsBuilder extends TraktApiBuilder<Update> {
        private static final String URI = "/shows/updated.json/" + FIELD_API_KEY + "/" + FIELD_TIMESTAMP;

        private ShowsBuilder(UpdateService service) {
            super(service, new TypeToken<Update>() {}, URI);
        }
        
        /**
         * Start with this timestamp and find anything updated since then.
         *
         * @param timestamp Value.
         * @return Builder instance.
         */
        public ShowsBuilder timestamp(long timestamp) {
            this.field(FIELD_TIMESTAMP, timestamp);
            return this;
        }
    }
}
