package com.example.exfch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class modifyActivity extends AppCompatActivity {

    EditText id, pw, first_name, last_name, birth;
    ImageButton fix;
    RadioGroup radioGroup;
    RadioButton Male, Female;
    NumberPicker numberPicker;

    private FirebaseDatabase database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
    DatabaseReference databaseReference = database.getReference();
    DatabaseReference conditionRef = databaseReference.child("User");
    DatabaseReference usercount = databaseReference.child("User Count");

    String userID;
    String userPW;
    String FName;
    String LName;
    String userBday;
    String userSex;
    int userAge;
    final int[] age = {0};
    String user = "User";

    int num;

    //pw 영문 및 숫자만 허용
    protected InputFilter filter= new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //fn, ln 영문 및 한글만 허용
    protected InputFilter filter_name= new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅣ가-힣]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //bday 숫자만 허용
    protected InputFilter filter_bday= new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        numberPicker = findViewById(R.id.number_picker_modify);
        numberPicker.setMaxValue(99); //최대값
        numberPicker.setMinValue(9); //최소값
        numberPicker.setValue(25); //초기값

        id = findViewById(R.id.inputID_modify);
        pw = findViewById(R.id.inputPW_modify);
        first_name = findViewById(R.id.FName_modify);
        last_name = findViewById(R.id.LName_modify);
        birth = findViewById(R.id.bday_modify);
        radioGroup = findViewById(R.id.radio_group_modify);
        Male = findViewById(R.id.Male_modify);
        Female = findViewById(R.id.Female_modify);

        fix = findViewById(R.id.imageButton_modify);

        //pw길이 10자 제한
        int maxLength_pw = 10;
        pw.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength_pw)});
        pw.setFilters(new InputFilter[] {filter});

        //fname, lname 길이 20자 제한
        int maxLength_flname = 20;
        first_name.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength_flname)});
        last_name.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength_flname)});
        first_name.setFilters(new InputFilter[] {filter_name});
        last_name.setFilters(new InputFilter[] {filter_name});

        //bday 길이 8자 제한
        int maxLength_bday = 8;
        birth.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength_bday)});
        birth.setFilters(new InputFilter[] {filter_bday});

        Intent intent = getIntent();
        num = intent.getIntExtra("사용자번호", 0);
        System.out.println("num: " + num);
        getuserdata(num);

        fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixuserdata(num);
            }
        });
    }

    public void getuserdata(int num) {
        conditionRef.child("User"+num).child("userID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String snap = snapshot.getValue().toString();
                id.setText(snap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fixuserdata(int num){
        if(Male.isChecked()) {
            userSex = "Male";
        } else if(Female.isChecked()) {
            userSex = "Female";
        }

        userPW = pw.getText().toString();
        FName = first_name.getText().toString();
        LName = last_name.getText().toString();
        userBday = birth.getText().toString();
        userAge = age[0];

        conditionRef.child("User"+num).child("userID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String snap = snapshot.getValue().toString();
                if(snapshot.exists()){
                    Userdata new_user = new Userdata(snap, userPW, FName, LName, userBday, userSex, userAge);
                    conditionRef.child(user+num).setValue(new_user);
                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                    intent1.putExtra("사용자번호", num);
                    startActivity(intent1);
                    finish();
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}