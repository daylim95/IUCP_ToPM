package com.example.knk.topm.AdminActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knk.topm.Object.MyButton;
import com.example.knk.topm.Object.Screen;
import com.example.knk.topm.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ScreenEditActivity2 extends AppCompatActivity {

    int row;        // 행
    int col;        // 열
    int size ;      // size = row *col
    String screenNum; // "n"
    String screenKey; // "n관"

    HashMap<String, Boolean> abled;   // 좌석인지 아닌지 여부 저장
    HashMap<String, Boolean> special; // 우등석인지 아닌지 여부 저장
    
    String colChars[];
    int Screen_ID_buff;

    MyButton seats[];
    RelativeLayout rowLayout,colLayout; // 행과 열의 인덱스를 출력할 레이아웃
    RelativeLayout totalLayout;         // 좌석 전체를 출력할 레이아웃

    /* 데이터베이스 */
    private FirebaseDatabase firebaseDatabase;           // firebaseDatabase
    private DatabaseReference screenReference;           // rootReference

    final int DEFAUL_VALUE = 5; // 전송 실패시 디폴트값 10

    /* 상수 */
    final private static String screen_ref = "screen";          // 상영관 레퍼런스로 가는 키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_edit2);

        init();
        
    }

    public void init() {
        // db
        firebaseDatabase = FirebaseDatabase.getInstance();
        screenReference = firebaseDatabase.getReference(screen_ref);

        // 이전 액티비티에서 전송한 정보 수신
        Intent intent = getIntent();
        row = intent.getIntExtra("row", DEFAUL_VALUE);      // 행 정보
        col = intent.getIntExtra("col", DEFAUL_VALUE);      // 열 정보

        abled = new HashMap<>();
        special = new HashMap<>();

        // ScreenEditActivity1 에서 ScreenId 받아오기
        Screen_ID_buff = intent.getIntExtra("SCREENID2", -1);

        // 관 이름 문자열로 생성
        screenNum = String.valueOf(Screen_ID_buff);
        screenKey = Screen_ID_buff + "관";

        // 상영관 좌석 사이즈 할당
        size = row * col;
        
        // 열 번호에 해당하는 알파벳 할당해 배열에 저장
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        colChars = new String[alphabet.length()];
        colChars =  alphabet.split("");

        totalLayout = (RelativeLayout) findViewById(R.id.totalLayout);             // 전체 좌석을 출력하는 레이아웃
        seats = new MyButton[size];                                 // 사이즈 개수 만큼 버튼 생성

        createLayout();     // 레이아웃 생성
        assignButtonID();   // 버튼 ID 할당
        saveToDataBase();   // 데이터베이스에 저장

        for (int k = 0; k < size; k++) {
            seats[k].setTag(k);
            final int index = Integer.parseInt(screenNum) * 1000 + (k + 1); // 실제 DB에 저장되어있는 버튼 ID값

            seats[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 좌석 버튼 클릭 이벤트
                    if(abled.get(String.valueOf(index)).equals(MyButton.ABLED)) {
                        // 현재 좌석인 자리라면
                        abled.put(String.valueOf(index), MyButton.UNABLED);     // 좌석이 아닌 상태로 바꾸어주고
                        v.setBackgroundResource(R.drawable.movie_seat_repair);  // 이미지 바꾸어줌
                    }
                    else if(abled.get(String.valueOf(index)).equals(MyButton.UNABLED)){
                        // 현재 좌석이 아닌 자리라면
                        abled.put(String.valueOf(index), MyButton.ABLED);     // 좌석이 아닌 상태로 바꾸어주고
                        v.setBackgroundResource(R.drawable.movie_seat_ok);  // 이미지 바꾸어줌
                    }
                }
            });
        }
    }

    public void createLayout() {
        // 버튼들 출력할 레이아웃을 생성하는 함수
        
        // index layout 부분
        for(int count=0; count<row; count++) {   // 행 index

            // 행 번호 출력
            rowLayout = new RelativeLayout(this);
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

        for(int count=0; count<col; count++) {     // 열 index

            // 열 번호 출력
            colLayout = new RelativeLayout(this);
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
        int Scree_Hall_ID_Count = Screen_ID_buff * 1000 + 1; // n관일 경우, 버튼의 아이디는 n001부터 시작함.
        for (int i = 0; i < size; i++) {                     // 1차원 배열로 저장

            seats[i] = new MyButton(this);           // 객체 생성
            seats[i].setId(Scree_Hall_ID_Count + i);           // n001 부터 시작해 모든 버튼에 ID 할당

            seats[i].setBackgroundResource(R.drawable.movie_seat_ok);   // 배경 png로 바꿈
            seats[i].setText("" + i);
            seats[i].setTextSize(0, 8);         // 글자 크기
            RelativeLayout.LayoutParams RL = new RelativeLayout.LayoutParams(40, 40);  //(40,40)-> 버튼의 크기: 40=50-10

            //***************** row * col 배치하기
            if (i % row == 0) {    // >row 시 다음행으로 넘어가기
                j++;
            }
            //RL.leftMargin = 50 * (i % col);    // index 때문에 수치바꿈
            RL.leftMargin = 50+50 * (i % row);     // 50는 행간 사이입니다   . 추가된 50은 index (0,0)위치의 빈칸입니다 .
            RL.topMargin = j * 50;                 // 50는 열간 사이입니다
            totalLayout.addView(seats[i], RL);        //mybutton 출력함
            // this.setContentView(totalLayout);
        }
    }

    public void saveToDataBase() {
        // 새로 입력한 행, 열 정보를 토대로
        // 데이터베이스에 저장

        // 먼저 ID들을 받아온 다음에
        ArrayList IDs = new ArrayList();

        // 초기화
        for(int i=0; i<size; i++) {
            String strID = String.valueOf(seats[i].getId()) ;
            IDs.add(seats[i].getId());                      // 아이디 저장
            abled.put(strID, MyButton.ABLED);  // 좌석인지 아닌지 저장
            special.put(strID, MyButton.UNABLED);  // 우등석인지 아닌지 저장
        }

        Screen newScreen = new Screen(row, col, screenNum/*, IDs*/);     // 객체 생성
        newScreen.setAbledMap(abled);                                     // 해쉬 맵 갱신
        newScreen.setSpecialMap(special);                                // 해쉬 맵 갱신
        screenReference.child(screenKey).setValue(newScreen);        // 저장

        // 이 함수는 최종적으로 상영관 정보를 저장할 때 한 번 더 불러와져야 합니다.
    }

    public void screenEditComplete(View view) {
        // 상영관 수정을 완료하면 호출하는 함수

        Screen newScreen = new Screen(row, col, screenNum/*, IDs*/);     // 객체 생성
        newScreen.setAbledMap(abled);
        newScreen.setSpecialMap(special);
        screenReference.child(screenKey).setValue(newScreen);        // 저장
        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}