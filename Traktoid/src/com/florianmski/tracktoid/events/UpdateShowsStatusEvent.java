package com.florianmski.tracktoid.events;

public class UpdateShowsStatusEvent extends UpdateStatusEvent
{
	public UpdateShowsStatusEvent(boolean running) 
	{
		super(running);
	}
}
