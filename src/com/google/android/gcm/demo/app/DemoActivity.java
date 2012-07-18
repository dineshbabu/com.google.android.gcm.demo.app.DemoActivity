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

import static com.google.android.gcm.demo.app.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.google.android.gcm.demo.app.CommonUtilities.EXTRA_MESSAGE;
import static com.google.android.gcm.demo.app.CommonUtilities.SENDER_ID;
import static com.google.android.gcm.demo.app.CommonUtilities.SERVER_URL;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main UI for the demo app.
 */
public class DemoActivity extends Activity {
	StoryDetails sd ;
    TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;

    @TargetApi(9)
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// In order to allow to make a network call in the newIntent method
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        
        super.onCreate(savedInstanceState);
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        setContentView(R.layout.main);
        mDisplay = (TextView) findViewById(R.id.display);
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, needs to check if it is
            // registered on our server as well.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.Uncomment if device already registered message needs to be displayed.
                //mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
            
            
            //Read notification
            
            
            
        }
    }
    
    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
    	
        String payLoad = intent.getDataString();
        //String storyDetails  = "";
        StringBuffer queryString = new StringBuffer();
        Gson gson = new Gson();
        sd = gson.fromJson(payLoad, StoryDetails.class);
        
        /*Map<String, String> params = new HashMap<String, String>();
        params.put("user", "dinesh");
        params.put("command", "get");
        params.put("type", "story");
        params.put("uid", String.valueOf(sd.getUid()));*/
        queryString.append("?user=dinesh&command=get&type=story&uid=");
        queryString.append(String.valueOf(sd.getUid()));
        try {
			//storyDetails = ServerUtilities.fecthStoryDetails(SERVER_URL +"/sendAll", params);
        	sd = gson.fromJson(ServerUtilities.fecthStoryDetails(CommonUtilities.DATA_SERVER_URL+queryString), StoryDetails.class);
        	//sd = gson.fromJson(ServerUtilities.fecthStoryDetails(SERVER_URL+"/sendAll"+queryString), StoryDetails.class);
        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        /*Bundle extras = intent.getExtras();
        if (extras != null) {
            int i = extras.getInt("story", -1);
            if (i != -1) Toast.makeText(this, "Got message! " + String.valueOf(i), Toast.LENGTH_LONG).show();
        }*/
        
        //Intent intent = getIntent();
        intent.putExtra("criteria", sd.getCriteria());        

        intent.setData(Uri.parse("uid:"+sd.getUid()));
        // Create the text view
        /*EditText textView = new EditText(this);
        textView.setTextSize(10);
        textView.setText(message);

        setContentView(textView); */
        setContentView(R.layout.activity_main);
        
        // Create the text view
        //EditText textView = new EditText(this);
        EditText textView =(EditText) this.findViewById(R.id.editText11);
        //textView.setTextSize(10);
        textView.setText(sd.getCriteria());
        //setContentView(textView);
    }
    
    public void sendNewCriteria(View okButton){
       	boolean success = false;
    	StringBuffer queryString = new StringBuffer();
    	EditText textView =(EditText) this.findViewById(R.id.editText11);
    	String criteria = URLEncoder.encode(textView.getText().toString());
   	
    	queryString.append("?user=dinesh&command=put&type=story&criteria=");
    	queryString.append(criteria);
    	queryString.append("&uid=");
    	queryString.append(sd.getUid());
    	
        try {
			//storyDetails = ServerUtilities.fecthStoryDetails(SERVER_URL +"/sendAll", params);
        	//sd = gson.fromJson(ServerUtilities.fecthStoryDetails(CommonUtilities.FLOZ_URL+queryString), StoryDetails.class);
        	//success = Boolean.valueOf(ServerUtilities.sendNewCriteria(SERVER_URL+"/sendAll"+queryString));
        	success = Boolean.valueOf(ServerUtilities.sendNewCriteria(CommonUtilities.DATA_SERVER_URL+queryString));
        	//findViewById(R.layout.this).setVisibility(RelativeLayout.GONE);
      
        	this.finish();
        	
        	/*Intent intent= new Intent(this, this.getClass());
        	intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(intent);*/

        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void closeApp(View cancelButton){
    	this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*
             * Typically, an application registers automatically, so options
             * below are disabled. Uncomment them if you want to manually
             * register or unregister the device (you will also need to
             * uncomment the equivalent options on options_menu.xml).
             */
            /*
            case R.id.options_register:
                GCMRegistrar.register(this, SENDER_ID);
                return true;
            case R.id.options_unregister:
                GCMRegistrar.unregister(this);
                return true;
             */
            case R.id.options_clear:
                mDisplay.setText(null);
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            mDisplay.append(newMessage + "\n");
        }
    };

}