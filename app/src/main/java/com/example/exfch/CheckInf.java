package com.example.exfch;

public class CheckInf {
    public int usernum = 0;
    public int check = 0;
    public int check2 = 0;
    public int check3 = 0;
    public int check4 = 0;
    public int check5 = 0;

    public String checkid, checkpw;
    public String checkfname, checklname, checkbday, checksex;
    public int checkage = 0;

    public String getId() {
        return checkid;
    }
    public void setId(String checkid) {
        this.checkid = checkid;
    }

    public String getPw() {
        return checkpw;
    }
    public void setPw(String checkpw) {
        this.checkpw = checkpw;
    }

    public String getFName() {
        return checkfname;
    }
    public void setFName(String checkfname) {
        this.checkfname = checkfname;
    }

    public String getLName() {
        return checklname;
    }
    public void setLName(String checklname) {
        this.checklname = checklname;
    }

    public String getBday() {
        return checkbday;
    }
    public void setBday(String checkbday) {
        this.checkbday = checkbday;
    }

    public String getSex() {
        return checksex;
    }
    public void setSex(String checksex) {
        this.checksex = checksex;
    }

    public int getAge() {
        return checkage;
    }
    public void setAge(int checkage) {
        this.checkage = checkage;
    }

    public CheckInf() {}
    public CheckInf(String checkfname, String checklname, String checkid, String checkpw, String checkbday, String checksex, int checkage) {
        this.checkfname = checkfname;
        this.checklname = checklname;
        this.checkid = checkid;
        this.checkpw = checkpw;
        this.checkbday = checkbday;
        this.checksex = checksex;
        this.checkage = checkage;
    }
}
