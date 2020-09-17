package com.example.knk.topm.Object;

public class Movie {

    int runningTime; // 상영 시간
    String title; // 제목
    String director; // 감독
    int rating; // 상영 등급

    public Movie(int runningTime, String title, String director, int rating) {
        this.runningTime = runningTime;
        this.title = title;
        this.director = director;
        this.rating = rating;
    }

    public Movie() {

    }

    public int getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(int runningTime) {
        this.runningTime = runningTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return this.title;
        //return super.toString();
    }
}
