package com.example.exfch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.content.SharedPreferences;

import java.util.regex.Pattern;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    //id, pw 영문 및 숫자만 허용
    protected InputFilter filter= new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView register = (TextView) findViewById(R.id.register_login);
        TextView find_pw = (TextView) findViewById(R.id.findpw_login);
        TextView find_id = (TextView) findViewById(R.id.findid_login);

        EditText id = (EditText) findViewById(R.id.InputID_login);
        EditText pw = (EditText) findViewById(R.id.InputPW_login);

        ImageButton login = (ImageButton) findViewById(R.id.loginB);
        ImageButton notlogin = (ImageButton) findViewById(R.id.notloginB);

        //id, pw길이 10자 제한
        int maxLengthpw = 10;
        int maxLengthid = 50;
        id.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthid)});
        pw.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthpw)});
        pw.setFilters(new InputFilter[] {filter});

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // register_login 클릭될 시 정보 확인 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), DataCheckActivity.class);
                startActivity(intent);
            }
        });

        find_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // findid_login 클릭 시 find id 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
                startActivity(intent);
            }
        });

        find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // findpw_login 클릭 시 find pw 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), FindPwActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력된 id와 pw를 데베와 비교
                String input_id = id.getText().toString();
                String input_pw = pw.getText().toString();

                if(input_id.equals("") || input_pw.equals("")){ //id 또는 pw가 빈 값
                    Toast.makeText(getApplicationContext(), "빈 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // loginB 클릭 시 MainActivity 화면으로 이동
                    loginUser(input_id, input_pw);
                }
            }
        });

        notlogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // notloginB 클릭될 시 MainActivity 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
    DatabaseReference databaseReference = database.getReference();
    DatabaseReference conditionRef = databaseReference.child("User");
    DatabaseReference usercount = databaseReference.child("User Count");

    final int[] count = {0};
    String user = "User";

    public void loginUser(String input_id, String input_pw){
        usercount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String value = snapshot.getValue().toString();
                    count[0] = Integer.parseInt(value);
                    checkusernum(input_id, input_pw);
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

    public void checkusernum(String id, String pw) {
        CheckInf checkIDPW = new CheckInf();
        if(count[0] >= 1){
            for(int i = 1; i <= count[0]; i++){
                int finalI = i;
                conditionRef.child(user+i).child("userID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        if(value.equals(id) == true){
                            checkIDPW.checkid = value;
                            checkIDPW.usernum = finalI;
                            System.out.println("id: " + checkIDPW.checkid);
                            System.out.println("pw: " + pw);
                            System.out.println("num: " + checkIDPW.usernum);
                            checkIDPW.check = 1;
                            System.out.println("1. check: " + checkIDPW.check);
                            checkidpw(id, pw, finalI);
                        } else if(checkIDPW.check != 1 && finalI == count[0]){
                            Toast.makeText(getApplicationContext(), "아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    public void checkidpw(String id, String pw, int num) {
        conditionRef.child(user+num).child("userID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String snap = snapshot.getValue().toString();
                System.out.println(num + ". snap: " + snap);
                if (snap.equals(id) == true) {
                    conditionRef.child(user+num).child("userPW").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            String snap2 = snapshot2.getValue().toString();
                            System.out.println(num + ". snap: " + snap2);
                            if (snap2.equals(pw) == true) {
                                //Login success
                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("사용자번호", num);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if(snap.equals(id) == false) {
                    System.out.println("2/ " + num + ". snap: " + snap);
                    Toast.makeText(getApplicationContext(), "아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}