package com.example.exfch;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    final int MY_PERMISSION_REQUEST_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //chkPermission();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

    private void chkPermission(){

        //권한부여가 되어있지 않을 때
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("tag","권한부여가 되어있지 않을 때");
            Toast.makeText(this, "카메라에 대한 권한이 없으므로 권한을 요청합니다.", Toast.LENGTH_SHORT).show();

            //권한이 없을때 최초 실행시 작동안함(false)
            //사용자가 권한요청을 한 번 거절하면 shouldShowRequestPermissionRationale는 true를 반환하여 작동
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                Toast.makeText(this, "앱 실행을 위해서는 카메라 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show();
            }

            //권한요청 팝업창
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_STORAGE);

        }else{
            //권한부여가 되어있을 때
            Log.d("tag","권한부여가 되어있을 때");
            Toast.makeText(this, "카메라에 대한 권한이 이미 부여되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }



}