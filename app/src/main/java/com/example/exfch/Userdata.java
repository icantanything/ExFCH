package com.example.exfch;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Userdata {

    //사용자 기본정보
    public String FName;
    public String LName;
    public String userID;
    public String userPW;
    public String userSex;
    public String userBday;
    public int userAge;

    public Userdata() {}

    public Userdata(String userID, String userPW, String FName, String LName, String userBday, String userSex, int userAge){
        this.userID = userID;
        this.userPW = userPW;
        this.FName = FName;
        this.LName = LName;
        this.userBday = userBday;
        this.userSex = userSex;
        this.userAge = userAge;
    }
}
