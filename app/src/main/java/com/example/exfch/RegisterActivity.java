package com.example.exfch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Pattern;

import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    String userID;
    String userPW;
    String FName;
    String LName;
    String userBday;
    String userSex;
    int userAge;

    final int[] age = {0};
    NumberPicker numberPicker;

    EditText id, pw, first_name, last_name, birth;
    ImageButton register, overlap;
    RadioGroup radioGroup;
    RadioButton Male, Female;

    public static String context_userid;
    public int var;

    String user = "User";
    int num = 0, num2;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

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
        setContentView(R.layout.activity_register);

        numberPicker = findViewById(R.id.number_picker_register);
        numberPicker.setMaxValue(99); //최대값
        numberPicker.setMinValue(9); //최소값
        numberPicker.setValue(25); //초기값

        id = findViewById(R.id.InputID_register);
        pw = findViewById(R.id.InputPW_register);
        first_name = findViewById(R.id.FName_register);
        last_name = findViewById(R.id.LName_register);
        birth = findViewById(R.id.Bday_register);
        radioGroup = findViewById(R.id.radio_group_register);
        Male = findViewById(R.id.Male_register);
        Female = findViewById(R.id.Female_register);

        //id, pw길이 10자/50자 제한
        int maxLength_pw = 10;
        int maxLength_id = 50;
        id.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength_id)});
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

        TextView text = (TextView) findViewById(R.id.TextB_login);
        text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TextView 클릭될 시 로그인 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        overlap = findViewById(R.id.OverlapID_register);
        register = findViewById(R.id.registerB);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                age[0] = newValue;
            }
        }); //나이 지정

        context_userid = userID;
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
    DatabaseReference databaseReference = database.getReference();
    DatabaseReference conditionRef = databaseReference.child("User");
    DatabaseReference usercount = databaseReference.child("User Count");

    int over = 0;
    final int[] count = {0};

    protected void onStart() {
        super.onStart();

        userID = id.getText().toString();
        userPW = pw.getText().toString();
        FName = first_name.getText().toString();
        LName = last_name.getText().toString();
        userBday = birth.getText().toString();
        userAge = age[0];

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Male.isChecked()) {
                    userSex = "Male";
                } else if(Female.isChecked()) {
                    userSex = "Female";
                }

                userID = id.getText().toString();
                userPW = pw.getText().toString();
                FName = first_name.getText().toString();
                LName = last_name.getText().toString();
                userBday = birth.getText().toString();
                userAge = age[0];

                if(over == 0){
                    Toast.makeText(getApplicationContext(), "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else if(over == 1) {
                    AddNewUser(userID, userPW, FName, LName, userBday, userSex, userAge);
                }
            }
        });


        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = id.getText().toString();
                if(userID.length() == 0) {
                    Toast.makeText(getApplicationContext(), "빈칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    usercount.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String value = snapshot.getValue().toString();
                                count[0] = Integer.parseInt(value);
                            } else {
                                count[0] = 0;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if(count[0] == 0){
                        Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        over = 1;
                    } else if(count[0] >= 1){
                        for(int i = 1; i <= count[0]; i++){
                            conditionRef.child(user+i).child("userID").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String value = snapshot.getValue(String.class);
                                    if(snapshot.exists()){
                                        Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        over = 0;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                        over = 1;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void AddNewUser(String userID, String userPW, String FName, String LName, String userBday, String userSex, int userAge) {
        if(userID.length() == 0 || userPW.length() == 0 || FName.length() == 0 || LName.length() == 0 || userBday.length() == 0 || userSex.length() == 0 || userAge < 9 || userAge > 99) {
            Toast.makeText(getApplicationContext(), "빈 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
            if (userAge < 9 || userAge > 99) {Toast.makeText(getApplicationContext(), "나이를 입력해주세요.", Toast.LENGTH_SHORT).show();}
        } else {
            int num = count[0];
            num++;
            Userdata new_user = new Userdata(userID, userPW, FName, LName, userBday, userSex, userAge);
            conditionRef.child(user+num).setValue(new_user);
            usercount.setValue(num);

            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}