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

    public MovieSchedule() {

    }

    public MovieSchedule(String movieTitle, String screenNum, Date screeningDate) {
        this.movieTitle = movieTitle;
        this.screenNum = screenNum;
        this.screeningDate = screeningDate;
        this.bookedSeats = 0;

        this.restSeats = 0; // 일단

        bookedMap = new HashMap<>();
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
