package com.example.exfch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
    String userID;
    TextView userID_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        userID_set = (TextView)findViewById(R.id.textView);
        Log.d(this.getClass().getName(), (String)userID_set.getText());
        userID_set.setText(userID);
    }
}