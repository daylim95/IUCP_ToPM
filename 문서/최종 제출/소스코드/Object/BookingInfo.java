package com.example.knk.topm.Object;

import java.util.ArrayList;
import java.util.Date;

public class BookingInfo {
    String userID;                  // 예매자 아이디
    String scheduleKey;             // 예매한 스케줄 key
    int personnel;                  // 인원
    ArrayList<String> bookedSeats;  // 예매한 자리 번호 저장 (버튼 ID)
    Date screeningDate;             // 상영 시간
    String title;                   // 영화 제목

    public BookingInfo(String userID, String scheduleKey, int personnel, ArrayList<String> bookedSeats, Date screeningDate, String title) {
        this.userID = userID;
        this.scheduleKey = scheduleKey;
        this.personnel = personnel;
        this.bookedSeats = bookedSeats;
        this.screeningDate = screeningDate;
        this.title = title;
    }

    public Date getScreeningDate() {
        return screeningDate;
    }

    public void setScreeningDate(Date screeningDate) {
        this.screeningDate = screeningDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BookingInfo() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getScheduleKey() {
        return scheduleKey;
    }

    public void setScheduleKey(String scheduleKey) {
        this.scheduleKey = scheduleKey;
    }

    public int getPersonnel() {
        return personnel;
    }

    public void setPersonnel(int personnel) {
        this.personnel = personnel;
    }

    public ArrayList<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(ArrayList<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }
}
