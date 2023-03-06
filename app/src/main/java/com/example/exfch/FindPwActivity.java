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

import java.util.Map;
import java.util.regex.Pattern;

public class FindPwActivity extends AppCompatActivity {
    String userID, FName, LName, userBday, userSex;
    int userAge;

    final int[] age = {0};
    NumberPicker numberPicker;

    EditText id, first_name, last_name, birth;
    ImageButton Find_PW;
    RadioGroup radioGroup;
    RadioButton Male, Female;

    final int[] count = {0};
    String user = "User";

    //fn, ln 영문 및 한글만 허용
    protected InputFilter filter_name= new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅣ가-힣]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //bday 숫자만 허용
    protected InputFilter filter_bday= new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw);

        numberPicker = findViewById(R.id.number_picker_findpw);
        numberPicker.setMaxValue(99);
        numberPicker.setMinValue(9);
        numberPicker.setValue(25);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                age[0] = newValue;
            }
        }); //나이 지정

        id = findViewById(R.id.InputID_findpw);
        first_name = findViewById(R.id.FName_findpw);
        last_name = findViewById(R.id.LName_findpw);
        birth = findViewById(R.id.Bday_findpw);
        radioGroup = findViewById(R.id.radio_group_findpw);
        Male = findViewById(R.id.Male_findpw);
        Female = findViewById(R.id.Female_findpw);

        //id 길이 10자 제한
        int maxLength_idpw = 10;
        id.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength_idpw)});

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

        Find_PW = findViewById(R.id.findpw_findpw);
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
    DatabaseReference databaseReference = database.getReference();
    DatabaseReference conditionRef = databaseReference.child("User");
    DatabaseReference usercount = databaseReference.child("User Count");

    protected  void onStart() {
        super.onStart();

        userID = id.getText().toString();
        FName = first_name.getText().toString();
        LName = last_name.getText().toString();
        userBday = birth.getText().toString();
        userAge = age[0];

        Find_PW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Male.isChecked()) {
                    userSex = "Male";
                } else if(Female.isChecked()) {
                    userSex = "Female";
                }

                userID = id.getText().toString();
                FName = first_name.getText().toString();
                LName = last_name.getText().toString();
                userBday = birth.getText().toString();
                userAge = age[0];

                Checkuser(FName, LName, userBday, userSex, userAge, userID);
            }
        });
    }

    public void Checkuser(String FName, String LName, String userBday, String userSex, int userAge, String userID){
        usercount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String value = snapshot.getValue().toString();
                    count[0] = Integer.parseInt(value);
                    //FindID(FName, LName, userBday, userSex, userAge);
                    getuserdata(FName, LName, userBday, userSex, userAge, count[0], userID);
                } else {
                    count[0] = 0;
                    Toast.makeText(getApplicationContext(), "회원가입을 진행해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    CheckInf checkIDPW = new CheckInf();

    public void getuserdata(String FName, String LName, String userBday, String userSex, int userAge, int count, String userID) {
        for(int i = 1; i <= count; i++){
            int finalI = i;
            conditionRef.child(user+ i).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, String> user1 = (Map) snapshot.getValue();
                    String gfn = user1.get("FName");
                    String gln = user1.get("LName");
                    String gid = user1.get("userID");
                    String gpw = user1.get("userPW");
                    String gbd = user1.get("userBday");
                    String gse = user1.get("userSex");
                    int gag = Integer.valueOf(String.valueOf(user1.get("userAge")));

                    if(gfn.equals(FName) == true && gln.equals(LName) == true && gbd.equals(userBday) == true && gse.equals(userSex) == true && gag == userAge && gid.equals(userID) == true) {
                        checkIDPW.check = 1;
                        Toast.makeText(getApplicationContext(), "비밀번호: " + gpw, Toast.LENGTH_SHORT).show();
                    } else if (checkIDPW.check != 1 && finalI == count){
                        checkIDPW.check = 0;
                        Toast.makeText(getApplicationContext(), "계정이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}