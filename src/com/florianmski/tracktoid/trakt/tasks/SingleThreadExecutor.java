package com.florianmski.tracktoid.trakt.tasks;

/**
 * Copyright (c) 2012 Lightbox
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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** 
 * SingleThreadExecutor. Designed to replace the executor used in AsyncTask which is wrongly configured with a bounded queue.
 * 
 * @author Fabien Devos
 */
public class SingleThreadExecutor extends ThreadPoolExecutor {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "SingleThreadExecutor";

	private static final int DEFAULT_CORE_POOL_SIZE = 1; // 1 Thread maximum running in parallel
	private static final int MAXIMUM_POOL_SIZE = 1; // Will be ignored as long as the queue is unbounded
	private static final int KEEP_ALIVE = 1; //(in seconds)

	public SingleThreadExecutor() {
		super(  DEFAULT_CORE_POOL_SIZE,
				MAXIMUM_POOL_SIZE,
				KEEP_ALIVE, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName(TAG + " | " + thread.getName());
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}
		});
	}


}
