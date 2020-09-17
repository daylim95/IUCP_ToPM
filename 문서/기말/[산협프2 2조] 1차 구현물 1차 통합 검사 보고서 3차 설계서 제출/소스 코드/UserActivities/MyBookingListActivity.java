package com.example.knk.topm.UserActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.knk.topm.CustomAdapters.BookingListAdapter;
import com.example.knk.topm.Object.BookingInfo;
import com.example.knk.topm.Object.User;
import com.example.knk.topm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBookingListActivity extends AppCompatActivity {

    User user;                                           // 현재 유저
    private ListView myBookingList;
    String strDate;                                      // 오늘 날짜 스트링
    ArrayList<BookingInfo> bookingData;                  // 예매 내역 받아와서 저장할 배열
    BookingListAdapter adapter;

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
        setContentView(R.layout.activity_my_booking_list);

        init();
    }

    public void init() {
        bookingData = new ArrayList<>();
        adapter = new BookingListAdapter(this, R.layout.booking_list_adapter_row, bookingData);
        myBookingList = (ListView) findViewById(R.id.myBookingList);
        myBookingList.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        // 이전 액티비티에서 전송한 내용 수신
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");      // 현재 로그인 중인 회원
        strDate = intent.getStringExtra("date");                // 오늘 날짜

        getMyBookingListFromDB();
    }

    // 데이터 베이스에서 내 예매목록 받아오기
    public void  getMyBookingListFromDB() {

        // 데이터베이스
        firebaseDatabase = FirebaseDatabase.getInstance();
        scheduleReference = firebaseDatabase.getReference(schedule_ref);
        screenReference = firebaseDatabase.getReference(screen_ref);
        userReference = firebaseDatabase.getReference(user_ref);
        bookingInfoReference = firebaseDatabase.getReference(bookingInfo_ref);

        bookingInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    BookingInfo b = data.getValue(BookingInfo.class);

                    if(b.getUserID().equals(user.getId())) {
                        // 현재 유저가 예매한 정보만
                        bookingData.add(b);
                        adapter.notifyDataSetChanged();
                    }
                    else continue;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
