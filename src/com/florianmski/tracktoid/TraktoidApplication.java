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

package com.florianmski.tracktoid;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import com.florianmski.tracktoid.trakt.TraktManager;

import static org.acra.ReportField.*;

import android.app.Application;

@ReportsCrashes(formKey = "dGlwaUVySktzbEJuVlBKUjBMeUNfYXc6MQ",
customReportContent = { APP_VERSION_NAME, ANDROID_VERSION, PHONE_MODEL, STACK_TRACE })
public class TraktoidApplication extends Application
{
	@Override
	public void onCreate() 
	{
		// The following line triggers the initialization of ACRA
		//        ACRA.init(this);

		TraktManager.create(this);
		super.onCreate();
	}
}
