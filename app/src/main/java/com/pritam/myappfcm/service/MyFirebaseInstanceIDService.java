package com.pritam.myappfcm.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.pritam.myappfcm.SharedPrefUtils;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

// https://github.com/ganny26/firebase-notification-postman-collection/blob/master/Firebase%20Notification%20REST%20API.postman_collection.json
// https://medium.com/@selvaganesh93/firebase-cloud-messaging-important-rest-apis-be79260022b5
// https://firebase.google.com/docs/cloud-messaging/android/device-group
// https://firebase.google.com/docs/cloud-messaging/android/send-multiple
//    https://github.com/ganny26/firebase-notification-postman-collection

    @Override
    public void onTokenRefresh() {

        //For registration of token
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        //To displaying token on logcat
        Log.d("TOKEN: ", deviceToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(deviceToken);

    }

    private void sendRegistrationToServer(String deviceToken) {
        SharedPrefUtils sh = new SharedPrefUtils(this);
        sh.saveSharedPrefString("deviceToken", deviceToken);
        String deviceToken1 = sh.getSharedPrefString("deviceToken");
        Log.d("deviceToken -->", deviceToken1);
    }

}