package com.example.exfch;

public class CheckInf {
    public int usernum = 0;
    public int check = 0;

    public String[] n;
    public String[] getN() { return n;}
    public void setN(String[] n) { this.n = n;}

    public int idTrue = 0, idFalse = 1;

    public String checkid, checkpw;
    public String checkfname, checklname, checkbday, checksex;
    public String userColor = null;
    public int checkage = 0;

    public String getId() {
        return this.checkid;
    }
    public void setId(String checkid) {
        this.checkid = checkid;
    }

    public String getPw() {
        return this.checkpw;
    }
    public void setPw(String checkpw) {
        this.checkpw = checkpw;
    }

    public String getFName() {
        return this.checkfname;
    }
    public void setFName(String checkfname) {
        this.checkfname = checkfname;
    }

    public String getLName() {
        return this.checklname;
    }
    public void setLName(String checklname) {
        this.checklname = checklname;
    }

    public String getBday() {
        return this.checkbday;
    }
    public void setBday(String checkbday) {
        this.checkbday = checkbday;
    }

    public String getSex() {
        return this.checksex;
    }
    public void setSex(String checksex) {
        this.checksex = checksex;
    }

    public int getAge() {
        return this.checkage;
    }
    public void setAge(int checkage) {
        this.checkage = checkage;
    }

    public int getidTrue() { return this.idTrue; }
    public void setidTrue(int idTrue) { this.idTrue = idTrue; }

    public int getidFalse() { return this.idFalse; }
    public void setidFalse(int idFalse) { this.idFalse = idFalse; }

    public int getCheck() { return this.check; }
    public void setCheck(int check) { this.check = check; }

    public String getUserColor() {
        return this.userColor;
    }
    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

    public int getUsernum() { return this.usernum; }
    public void setUsernum(int usernum) { this.usernum = usernum; }

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
