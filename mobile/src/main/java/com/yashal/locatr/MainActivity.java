package com.yashal.locatr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.yashal.locatr.sections.Message_selector_activity;
import com.yashal.locatr.sections.NumberInputActivity;
import com.yashal.locatr.services.RegistrationIntentService;
import com.yashal.locatr.services.SendGcm;
import com.yashal.locatr.ui.ContactsListActivity;
import com.yashal.locatr.util.Preferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button selectContactsButton = (Button) findViewById(R.id.select_contacts_button);
        Button sendButton = (Button) findViewById(R.id.send_alert_button);
        Button selectMessageButton = (Button) findViewById(R.id.select_message_button);
        Button enterNumberButton = (Button) findViewById(R.id.enter_number_button);

        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        selectContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactsListActivity.class);
                startActivity(intent);
            }
        });

        enterNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NumberInputActivity.class);
                startActivity(intent);
            }
        });

        selectMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Message_selector_activity.class);
                startActivity(intent);
            }
        });


        assert sendButton != null;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase.setAndroidContext(MainActivity.this);
                Firebase myFirebaseRef = new Firebase("https://locatr.firebaseio.com/");

                myFirebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Intent gcmIntent = new Intent(MainActivity.this, SendGcm.class);
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
                        gcmIntent.putExtra("message", "From: " + myNumber + " " + message);
                        startService(gcmIntent);
                        SmsManager smsManager = SmsManager.getDefault();
                        for (String number : contacts) {
                            Log.d("asd", "Sending text to " + number);
                            //   smsManager.sendTextMessage(number, null, message, null, null);
                        }
                        Toast.makeText(MainActivity.this, "Would send messages to " + contacts.toString(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
        });


        //  myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "onRecieve", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(context, "Succesful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, ":(", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Preferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("qwe", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
