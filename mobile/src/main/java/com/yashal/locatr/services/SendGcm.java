package com.yashal.locatr.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.yashal.locatr.backend.messaging.Messaging;

import java.io.IOException;

/**
 * Created by yashal on 17/4/16.
 */
public class SendGcm extends IntentService {

    private static final String TAG = "SendGcm";
    private static final String[] TOPICS = {"global"};
    private static Messaging msgService = null;

    public SendGcm() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("qwe", "GCM sender");
        sendMessage(intent.getStringExtra("message"));
    }

    private void sendMessage(String message) {
        Log.d("qwe", "sendMessage");
        Account mAccount = AccountManager.get(this).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)[0];

        if (msgService == null) {
            GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(this,
                    "server:client_id:1029858388367-ck7qtbvst14m0adc60ljmjssh02rgt6d.apps.googleusercontent.com");
            credential.setSelectedAccountName(mAccount.name);
            Log.d("qwe", mAccount.name);
            Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), credential)
                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                    // otherwise they can be skipped
                    // .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setRootUrl("https://the-locatr.appspot.com/_ah/api/")
                    .setApplicationName("com.yashal.locatr")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            msgService = builder.build();
        }
        try {
            msgService.send(message).execute();
        } catch (IOException ex) {
            Log.e("message to server", "Error :" + ex.getCause() + ex.toString());
        } catch (Exception e) {
            Log.e("message to server", "Error :" + e.getMessage());
        }
    }
}