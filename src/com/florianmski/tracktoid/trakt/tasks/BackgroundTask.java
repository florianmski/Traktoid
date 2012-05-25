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

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import android.os.Handler;
import android.util.Log;

/** 
 * <p>BackgroundTask is a convenient way to execute tasks on the background, and then being called back on main thread.
 * This is greatly inspired by AsyncTask, but gives us finer control over the parameters of execution (thread pool 
 * size, etc.).
 * <p>Example:
 * <pre>
 * 		BackgroundTask&lt;String&gt; task = new BackgroundTask&lt;String&gt;() {
 *  		&#064;Override
 *  		protected void onProgressPublished(int progress) {
 *  			// Show progress
 *  		}
 *  		&#064;Override
 *  		protected void onFailed(Exception e) {
 *  			// Display error message
 *  		}
 *  		&#064;Override
 *  		protected void onCompleted(String result) {
 *  			// Use the result
 *  		}
 *  		&#064;Override
 *  		protected String doWorkInBackground() throws Exception {
 *  			// In this example, we just fake a long computation with several steps
 *  			for (int i = 0; i <= 10; i++) {
 *  				Thread.sleep(200);
 *  				publishProgress(i);
 *  			}
 *  			return "result";
 *  		}
 *  	};
 *  	
 *  	task.execute();
 * </pre>
 * @author Fabien Devos
 */
public abstract class BackgroundTask<TResult> {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "BackgroundTask";

	private static ExecutorService sMainExecutor = new DefaultExecutor();
	private static Handler sHandler = null;
	private static ConcurrentMap<String, BackgroundTask<?>> sRunningTasks = new ConcurrentHashMap<String, BackgroundTask<?>>();

	private ExecutorService mExecutor = null;
	private FutureTask<TResult> mFutureTask;
	private String mId;

	//----------------------------------------------
	// Running tasks.

	public boolean isRunning() {
		return isRunning(mId);
	}

	public static boolean isRunning(String key) {
		if (key == null) { return false; }
		return sRunningTasks.get(key) != null;
	}

	public static BackgroundTask<?> getRunningTask(String key) {
		if (key == null) { return null; }
		return sRunningTasks.get(key);
	}

	private static void addRunningTask(BackgroundTask<?> task) {
		if (task.getId() != null) {
			sRunningTasks.put(task.getId(), task);
		}
	}

	private static void removeTask(String key) {
		if (key != null) {
			sRunningTasks.remove(key);
		}
	}

	//----------------------------------------------
	// Constructors.

	/**
	 * Constructor. <strong>Must be called on main thread.</strong>
	 */
	public BackgroundTask() {
		this(null);
	}

	/**
	 * Constructor. <strong>Must be called on main thread.</strong>
	 * @param executor the executor to use for this task execution
	 */
	public BackgroundTask(ExecutorService executor) {
		if(sHandler == null) {
			sHandler = new Handler();
		}
		if(executor != null) {
			mExecutor = executor;
		} else {
			mExecutor = sMainExecutor;
		}

		// Create the Callable worker that will run in the background
		Callable<TResult> doWorkCallable = new Callable<TResult>() {
			@Override
			public TResult call() throws Exception {
				callOnPreExecuteOnMainThread();
				return doWorkInBackground();
			}
		};

		// Wrap it in a FutureTask to be notified of the result
		mFutureTask = new FutureTask<TResult>(doWorkCallable) {
			@Override
			protected void done() {
				if ( ! isCancelled()) {
					try {
						TResult result = get();
						callOnCompletedOnMainThread(result);
					} catch (InterruptedException e) {
						Log.d(TAG, "%s", e);
						callOnFailedOnMainThread(e);
					} catch (ExecutionException e) {
						Log.d(TAG, "%s", e);
						// Try to unwrap exception
						Throwable cause = e.getCause();
						if (cause != null && cause instanceof Exception) {
							callOnFailedOnMainThread((Exception) cause);							
						} else {
							callOnFailedOnMainThread(e);
						}
					} catch (CancellationException e) {
						// Silently ignore cancellation
					} catch (Exception e) {
						Log.d(TAG, "%s", e);
						callOnFailedOnMainThread(e);
					} catch (Throwable t) {
						Log.d(TAG, "%s", t);
						callOnFailedOnMainThread(new RuntimeException(t));
					}
				}
			}
		};

	}

	//----------------------------------------------
	// Getters and Setters

	public String getId() {
		return mId;
	}

	/** Must be called before {@link #execute()} */
	public void setId(String id) {
		mId = id;
	}

	//----------------------------------------------
	// Helpers method to run call-backs on main thread

	private void callOnCompletedOnMainThread(final TResult result) {
		sHandler.post(new Runnable() {
			@Override
			public void run() {
				removeTask(mId);
				onCompleted(result);
			}
		});
	}

	private void callOnFailedOnMainThread(final Exception e) {
		sHandler.post(new Runnable() {
			@Override
			public void run() {
				removeTask(mId);
				onFailed(e);
			}
		});
	}
	
	private void callOnPreExecuteOnMainThread() {
		sHandler.post(new Runnable() {
			@Override
			public void run() {
				onPreExecute();
			}
		});
	}

	private void callOnProgressPublishedOnMainThread(final int progress, final TResult tmpResult, final String... values) {
		sHandler.post(new Runnable() {
			@Override
			public void run() {
				onProgressPublished(progress, tmpResult, values);
			}
		});
	}

	//----------------------------------------------
	// API for subclasses

	/**
	 * This method will be called in <strong>a background thread</strong>.
	 * Override this method to perform your background computation, and then simply return the result.
	 * You'll be called back on the main thread with the {@link #onCompleted(Object)} method.
	 */
	protected abstract TResult doWorkInBackground() throws Exception;

	/**
	 * This method will be called <strong>on the main thread</strong> if the task terminates successfully. 
	 * @param result the result of the background computation.
	 */
	protected abstract void onCompleted(TResult result);

	/**
	 * This method will be called <strong>on the main thread</strong> if the task terminates with an error. 
	 * @param e the exception that caused the termination.
	 */
	protected abstract void onFailed(Exception e);

	/**
	 * This method will be called <strong>on the main thread</strong> before doWorkInBackground is called
	 */
	protected abstract void onPreExecute();
	
	/**
	 * This method will be called <strong>on the main thread</strong> each time a progress is published. 
	 * Optional. Default implementation does nothing.
	 * @param progress the current progress
	 * @param tmpResult the temporary result, if any
	 */
	protected abstract void onProgressPublished(int progress, TResult tmpResult, String... values);

	/**
	 * Call this method from {@link #doWorkInBackground()} to publish the progress of the currently running task to
	 * the main thread. You will be called back in {@link #onProgressPublished(int, Object)}.
	 * @param progress
	 * @param tmpResult
	 */
	public final void publishProgress(int progress, TResult tmpResult, String... values) {
		callOnProgressPublishedOnMainThread(progress, tmpResult, values);
	}

	//----------------------------------------------
	// Execution

	/**
	 * Start background execution of this task, using either the provided ExecutorService, or the default one. 
	 */
	public void execute() {
		addRunningTask(this);

		mExecutor.execute(mFutureTask);
	}

	/**
	 * Cancel this task.
	 */
	public void cancel() {
		removeTask(mId);

		mFutureTask.cancel(true);
	}

	/**
	 * @return true if this task completed.
	 */
	public boolean isDone() {
		return mFutureTask.isDone();
	}

}
