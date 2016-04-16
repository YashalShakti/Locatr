package com.yashal.locatr.services;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by yashal on 17/4/16.
 */
public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if ("/MESSAGE".equals(messageEvent.getPath())) {
            // launch some Activity or do anything you like
            Log.d("qwe","Wear app clicked");
        }
    }
}