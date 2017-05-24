package com.fanny.traxivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Listen to the changes in the Data Layer Event, used to send the user's name from the mobile to the wear
 */
public class ListenerService extends WearableListenerService {
    /**
     * When there is a change in the Data Layer Event, write the new name in the shared preferences and call sendBroadcast to update the textView "welcome" in the main activity
     * @see ListenerService#sendBroadcast()
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/message_path")) {
            final String name = new String(messageEvent.getData());

            final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("name", name);
            editor.apply();

            sendBroadcast();


            Log.v("myTag", "Message received on watch is: " + name);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    /**
     * Send the intent to the BroadcastReceiver nameReceiver in the main activity
     * @see MainActivity#nameReceiver
     */
    public void sendBroadcast() {
        Intent intent = new Intent("newName");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
}

}
