package com.example.knk.topm.Object;

import java.util.Date;
import java.util.HashMap;

public class MovieSchedule {

    String movieTitle;                          // 영화 이름
    String screenNum;                           // 상영관번호

    HashMap<String, Boolean> bookedMap;

    public Date screeningDate; // 상영 날짜
    int bookedSeats; // 예약된 좌석 숫자
    int restSeats; // 남은 좌석 숫자
    int endHour;
    int endMin;
    int runningTime;

    public MovieSchedule() {

    }

    public MovieSchedule(String movieTitle, String screenNum, Date screeningDate, int runningTime) {
        this.movieTitle = movieTitle;
        this.screenNum = screenNum;
        this.screeningDate = screeningDate;
        this.bookedSeats = 0;

        this.restSeats = 0; // 일단

        bookedMap = new HashMap<>();
        this.runningTime = runningTime;
        calculateEndTime();
    }

    // 런닝타임 정보를 더해 끝나는 시간과 분 계산, 24시 후의 경우에 관해서는 계산되지 않았음.
    // 25시, 26시 ...일때는 날짜가 넘어가지만 그대로 25시 26시로 간주.
    public void calculateEndTime(){
        endMin = (screeningDate.getMinutes()+runningTime)%60;
        endHour = screeningDate.getHours() + (screeningDate.getMinutes()+runningTime)/60;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getScreenNum() {
        return screenNum;
    }

    public void setScreenNum(String screenNum) {
        this.screenNum = screenNum;
    }

    public Date getScreeningDate() {
        return screeningDate;
    }

    public void setScreeningDate(Date screeningDate) {
        this.screeningDate = screeningDate;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public int getRestSeats() {
        return restSeats;
    }

    public void setRestSeats(int restSeats) {
        this.restSeats = restSeats;
    }

    public HashMap<String, Boolean> getBookedMap() {
        return bookedMap;
    }

    public void setBookedMap(HashMap<String, Boolean> bookedMap) {
        this.bookedMap = bookedMap;
    }

}
