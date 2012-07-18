/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gcm.demo.app;

import android.content.Context;
import android.content.Intent;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    //static final String SERVER_URL = "http://169.254.249.199:8080/gcm-demo"; // Office
	//static final String SERVER_URL = "http://192.168.1.105:8080/gcm-demo"; //Home
	//static final String SERVER_URL = "http://192.168.136.1:8080/gcm-demo"; //USB Tethering
	//static final String SERVER_URL = "http://169.254.249.199:8080/gcm-demo"; //Serena Guest wifi
	static final String SERVER_URL = "http://172.20.10.5:8080/gcm-demo"; //Jack iphone wifi
	static final String FLOZ_URL = "http://10.130.12.104:3003"; //Floz's machine    
	static final String DATA_SERVER_URL = "http://169.254.249.199:3003";    

    /**
     * Google API project id registered to use GCM.
     */
    static final String SENDER_ID = "14228982970";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }   
    
}
