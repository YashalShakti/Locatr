package com.yashal.locatr.services;

import android.content.Intent;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
            Firebase.setAndroidContext(this);
            final Firebase myFirebaseRef = new Firebase("https://locatr.firebaseio.com/");
            myFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Intent gcmIntent = new Intent(DataLayerListenerService.this, SendGcm.class);
                    System.out.println("There are " + snapshot.getChildrenCount() + "posts");
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String post = postSnapshot.getValue(String.class);
                        Log.d("firebase", post);
                        gcmIntent.putExtra("message", post);
                    }
                    startService(gcmIntent);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }
}
