package com.example.knk.topm.Object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Screen {

    int row;                // 행
    int col;                // 열
    int totalSeats;         // 총 좌석 개수
    String screenNum;       // 상영관 번호

    HashMap<String, Boolean> abledMap;      // 좌석인지 아닌지 여부 저장
    HashMap<String, Boolean> specialMap;    // 우등석인지 아닌지 여부 저장
    HashMap<String, Boolean> coupleMap;     // 커플석인지 아닌지 여부 저장

    public Screen(int row, int col, String screenNum/*, ArrayList IDs*/) {
        // 변수 초기화
        this.row = row;
        this.col = col;

        totalSeats = row * col;
        this.screenNum = screenNum;

        abledMap = new HashMap<>();
        specialMap = new HashMap<>();
    }

    public Screen() {
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }


    public HashMap<String, Boolean> getAbledMap() {
        return abledMap;
    }

    public void setAbledMap(HashMap<String, Boolean> abledMap) {
        this.abledMap = abledMap;
    }

    public HashMap<String, Boolean> getSpecialMap() {
        return specialMap;
    }

    public void setSpecialMap(HashMap<String, Boolean> specialMap) {
        this.specialMap = specialMap;
    }

    public String getScreenNum() {
        return screenNum;
    }

    public void setScreenNum(String screenNum) {
        this.screenNum = screenNum;
    }

    public int getTotalSeats() {
        // 전체 좌석 수 반환
        int total = row * col;          // 정사각형 좌석 수
        Set set = abledMap.keySet();    // 버튼 아이디 set 가져옴
        Iterator iterator = set.iterator();

        int unabledSeatsNum = 0;        // 좌석이 아닌 것의 개수 저장할 것

        // 모든 키에 대해서 검사
        while(iterator.hasNext()) {
            if((abledMap.get((String) iterator.next())).equals(MyButton.UNABLED)) {
                // 좌석이 아니라면
                unabledSeatsNum++;      // 좌석이 아닌 것의 개수 증가
            }
        }

        totalSeats -= unabledSeatsNum;  // 전체 숫자에서 빼기

        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public HashMap<String, Boolean> getCoupleMap() {
        return coupleMap;
    }

    public void setCoupleMap(HashMap<String, Boolean> coupleMap) {
        this.coupleMap = coupleMap;
    }


}
