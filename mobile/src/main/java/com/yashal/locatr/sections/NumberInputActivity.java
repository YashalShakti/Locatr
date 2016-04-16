package com.yashal.locatr.sections;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.yashal.locatr.R;

public class NumberInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_input);
        final EditText numberInput = (EditText) findViewById(R.id.number_input);
        Button saveButton = (Button) findViewById(R.id.save_number);
        Firebase.setAndroidContext(this);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase myFirebaseRef = new Firebase("https://locatr.firebaseio.com/");
                myFirebaseRef.child("mynumber").setValue(numberInput.getText().toString());
                finish();
            }
        });
    }
}

