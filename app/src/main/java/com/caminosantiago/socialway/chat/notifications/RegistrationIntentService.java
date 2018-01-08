package com.caminosantiago.socialway.chat.notifications;

/**
 * Created by admin on 21/11/2015.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.caminosantiago.socialway.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            sendRegistrationToServer(token);
        } catch (Exception e) {}
    }

   //clN5HQ4TnoE:APA91bHH43CyP4u_DhsqJx7RKkHVnOlHA3CeIdr656IRnbcfhcwDNpWr4NHwhy5X-uxntMvtkjvvvsGYHcQq-haQunPS86uR7CMX1eIEApz10HNkyXRJAZ6mqvyeFHNbRNPK2Oj_BQWY



    private void sendRegistrationToServer(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tokenNotifications", token);
        editor.commit();
    }


}