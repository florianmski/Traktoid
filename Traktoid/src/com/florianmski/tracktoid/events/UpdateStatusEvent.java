package com.florianmski.tracktoid.events;

public abstract class UpdateStatusEvent 
{
	protected boolean running;

	public UpdateStatusEvent(boolean running) 
	{
		this.running = running;
	}

	public boolean isRunning()
	{
		return running;
	}
}
