package com.florianmski.tracktoid.data.provider.util;

import java.util.HashMap;
import java.util.Map;

public enum Table 
{
	SHOWS("shows"),
	MOVIES("movies"),
	SEASONS("seasons"),
	EPISODES("episodes");
	
    private final String value;

    private Table(String value) 
    {
        this.value = value;
    }

    @Override
    public String toString() 
    {
        return this.value;
    }

    private static final Map<String, Table> STRING_MAPPING = new HashMap<String, Table>();

    static 
    {
        for (Table via : Table.values()) 
        {
            STRING_MAPPING.put(via.toString(), via);
        }
    }

    public static Table fromValue(String value) 
    {
        return STRING_MAPPING.get(value);
    }
}
