package com.example.knk.topm.AdminActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.knk.topm.BackPressedHandler;
import com.example.knk.topm.CustomAdapters.ScheduleListAdapter;
import com.example.knk.topm.Object.MovieSchedule;
import com.example.knk.topm.Object.User;
import com.example.knk.topm.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdminMainActivity extends AppCompatActivity {

    private BackPressedHandler backPressedHandler;  // 뒤로가기 핸들러

    /* 상단뷰를 위한 변수 */
    private TextView dateTextView;          //현재 이동돼있는 날짜를 보여주는 텍스트뷰
    private Button prevBtn;                 //이전날짜로 가는 버튼
    private Button nextBtn;                 //이후날짜로 가는 버튼

    private ListView dayScheduleList;                   // 영화 스케줄 출력을 위한 리스트 뷰
    private ArrayList<MovieSchedule>[] scheduleData;    // 서버에 저장된 스케줄 데이터를 날짜별로 인덱스를 나눠 받아오는 "arrayList객체" 배열
    private ScheduleListAdapter schAdapter;             // 리스트뷰에 연결show하는 어댑터
    private final int SCH_LIST_ROW_LAYOUT_RESOURCE = R.layout.schedule_list_adpater_row2;

    final private String USER_PUTEXTRA_TAG = "user";
    private User user;                                          //유저정보 (여기선 관리자 정보) 가져옴.

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rootReference;

    private Date currentDate;               // 오늘 날짜
    private String strDate;                 // 오늘 날짜 문자열
    public int dateCount;

    /* 상수 */
    private final String schedule_ref = "schedule";
    final int FUTURE_DATE = 4;              // 미래 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        backPressedHandler = new BackPressedHandler(this,1);    // 뒤로가기 핸들러 초기화

        // 이전 액티비티에서 로그인한 User정보 가져옴
        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra(USER_PUTEXTRA_TAG);

        initViewAndDB();
        initScheduleListView();
    }

    // 뒤로가기 눌렀을 때 처리하는 함수
    @Override
    public void onBackPressed() {
        backPressedHandler.onBackPressed();
    }

    public void initViewAndDB(){

        // 상단 텍스트뷰, 버튼
        dateTextView = findViewById(R.id.dateTextView);
        prevBtn = findViewById(R.id.prevBtn);
        prevBtn.setEnabled(false);              // 초기화면은 무조건 오늘날짜이므로 이전 날짜 버튼 비활성화하기
        nextBtn = findViewById(R.id.nextBtn);

        // 데이터베이스 초기화
        dayScheduleList = findViewById(R.id.dayScheduleList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootReference = firebaseDatabase.getReference(schedule_ref);

    }

    public void initScheduleListView(){
        /* 2.날짜별 스케줄 */
        // 리스트뷰
        dayScheduleList = findViewById(R.id.dayScheduleList);   // 리스트뷰 초기화
        scheduleData = new ArrayList[FUTURE_DATE];              // 파이어베이스로부터 가져온 스케줄데이터를 저장하는 arrayList, 날짜별로 인덱스를 나눠 arrayList객체 배열 생성
        dateCount = 0;                                          // 0이면 오늘, 1, 2, 3일 +됨
        for(int i=0; i<FUTURE_DATE; i++) {                      // 오늘부터 최대 3일후까지만 생성
            scheduleData[i] = new ArrayList<>();
        }

        // 오늘 날짜 계산
        currentDate = new Date(System.currentTimeMillis());         // 시스템으로부터 받아온 오늘 날짜를 Date형으로 저장
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");   // 날짜 포맷
        strDate = sdf.format(currentDate);                          // 오늘 날짜를 날짜 포맷에 맞게 변형
        dateTextView.setText(strDate);                              // 오늘 날짜로 설정한 날짜를 보여주는 텍스트뷰에 setText

        Calendar today = Calendar.getInstance();                    // 캘린더 객체 오늘의 날짜
        today.add(Calendar.DATE,-1);                        // 오늘 날짜에서 하루 뺌. (즉, 어제)

        // 미래 날짜 계산 후 오늘날짜로부터 이후 3일까지의 스케줄 데이터 arrayList 객체배열에 저장
        for(int i=0; i<FUTURE_DATE; i++) {

            today.add(Calendar.DATE, 1);                    // 날짜 하루씩 더하기 for문 순서대로 돌면서, 오늘,내일,2일뒤,3일뒤 계산
            strDate = sdf.format(today.getTime());                  // yyyy년 MM월 dd일
            final int index = i;

            // 스케줄 데이터베이스 변경 이벤트 핸들러
            rootReference.child(strDate).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    MovieSchedule newSchedule = dataSnapshot.getValue(MovieSchedule.class); // 새로 추가된 스케줄 받아옴
                    scheduleData[index].add(newSchedule);                                   // 리스트 뷰에 갱신
                    schAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        // 어댑터에는 현재액티비티의 context, 뿌려줄 리스트뷰 한 줄(row)의 레아이웃, 뿌려줄 데이터 정보
        // init 함수 안이므로 오늘 날짜에 맞게 어댑터 달기
        schAdapter = new ScheduleListAdapter(this,SCH_LIST_ROW_LAYOUT_RESOURCE, scheduleData[0]);
        dayScheduleList.setAdapter(schAdapter);
    }

    public void movieEditClick(View view) {
        startActivity(new Intent(this, MovieManageActivity.class));
    }

    public void screenEditClick(View view) {
        startActivity(new Intent(this, ScreenListActivity.class));
    }

    public void scheduleEditClick(View view) {
        startActivity(new Intent(this, ScheduleManageActivity.class));
    }

    public String dateCalculator(int n) {
        String str;
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, n);
        Date future = today.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        str = sdf.format(future);
        return str;
    }

    public void prevBtnClick(View view) {
        // 미래 날짜를 보고 있을 때만 동작하도록
        if(dateCount != 0) {
            dateCount--; // 하루 감소
            nextBtn.setEnabled(true); // 다음 버튼 활성화
            String str = dateCalculator(dateCount);
            dateTextView.setText(str);

            if(dateCount == 0) // 오늘 날짜에 도달
                prevBtn.setEnabled(false);  //이전버튼 비활성화
            // 어댑터 새로 정의
            schAdapter = new ScheduleListAdapter(this, SCH_LIST_ROW_LAYOUT_RESOURCE, scheduleData[dateCount]);
            dayScheduleList.setAdapter(schAdapter); // 어댑터 새로 설정
            schAdapter.notifyDataSetChanged(); // 변경 통지
        }

    }

    public void nextBtnClick(View view) {
        // 가장 미래 날짜를 보고 있으면 안됨
        if(dateCount < FUTURE_DATE) {
            dateCount++; // 하루 증가
            prevBtn.setEnabled(true); // 다음 버튼 활성화
            String str = dateCalculator(dateCount);
            dateTextView.setText(str);

            if(dateCount == FUTURE_DATE - 1) // 가장 미래 날짜에 도달
                nextBtn.setEnabled(false);

            // 어댑터 새로 정의
            schAdapter = new ScheduleListAdapter(this,SCH_LIST_ROW_LAYOUT_RESOURCE, scheduleData[dateCount]);
            dayScheduleList.setAdapter(schAdapter); // 어댑터 새로 설정
            schAdapter.notifyDataSetChanged(); // 변경 통지
        }
    }
}
