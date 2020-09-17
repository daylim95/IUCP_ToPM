package com.example.knk.topm.Object;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    public String name;                             // 이름
    public String pw;                               // 비밀번호
    public String id;                               // ID
    public String birth;                            // 생년월일
    public boolean admin;                           // 관리자 여부
    public ArrayList<String> bookedSchedules;       // 예매한 내역, 예매내역 key를 저장한다.

    public User(String id, String pw, String name, String birth, boolean admin){
        this.id=id;
        this.pw=pw;
        this.name = name;
        this.birth=birth;
        this.admin = admin;
        bookedSchedules = new ArrayList<>();
        bookedSchedules.add("null");
    }

    public ArrayList<String> getBookedSchedules() {
        return bookedSchedules;
    }

    public void setBookedSchedules(ArrayList<String> bookedSchedules) {
        this.bookedSchedules = bookedSchedules;
    }

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
