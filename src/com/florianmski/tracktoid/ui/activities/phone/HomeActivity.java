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

package com.florianmski.tracktoid.ui.activities.phone;

import android.os.Bundle;
import android.support.v4.view.Window;
import android.view.KeyEvent;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.ui.fragments.HomeFragment;

public class HomeActivity extends TraktActivity
{	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
    	if ((keyCode == KeyEvent.KEYCODE_BACK))
    	{
    		((HomeFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_home)).handlePanel();
    		return true;
    	}
    	
   		return super.onKeyDown(keyCode, event);
    }
}