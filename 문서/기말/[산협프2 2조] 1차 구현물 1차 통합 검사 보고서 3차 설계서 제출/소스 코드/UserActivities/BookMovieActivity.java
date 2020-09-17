package com.example.knk.topm.UserActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knk.topm.Object.BookingInfo;
import com.example.knk.topm.Object.InputException;
import com.example.knk.topm.Object.MovieSchedule;
import com.example.knk.topm.Object.MyButton;
import com.example.knk.topm.Object.Screen;
import com.example.knk.topm.Object.User;
import com.example.knk.topm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BookMovieActivity extends AppCompatActivity {

    User user;                                      // 현재 로그인한 유저
    String scheduleKey;                             // 이전 액티비티에서 넘겨준 데이터베이스 접근 키
    String strDate;                                 // 이전 액티비티에서 넘겨준 상영 날짜 스트링
    String screenKey;                               // 스케줄 키
    MovieSchedule movieSchedule;                    // 해당 스케쥴
    Screen screen;                                  // 해당 스크린
    MyButton[] seats;                               // 좌석
    int size;
    int personnel;                                  // 인원 저장
    int personnelCount;                             // 좌석을 클릭한 갯수 카운트하는 변수\
    ArrayList<String> bookedSeats;                  // 예매한 좌석 버튼 ID저장
    boolean personnelCheck;                         // 인원 입력이 완료되었는지 확인

    EditText editPersonnel;                         // 인원 받는 EditText

    HashMap<String, Boolean> abled;                 // DB에서 받아온 좌석인지 아닌지 정보 저장
    HashMap<String, Boolean> booked;                // DB에서 받아온 예매 되었는지 아닌지 정보 저장
    HashMap<String, Boolean> special;               // DB에서 받아온 우등석인지 아닌지 정보 저장
    HashMap<String, Boolean> tempBooked;                   // 현재 사용자가 선택한 좌석(아직 예매 확정되지 않은 좌석)을 저장

    // 레이아웃
    RelativeLayout totalLayout;

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

    // 올해 저장 - 나이제한 대비
    final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_movie);

        init();

    }

    public void init() {
        // View를 초기화
        final TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        editPersonnel = (EditText) findViewById(R.id.editPersonnel);
        personnelCount = 0;
        personnelCheck = false;
        bookedSeats = new ArrayList<>();
        tempBooked = new HashMap<>();

        // 이전 액티비티에서 전송한 정보 수신
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");  // 현재 로그인 한 유저
        scheduleKey = intent.getStringExtra("key");         // 스케줄 키
        strDate = intent.getStringExtra("date");            // 날짜

        // 데이터베이스
        firebaseDatabase = FirebaseDatabase.getInstance();
        scheduleReference = firebaseDatabase.getReference(schedule_ref);
        screenReference = firebaseDatabase.getReference(screen_ref);
        userReference = firebaseDatabase.getReference(user_ref);
        bookingInfoReference = firebaseDatabase.getReference(bookingInfo_ref);

        // 리스너 달기
        scheduleReference.child(strDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(scheduleKey)) {
                        movieSchedule = data.getValue(MovieSchedule.class); // 객체 저장

                        // 객체를 가지고 온 후 객체의 정보로 초기화 하는 변수는 이 안에서 초기화 해야합니다.
                        // 왜냐하면, 이 리스너가 Asynchronous 하기 때문입니다.
                        booked = movieSchedule.getBookedMap();
                        screenKey = movieSchedule.getScreenNum() + "관";       // 스크린 키
                        titleTextView.setText(movieSchedule.getMovieTitle());
                        break;
                    }
                }
                tempBooked.putAll(booked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        // saveToDataBase();   // 데이터베이스에 저장

        // 버튼 클릭 리스너 설정
        for (int k = 0; k < seats.length ; k++) {
            seats[k].setTag(k);
            final int index = Integer.parseInt(screen.getScreenNum()) * 1000 + (k + 1); // 실제 DB에 저장되어있는 버튼의 ID값
            seats[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(personnelCheck) {
                        // 인원 입력이 완료되었을 경우에만 좌석 선택이 가능하다.
                        if(personnelCount < personnel) {
                            // 입력 인원보다 작을 시에만 좌석 클릭이 가능하고요
                            if(tempBooked.get(String.valueOf(index)).equals(MyButton.UNBOOKED)) {
                                // 현재 선택한 자리가 아닌 경우
                                // 선택한다
                                tempBooked.put(String.valueOf(index), MyButton.BOOKED); // 선택으로 상태 변경
                                v.setBackgroundResource(R.drawable.movie_seat_select);  // 좌석 이미지 변경
                                personnelCount++;                                       // 인원 수 증가
                                bookedSeats.add(String.valueOf(index));                 // 예매 목록에 추가
                            }
                            else if(tempBooked.get(String.valueOf(index)).equals(MyButton.BOOKED) && booked.get(String.valueOf(index)).equals(MyButton.UNBOOKED)){
                                // 현재 선택했고, 예매되지는 않은 자리
                                // 선택을 취소한다.
                                tempBooked.put(String.valueOf(index), MyButton.UNBOOKED);   // 비선택으로 상태 변경
                                v.setBackgroundResource(R.drawable.movie_seat_ok);      // 좌석 이미지 변경
                                personnelCount--;                                       // 인원 수 감소
                                bookedSeats.remove(String.valueOf(index));              // 예매 목록에서 제거
                            }
                        }
                        else if (personnel == personnel) {
                            // 선택 인원과 입력 인원이 같은 경우
                            // 선택한 좌석 취소만 가능
                            if(tempBooked.get(String.valueOf(index)).equals(MyButton.BOOKED) && booked.get(String.valueOf(index)).equals(MyButton.UNBOOKED)) {
                                // 선택을 취소하려고 하는 경우
                                tempBooked.put(String.valueOf(index), MyButton.UNBOOKED);   // 비선택으로 상태 변경
                                v.setBackgroundResource(R.drawable.movie_seat_ok);      // 좌석 이미지 변경
                                personnelCount--;                                       // 인원 수 감소
                                bookedSeats.remove(String.valueOf(index));              // 예매 목록에서 제거

                            }
                            else {
                                // 추가로 선택하려고 하는 경우
                                Toast.makeText(BookMovieActivity.this, "이미 모두 선택했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(BookMovieActivity.this, "인원 선택을 완료하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
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
        colChars =  alphabet.split("");

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
                if(booked.get(String.valueOf(id)).equals(MyButton.BOOKED)) {
                    // 예매 된 자리라면
                    seats[i].setBackgroundResource(R.drawable.movie_seat_selled);
                }
                else {
                    // 예매 되지 않은 자리라면
                    seats[i].setBackgroundResource(R.drawable.movie_seat_ok);
                }
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

    public void saveToDataBase() {

        /* MovieSchedule 갱신 */
        movieSchedule.setBookedMap(tempBooked);                             // bookedMap을 갱신한다.
        scheduleReference.child(strDate).child(scheduleKey).setValue(movieSchedule);   // 객체를 데이터베이스에 업로드한다.
        Toast.makeText(this, "영화 예매가 완료되었습니다.", Toast.LENGTH_SHORT).show();

        // 유저의 예매 리스트에 추가하기!!!
        BookingInfo bookingInfo = new BookingInfo(user.getId(), scheduleKey, personnel, bookedSeats, movieSchedule.getScreeningDate(), movieSchedule.getMovieTitle());   // bookingInfo 객체 생성
        String bookingInfoKey = user.getId() + " " + scheduleKey;        // 예매 정보 키 : 유저 아이디 + 스케줄 키

        bookingInfoReference.child(bookingInfoKey).setValue(bookingInfo);
        user.bookedSchedules.add(bookingInfoKey);                        // 사용자의 예매 목록에 이번 예매의 키를 추가한다.
        userReference.child(user.getId()).setValue(user);                // 데이터베이스에 유저 정보 갱신

        // 나의 예매 내역으로 이동
        Intent intent = new Intent(this, MyBookingListActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    public void completeBtnClicked(View view) {
        if(personnelCount == personnel) {
            // 입력 인원만큼 좌석을 선택한 경우
            saveToDataBase();               // 데이터베이스에 저장합니다. ^^
        }
        else {
            // 아닌 경우
            Toast.makeText(this, "입력 인원만큼 좌석을 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean personnelInputComplete(View view) {
        // 인원 정보 입력 완료
        String str = editPersonnel.getText().toString();
        
        if(str.equals("")) {
            // 입력 되지 않음
            try {
                throw new InputException();
            } catch (InputException e) {
                Toast.makeText(this, "입력을 확인하세요.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }
        else {
            // 입력됨
            personnel = Integer.parseInt(str);

            if(personnel <= 0) {
                // 0보다 같거나 작다면
                try {
                    throw new InputException();
                } catch (InputException e) {
                    Toast.makeText(this, "입력을 확인하세요.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return false;
                }
            }
            else {
                // 0보다 큰 숫자 입력시 입력창, 버튼 비활성화
                Button personnelInputBtn = (Button) findViewById(R.id.personnelInputBtn);
                personnelInputBtn.setEnabled(false);
                editPersonnel.setEnabled(false);
                personnelCheck = true;
                return true;
            }
        }
    }
}
