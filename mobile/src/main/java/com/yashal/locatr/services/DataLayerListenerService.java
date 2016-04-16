package com.yashal.locatr.services;

import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yashal on 17/4/16.
 */
public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if ("/MESSAGE".equals(messageEvent.getPath())) {
            // launch some Activity or do anything you like
            Firebase.setAndroidContext(this);
            final Firebase myFirebaseRef = new Firebase("https://locatr.firebaseio.com/");
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Intent gcmIntent = new Intent(DataLayerListenerService.this, SendGcm.class);
                    System.out.println("There are " + snapshot.getChildrenCount() + "posts");
                    List<String> contacts = null;
                    String message = "";
                    String myNumber = "";
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (postSnapshot.getKey().equals("message")) {
                            message = postSnapshot.getValue(String.class);
                        } else if (postSnapshot.getKey().equals("contacts")) {
                            contacts = postSnapshot.getValue(ArrayList.class);
                        } else if (postSnapshot.getKey().equals("mynumber")) {
                            myNumber = postSnapshot.getValue(String.class);
                        }
                    }
                    gcmIntent.putExtra("message", "From: " + myNumber + "\n" + message);
                    startService(gcmIntent);
                    SmsManager smsManager = SmsManager.getDefault();
                    for (String number : contacts) {
                        Log.d("asd", "Sending text to " + number);
                        //   smsManager.sendTextMessage(number, null, message, null, null);
                    }
                    Toast.makeText(DataLayerListenerService.this, "Would send messages to " + contacts.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }
}
