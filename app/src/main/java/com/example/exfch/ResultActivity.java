package com.example.exfch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    TextView result, mainback;
    ImageView resultimage;

    String personalresult;

    ArrayList<String> resultList = new ArrayList<>();

    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        result = (TextView)findViewById(R.id.personalcolor);
        mainback = (TextView)findViewById(R.id.backmain);
        resultimage = (ImageView)findViewById(R.id.resultimage);

        Intent intent = getIntent();
        resultList = (ArrayList<String>)intent.getSerializableExtra("사용자정보");
        personalresult = resultList.get(0);
        num = Integer.parseInt(resultList.get(1));

        result.setText(personalresult);
        if(personalresult.contains("spring")) {
            resultimage.setImageResource(R.drawable.spring);
        } else if (personalresult.contains("summer")) {
            resultimage.setImageResource(R.drawable.summer);
        } else if (personalresult.contains("autumn")) {
            resultimage.setImageResource(R.drawable.fall);
        } else if (personalresult.contains("winter")) {
            resultimage.setImageResource(R.drawable.winter);
        }



        mainback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("사용자번호", num);
                startActivity(intent);
                finish();
            }
        });
    }
}