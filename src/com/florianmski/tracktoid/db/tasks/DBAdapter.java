/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.db.tasks;

import java.util.List;

import com.florianmski.tracktoid.db.tasks.DBTask.DBListener;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;

public abstract class DBAdapter implements DBListener
{
	public void onDBShows(List<TvShow> shows) {}
	public void onDBSeasons(List<TvShowSeason> seasons) {}
	public void onDBEpisodes(List<TvShowEpisode> episodes) {}
}
