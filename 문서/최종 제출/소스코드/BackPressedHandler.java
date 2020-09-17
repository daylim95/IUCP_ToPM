package com.example.knk.topm;

import android.app.Activity;
import android.widget.Toast;

// 다음 세가지 경우에 뒤로가기를 실행했을 경우 처리하는 핸들러
// 1. 일반 사용자 메인화면
// 2. 관리자 메인화면
// 3. 로그인 화면 (앱 실행 초기화면)
// 에서 뒤로가기를 한 번 누르고 2초 안에 한 번 더 눌렀을 경우
// 1,2는 로그아웃
// 3은 앱 종료가 된다.
public class BackPressedHandler {
    private long backKeyPressedTime = 0;                        // 뒤로가기를 누른 시간
    private Activity activity;                                  // 뒤로가기를 누른 액티비티
    private int mode;                                           // 로그인 화면일 경우 mode = 0, 메인화면일 경우 mode = 1
    private Toast toast;                                        // 토스트 메세지

    private final static int BACK_FROM_MAIN = 1;                // mode = 1 사용자 혹은 관리자 메인화면에서 뒤로 가기를 원하는 경우
    private final static int BACK_FROM_LOGIN = 0;               // mode = 0 로그인 화면에서 뒤로 가기를 원하는 경우

    // 생성자
    public BackPressedHandler(Activity activity, int mode) {
        this.activity = activity;
        this.mode = mode;
    }

    // 뒤로가기를 눌렀을 때 처리하는 함수
    public void onBackPressed(){
        // (이 함수가 불려진 시간 - 뒤로가기를 누른시간) > 2초 이면 종료가 실행되지 않음.
        if(System.currentTimeMillis() > backKeyPressedTime+2000){
            backKeyPressedTime = System.currentTimeMillis();
            showToast();
            return;
        }
        // (이 함수가 불려진 시간 - 뒤로가기를 누른시간) <= 2초 이면 현재 액티비트는 종료됨.
        else{
            activity.finish();
            toast.cancel();
        }
    }

    // 토스트 메세지를 띄워주는 함수
    public void showToast(){
        String guide = null;
        // 로그인화면에서 뒤로가기는 종료
        if(mode == BACK_FROM_LOGIN)
            guide = "\'뒤로\'버튼을 한번 더 누르시면 종료합니다.";
        // 메인화면에서 뒤로가기는 로그아웃
        else if(mode == BACK_FROM_MAIN)
            guide = "\'뒤로\'버튼을 한번 더 누르시면 로그아웃합니다.";
        toast = Toast.makeText(activity,guide,Toast.LENGTH_SHORT);
        toast.show();
    }
}
