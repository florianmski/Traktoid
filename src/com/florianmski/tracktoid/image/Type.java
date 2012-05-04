package com.florianmski.tracktoid.image;

import java.util.HashMap;
import java.util.Map;

public enum Type 
{
    POSTER("poster"),
    FANART("fanart"),
    HEADSHOT("headshot"),
    SCREEN("screen"),
    BANNER("banner");

    private final String value;

    private Type(String value) 
    {
        this.value = value;
    }

    @Override
    public String toString() 
    {
        return this.value;
    }

    private static final Map<String, Type> STRING_MAPPING = new HashMap<String, Type>();

    static 
    {
        for (Type via : Type.values()) 
        {
            STRING_MAPPING.put(via.toString().toUpperCase(), via);
        }
    }

    public static Type fromValue(String value) 
    {
        return STRING_MAPPING.get(value.toUpperCase());
    }
}
