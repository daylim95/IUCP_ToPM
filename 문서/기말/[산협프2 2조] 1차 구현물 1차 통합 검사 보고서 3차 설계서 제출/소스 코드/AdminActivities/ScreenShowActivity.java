package com.example.knk.topm.AdminActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.knk.topm.Object.MyButton;
import com.example.knk.topm.Object.Screen;
import com.example.knk.topm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ScreenShowActivity extends AppCompatActivity {

    Screen screen;          // 해당 상영관
    int screenNum;          // 상영관 번호
    String screenKey;       // "n관' DB에 접근할 때 사용
    HashMap<String, Boolean> abled;         // 좌석들의 좌석인지 아닌지 여부 저장
    HashMap<String, Boolean> special;       // 좌석들이 우등석인지 아닌지 여부 저장
    int size;               // 상영관의 가로*세로 크기
    MyButton[] seats;       // 좌석들

    // 레이아웃
    RelativeLayout totalLayout;

    // 데이터베이스
    private FirebaseDatabase firebaseDatabase;      // firebaseDatabase
    private DatabaseReference screenReference;        //rootReference
    final private static String screen_ref = "screen";    // 스크린 레퍼런스로 가는 키

    /* 상수 */
    final static int DEFAULT_VALUE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_show);

        init();
    }

    public void init() {
        // 이전 액티비티에서 전송한 정보 받아오기
        Intent intent = getIntent();
        screenNum = intent.getIntExtra("SCREENID1", DEFAULT_VALUE);     // 상영관 번호
        screenKey = String.valueOf(screenNum) + "관";

        /* 뷰 초기화 */
        TextView screenTextView = (TextView) findViewById(R.id.screenTextView);
        screenTextView.setText(String.valueOf(screenNum) + "관");

        initDB();
    }

    public void initDB() {
        // 데이터베이스 초기화
        firebaseDatabase = FirebaseDatabase.getInstance();
        screenReference = firebaseDatabase.getReference(screen_ref);

        // 리스너 달기
        screenReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {

                    if(data.getKey().equals(screenKey)) {
                        screen = data.getValue(Screen.class);
                        size = screen.getRow() * screen.getCol();
                        abled = screen.getAbledMap();
                        special = screen.getSpecialMap();
                        printSeats();       // 좌석 출력
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void printSeats() {
        totalLayout = (RelativeLayout) findViewById(R.id.totalLayout);            // 전체 좌석을 출력하는 레이아웃
        seats = new MyButton[size];                                               // 사이즈 개수 만큼 버튼 생성

        createLayout();     // 레이아웃 생성
        assignButtonID();   // 버튼 ID 할당
    }

    public void createLayout() {
        // 버튼들 출력할 레이아웃을 생성하는 함수

        // index layout 부분
        for(int count=0; count<screen.getRow(); count++) {   // 행 index

            // 행 번호 출력
            RelativeLayout rowLayout = new RelativeLayout(this);
            TextView rowNum = new TextView(this);
            rowNum.setText(String.valueOf(count+1));

            if(count > 8){
                rowNum.setTextSize(10);   // x>10일 경우 이상헤게 나오기때문에 예외처리임
            }

            RelativeLayout.LayoutParams indexRlayout = new RelativeLayout.LayoutParams(40, 40);//(textview의 크기)
            indexRlayout.topMargin = 0;
            indexRlayout.leftMargin = 10 + (count * 50)+50;// 10( x0 left 까지 거리  )+(생선할때마다 50px 추가함)+(제일 왼쪽 index 빈칸으로 제움)
            rowLayout.addView(rowNum, indexRlayout);
            totalLayout.addView(rowLayout);
        }

        // 열 번호에 해당하는 알파벳 할당해 배열에 저장
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String[] colChars = new String[alphabet.length()];
        colChars = alphabet.split("");

        for(int count=0; count<screen.getCol(); count++) {     // 열 index

            // 열 번호 출력
            RelativeLayout colLayout = new RelativeLayout(this);
            TextView colNum = new TextView(this);
            colNum.setText(colChars[count+1]);      // 미리 알파벳을 저장했던 배열에서 가져와 출력

            RelativeLayout.LayoutParams indexRlayout = new RelativeLayout.LayoutParams(40, 40);
            indexRlayout.topMargin = 10 + (count * 50)+30;
            indexRlayout.leftMargin = 0;
            colLayout.addView(colNum, indexRlayout);
            totalLayout.addView(colLayout);
        }

    }

    public void assignButtonID() {
        // 각각의 버튼에 아이디를 할당하는 함수

        int j = 0;   // 행당 버튼 개수 count 하는 변수

        // ▼ DB에 스크린 아이디를 저장하기 위한 과정
        int screen_Hall_ID_Count = Integer.parseInt(screen.getScreenNum()) * 1000 + 1; // n관일 경우, 버튼의 아이디는 n001부터 시작함.

        for (int i = 0; i < size; i++) {                     // 1차원 배열로 저장

            seats[i] = new MyButton(this);           // 객체 생성
            int id = screen_Hall_ID_Count + i;
            seats[i].setId(id);           // n001 부터 시작해 모든 버튼에 ID 할당

            if(abled.get(String.valueOf(id)).equals(MyButton.UNABLED)) {
                // 좌석이 아닌 곳이라면..
                seats[i].setVisibility(View.INVISIBLE); // 자리는 차지하되 보이지는 않음
            }
            else {
                // 좌석인 곳이라면..
                seats[i].setBackgroundResource(R.drawable.movie_seat_ok);
            }

            seats[i].setText("" + i);
            seats[i].setTextSize(0, 8);         // 글자 크기
            RelativeLayout.LayoutParams RL = new RelativeLayout.LayoutParams(40, 40);  //(40,40)-> 버튼의 크기: 40=50-10

            //***************** row * col 배치하기
            if (i % screen.getRow() == 0) {    // >row 시 다음행으로 넘어가기
                j++;
            }
            //RL.leftMargin = 50 * (i % col);    // index 때문에 수치바꿈
            RL.leftMargin = 50+50 * (i % screen.getRow());     // 50는 행간 사이입니다   . 추가된 50은 index (0,0)위치의 빈칸입니다 .
            RL.topMargin = j * 50;            // 50는 열간 사이입니다
            totalLayout.addView(seats[i], RL);        //mybutton 출력함
        }

    }

    public void screenEditClick(View view) {
        // 상영관 수정 화면으로 이동
        Intent intent = new Intent(this, ScreenEditActivity1.class);
        intent.putExtra("SCREENID1", screenNum);    // 상영관 번호 보내줌
        startActivity(intent);                            // 액티비티 전환
    }
}
