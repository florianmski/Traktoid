package com.florianmski.tracktoid;

import com.squareup.otto.Bus;

public class TraktBus 
{
	private static final Bus BUS = new Bus();

	public static Bus getInstance() 
	{
		return BUS;
	}

	private TraktBus() {}
}
