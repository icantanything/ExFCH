package com.example.exfch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public String result;

    ImageButton Set, Personal;
    int num;

    String personalresult;

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    ImageView resultimage;
    TextView reText, save;

    ArrayList<String> resultList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
    DatabaseReference databaseReference = database.getReference();
    DatabaseReference conditionRef = databaseReference.child("User");
    DatabaseReference usercount = databaseReference.child("User Count");
    String user = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        num = intent.getIntExtra("사용자번호", 0);
        checkData(num);

        resultimage = (ImageView)findViewById(R.id.imageView2);
        reText = (TextView)findViewById(R.id.textView3);
        save = (TextView)findViewById(R.id.textView);

        if(num >= 1) {
            save.setVisibility(View.GONE);
        }

        Set = (ImageButton) findViewById(R.id.Set_main);
        Set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set 클릭될 시 Setting 화면으로 이동
                Intent intent2 = new Intent(getApplicationContext(), SettingActivity.class);
                intent2.putExtra("사용자번호", num);
                startActivity(intent2);
                finish();
            }
        });

        Personal = (ImageButton)findViewById(R.id.imageButton_personal);
        Personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PersonalActivity.class);
                intent2.putExtra("사용자번호", num);
                startActivity(intent2);
                finish();
            }
        });


    }

    public void checkData(int num) {
        System.out.println("num: " + num);
        if(num >= 1) {
            conditionRef.child(user+num).child("userColor").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.getValue(String.class);
                    if(snapshot.exists()){
                        personalresult = value;

                        if(personalresult.contains("spring")) {
                            resultimage.setImageResource(R.drawable.spring);
                            reText.setText("Spring");
                        } else if (personalresult.contains("summer")) {
                            resultimage.setImageResource(R.drawable.summer);
                            reText.setText("Summer");
                        } else if (personalresult.contains("fall")) {
                            resultimage.setImageResource(R.drawable.fall);
                            reText.setText("Autumn");
                        } else if (personalresult.contains("winter")) {
                            resultimage.setImageResource(R.drawable.winter);
                            reText.setText("Winter");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "한 번 더 뒤로가기 버튼을 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    /*@Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }*/
}