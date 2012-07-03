package com.florianmski.tracktoid;

import java.util.List;

import com.florianmski.traktoid.TraktoidInterface;

public interface TraktListener<T>
{
//	public void onBeforeTraktRequest();
//	public void onAfterTraktRequest(boolean success);
//	public void onErrorTraktRequest(Exception e);
	public void onTraktItemsUpdated(List<T> traktItems);
	public void onTraktItemsRemoved(List<T> traktItems);
//	public void onCalendar(ArrayList<ArrayList<CalendarDate>> calendars);
}
