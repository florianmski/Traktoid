package com.florianmski.tracktoid;

import com.florianmski.traktoid.TraktoidInterface;

public interface TraktListener <T extends TraktoidInterface<T>>
{
//	public void onBeforeTraktRequest();
//	public void onAfterTraktRequest(boolean success);
//	public void onErrorTraktRequest(Exception e);
	public void onTraktItemUpdated(T traktItem);
	public void onTraktItemRemoved(T traktItem);
//	public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars);
}
