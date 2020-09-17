package com.example.knk.topm.UserActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.knk.topm.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowMovieInfoActivity extends AppCompatActivity {

    // 데이터베이스
    private FirebaseDatabase firebaseDatabase;      // firebaseDatabase
    private DatabaseReference scheduleReference;        // rootReference
    private DatabaseReference screenReference;        //rootReference
    private DatabaseReference userReference;
    private DatabaseReference bookingInfoReference;
    final private static String schedule_ref = "schedule";    // 스케줄 레퍼런스로 가는 키
    final private static String screen_ref = "screen";    // 스크린 레퍼런스로 가는 키
    final private static String user_ref = "user";    // 사용자 레퍼런스로 가는 키
    final private static String bookingInfo_ref = "bookingInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movie_info);
    }
}