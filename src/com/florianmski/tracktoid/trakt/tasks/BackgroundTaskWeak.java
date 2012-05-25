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

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

/** 
 * A {@link BackgroundTask} that helps you keep a {@link WeakReference} to an Activity, Service, or whatever you want.
 * This will helps you avoid memory leaks.
 * @author Fabien Devos
 */
public abstract class BackgroundTaskWeak<TReference, TResult> extends BackgroundTask<TResult> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "BackgroundTaskWeak";

	private WeakReference<TReference> mWeakReference;

	//----------------------------------------------
	// Constructors

	/**
	 * Constructor.
	 */
	public BackgroundTaskWeak(TReference ref) {
		this(ref, null);
	}

	/**
	 * Constructor.
	 */
	public BackgroundTaskWeak(TReference ref, ExecutorService executor) {
		super(executor);
		mWeakReference = new WeakReference<TReference>(ref);
	}

	//----------------------------------------------
	// Reference

	@Override
	public void cancel() {
		super.cancel();
		mWeakReference.clear();
	}

	/**
	 * @return The weakly referenced object, or null if it doesn't exist anymore.
	 */
	public TReference getRef() {
		return mWeakReference.get();
	}

	/**
	 * Set an object as weakly referenced. Will erase the previously set one.
	 * @param ref the reference to set
	 */
	public void setRef(TReference ref) {
		mWeakReference.clear();
		mWeakReference = new WeakReference<TReference>(ref);
	}


}

