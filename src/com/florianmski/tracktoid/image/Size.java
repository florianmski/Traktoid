package com.florianmski.tracktoid.image;

import java.util.HashMap;
import java.util.Map;

public enum Size 
{
    SMALL("small"),
    LARGE("large"),
    UNCOMRESSED("uncompressed");

    private final String value;

    private Size(String value) 
    {
        this.value = value;
    }

    @Override
    public String toString() 
    {
        return this.value;
    }

    private static final Map<String, Size> STRING_MAPPING = new HashMap<String, Size>();

    static 
    {
        for (Size via : Size.values()) 
        {
            STRING_MAPPING.put(via.toString().toUpperCase(), via);
        }
    }

    public static Size fromValue(String value) 
    {
        return STRING_MAPPING.get(value.toUpperCase());
    }
}
