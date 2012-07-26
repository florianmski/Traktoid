package com.florianmski.tracktoid.events;

public class UpdateMoviesStatusEvent extends UpdateStatusEvent
{
	public UpdateMoviesStatusEvent(boolean running) 
	{
		super(running);
	}
}
