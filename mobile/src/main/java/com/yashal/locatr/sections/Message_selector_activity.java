package com.yashal.locatr.sections;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.yashal.locatr.R;

public class Message_selector_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_selector);
        final EditText messageInput = (EditText) findViewById(R.id.message_input);
        Button saveButton = (Button) findViewById(R.id.save_message);
        Firebase.setAndroidContext(this);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase myFirebaseRef = new Firebase("https://locatr.firebaseio.com/");
                myFirebaseRef.child("message").setValue(messageInput.getText().toString());
                finish();
            }
        });
    }
}
