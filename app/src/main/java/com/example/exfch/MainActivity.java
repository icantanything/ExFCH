package com.example.exfch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    public String result;

    ImageButton Set, Personal, Makeup;
    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Set = (ImageButton) findViewById(R.id.Set_main);
        Set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set 클릭될 시 Setting 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        Personal = (ImageButton)findViewById(R.id.Personal_Color_B);
        Personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                num = intent.getIntExtra("사용자번호", 0);
                System.out.println("num: " + num);
                intent = new Intent(getApplicationContext(), PersonalActivity.class);
                intent.putExtra("사용자번호", num);
                startActivity(intent);
                finish();
            }
        });
    }


}