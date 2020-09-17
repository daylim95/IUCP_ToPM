package com.example.knk.topm.AdminActivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.knk.topm.CustomAdapters.ScheduleListAdapter;
import com.example.knk.topm.Object.BookingInfo;
import com.example.knk.topm.Object.InputException;
import com.example.knk.topm.Object.Movie;
import com.example.knk.topm.Object.MovieSchedule;
import com.example.knk.topm.Object.MyButton;
import com.example.knk.topm.Object.Screen;
import com.example.knk.topm.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ScheduleManageActivity extends AppCompatActivity implements ScheduleListAdapter.ScheduleDeleteBtnClickListener {   //스케쥴리스트어댑터 클릭리스너 implements

    /* 상단뷰를 위한 변수 */
    private TextView dateTextView;          //현재 이동돼있는 날짜를 보여주는 텍스트뷰
    private Button prevBtn;                 //이전날짜로 가는 버튼
    private Button nextBtn;                 //이후날짜로 가는 버튼

    /* 데이터베이스 */
    private FirebaseDatabase firebaseDatabase;          //firebaseDatabase
    private DatabaseReference movieReference;           //rootReference
    private DatabaseReference scheduleReference;        //rootReference
    private DatabaseReference screenReference;        //rootReference
    final private static String MOVIE_REF = "movie";          //영화 레퍼런스로 가는 키
    final private static String SCH_REF = "schedule";    //스케줄 레퍼런스로 가는 키
    final private static String SCREEN_REF = "screen";    //스케줄 레퍼런스로 가는 키
    final private static String BOOKING_REF = "bookingInfo";    //스케줄 레퍼런스로 가는 키

    /* 영화 스케줄 리스트 출력을 위한 변수 */
    private ListView dayScheduleList;                   // 영화 스케줄 출력을 위한 리스트 뷰
    private ArrayList<MovieSchedule>[] scheduleData;    // 서버에 저장된 스케줄 데이터를 날짜별로 인덱스를 나눠 받아오는 "arrayList객체" 배열
    private ScheduleListAdapter schAdapter;             // 리스트뷰에 연결show하는 어댑터
    private final int SCH_LIST_ROW_LAYOUT_RESOURCE = R.layout.schedule_list_adapter_row;


    /* 스케줄 등록 위한 변수 */
    // 1.영화 선택 스피너 변수들
    private Spinner movieSpinner;           // 등록된 영화 목록 Spinner
    private ArrayList<Movie> movieData;     // 데이터베이스에 저장된 영화 데이터 받아오는 배열
    private ArrayAdapter<Movie> adapter;    // 스피너에 연결하는 어댑터
    // 2.상영관 선택
    private Button screenBtn;               //상영관 선택을 위한 버튼
    // 3.상영날짜 선택
    private Button dateBtn;                 //상영날짜 선택을 위한 버튼
    // 4.상영시간 선택
    private Button timeBtn;                 //상영시간 선택을 위한 버튼

    private Date currentDate;               // 오늘 날짜
    private String strDate;                 // 오늘 날짜 문자열
    // 5. 입력한 스케쥴값을 저장하는 변수 - 따로 변수를 둔 것은 입력이 아우것도 안돼있을 때를 예외처리하기 위해서
    private Movie selectedMovie;            // 선택한 영화
    private int screenNum;                  // 선택한 상영관
    private int startHour;                  // 시작 시간
    private int startMin;                   // 시작 분
    private int showYear;                   // 상영 년도
    private int showMonth;                  // 상영 월
    private int showDay;                    // 상영 일
    public int dateCount;                   // 설정한 날짜라 현재날짜로부터 몇 일 뒤인지 저장하는 변수
    private boolean isAddable;              // 데이터베이스에 겹치지 않는 키(Key2 : key2란 스케쥴 노드 아래 key1노드 아래 key2를 말하며 실질적 값은 "상영관번호+상영시간객체"이다)가 들어가도록 미리 방지하는 변수
    private int runningTime;
    private boolean flag;
    private Date screeningDate;

    // 6. 삭제를 위한 변수
    private boolean bookingExist;           // 삭제하려는 스케쥴이 예매내역에 있는지 확인


    public ArrayList<Screen> screenData;

    /* 상수 */
    final static int SCREENS = 5;           // 상영관 5개
    final int DATE_DIALOG = 1111;           // 날짜 다이어로그
    final int TIME_DIALOG = 2222;           // 시간 다이어로그
    final int FUTURE_DATE = 4;              // 미래 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_manage);
        initViewAndDB();                    // 액티비티 타이틀, 디비연결 초기화
        initScheduleListView();             // 날짜별 스케줄 리스트뷰 초기화
        initInputView();                    // 스케쥴 입력 초기화
    }

    //액티비티 타이틀, 디비연결 초기화
    public void initViewAndDB() {
        /* 1.액티비티 타이틀, 디비 연결 */

        // 상단 텍스트뷰, 버튼
        dateTextView = findViewById(R.id.dateTextView);
        prevBtn = findViewById(R.id.prevBtn);
        prevBtn.setEnabled(false);              // 초기화면은 무조건 오늘날짜이므로 이전 날짜 버튼 비활성화하기
        nextBtn = findViewById(R.id.nextBtn);

        // 데이터베이스
        firebaseDatabase = FirebaseDatabase.getInstance();
        scheduleReference = firebaseDatabase.getReference(SCH_REF);
        movieReference = firebaseDatabase.getReference(MOVIE_REF);
        screenReference = firebaseDatabase.getReference(SCREEN_REF);
    }

    // 날짜별 스케줄 리스트뷰 초기화
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
            scheduleReference.child(strDate).addChildEventListener(new ChildEventListener() {
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
        // 어댑터에는 현재액티비티의 context, 뿌려줄 리스트뷰 한 줄(row)의 레아이웃, 뿌려줄 데이터 정보, 삭제버튼 클릭리스너, 현재설정된 날짜, 현재선택한날짜-오늘날짜
        // 어댑터에 넘기는 파라미터중 뒤에서 세 개는 삭제버튼 때문에 넘기는 인자
        // init 함수 안이므로 오늘 날짜에 맞게 어댑터 달기
        // 따라서 context,R.layout.schedule_list_adapter_row,오늘날짜의 scheduleData, implements한 리스너, 오늘날짜의 string, 0 을 넘긴다.
        schAdapter = new ScheduleListAdapter(this,SCH_LIST_ROW_LAYOUT_RESOURCE, scheduleData[0],this, sdf.format(currentDate),0);
        dayScheduleList.setAdapter(schAdapter);
    }

    // 스케쥴 입력 초기화
    public void initInputView(){
        /* 3. 입력 부분 */
        // 스피너
        movieSpinner = (Spinner) findViewById(R.id.movieSpinner);               //스피너 초기화
        movieData = new ArrayList<Movie>();                                     //데이터베이스로부터 받아올 영화정보를 저장할 arrayList 초기화
        adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, movieData); //기본 ArrayAdapter
        movieSpinner.setAdapter(adapter);

        // 데이터 베이스에서 정보 받아와서 movieData에 저장
        movieReference.addListenerForSingleValueEvent(new ValueEventListener() { // 최초 한번 실행되고 삭제되는 콜백
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 모든 데이터 가지고 오기
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    Movie movie = data.getValue(Movie.class);

                    if(movie == null)
                        Toast.makeText(ScheduleManageActivity.this, "null", Toast.LENGTH_SHORT).show();
                    // 영화객체가 정상적으로 들어왔을 때
                    else
                        movieData.add(movie);               // movieData에 삽입
                    adapter.notifyDataSetChanged();         // 스피너 갱신
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 스피너에 셀렉티드 리스너 달기
        selectedMovie = null;
        movieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMovie = movieData.get(position); // selectedMovie에 저장
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 변수 초기화
        screenNum = -1;     // 상영관 번호 선택 전에는 -1, 선택 후 1~5
        startHour = -1;     // 시작 시간
        startMin = -1;      // 시작 분
        showYear = -1;      // 상영 년도
        showMonth = -1;     // 상영 월
        showDay = -1;       // 상영 일

        screenBtn = (Button) findViewById(R.id.screenBtn);  //스크린 선택 다이얼로그를 띄우는 버튼
        dateBtn = (Button) findViewById(R.id.dateBtn);      //날짜 선택 다이얼로그를 띄우는 버튼
        timeBtn = (Button) findViewById(R.id.timeBtn);      //시간 선택 다이얼로그를 띄우는 버튼

        screenData = new ArrayList<>();
        screenReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    Screen screen = data.getValue(Screen.class);
                    screenData.add(screen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // 상영관 선택 다이어로그 (NumberPicker 다이어로그)
    public void selectScreenClick(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("상영관 선택");
        dialog.setContentView(R.layout.numberpicker_dialog);

        Button setBtn = dialog.findViewById(R.id.setBtn);

        final NumberPicker screenPicker = dialog.findViewById(R.id.screenPicker);
        screenPicker.setMaxValue(SCREENS); // max value SCREENS (5)
        screenPicker.setMinValue(1);   // min value 1
        screenPicker.setWrapSelectorWheel(false);
        setBtn.setOnClickListener(new View.OnClickListener() {
            // 확인 버튼 클릭
            @Override
            public void onClick(View v) {
                // 상영관 번호 저장
                screenNum = screenPicker.getValue();
                screenBtn.setText(String.valueOf(screenNum)+"관");   // 선택한 것 버튼 텍스트에 반영
                dialog.dismiss(); // 다이어로그 파괴
            }
        });
        dialog.show();
    }

    // 날짜 선택 버튼 (DatePicker 다이어로그)
    public void inputDateClick(View view) {
        //onCreateDialog참조
        showDialog(DATE_DIALOG);
    }

    // 시작 시간 입력 (TimePicker 다이어로그)
    public void inputStartTimeClick(View view) {
        //onCreateDialog참조
        showDialog(TIME_DIALOG);
    }


    // 스케쥴 추가 완료 버튼 클릭
    public void scheduleCompleteClick(View view) {
        // Input 검사
        int[] args = new int[6];
        args[0] = showYear;
        args[1] = showMonth;
        args[2] = showDay;
        args[3] = startHour;
        args[4] = startMin;
        args[5] = screenNum;

        if(inputCheck(args)) {
            // 검사 통과
            // 데이터 베이스에 업로드
            screeningDate = new Date(showYear, showMonth, showDay);    // Date로 변환
            screeningDate.setHours(startHour);                              // 시간
            screeningDate.setMinutes(startMin);                             // 분도 넣어줍니다.

            SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일");
            strDate = sdf.format(screeningDate);
            strDate = String.valueOf(showYear) + "년 " + strDate;
            //Toast.makeText(this, strDate, Toast.LENGTH_SHORT).show();
            // 객체 생성후
            final MovieSchedule movieSchedule = new MovieSchedule(selectedMovie.getTitle(), String.valueOf(screenNum),
                    screeningDate/*, startHour, startMin*/, selectedMovie.getRunningTime());
            HashMap<String, Boolean> booked = new HashMap<>(); // 예약 현황 초기화해서 스케줄 객체에 넣어줄 것

            Screen screen = screenData.get(screenNum - 1); // 선택한 스크린이요.
            Set set = screen.getAbledMap().entrySet();
            Iterator iterator = set.iterator();

            while(iterator.hasNext()) {
                // 모든 키에 대해서
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                booked.put(key, MyButton.UNBOOKED);     // 예약이 되어있지 않아요.
            }

            movieSchedule.setBookedMap(booked);

            /*
            1. 스케쥴데이터베이스 구조
            schedule/(key1)yyyy년 MM월 dd일/(key2)상영관번호+상영시간객체/스케쥴객체
            key는 특히 유일해야함!!!!! 특히 key2는 유일하게 입력받도록 확인하는 함수 필요
            상영관번호가 같으면서 + 상영시간객체까지 동일할 수 없음 따라서 스케쥴 추가할때 확인 필요!!!
            */

            // 데이터베이스 순서 : schedule/yyyy년 MM월 ddd일/상영관번호+상영시간객체/상영스케쥴객체
            // scheduleKey = 상영관번호+상영시간객체
            final String scheduleKey = movieSchedule.getScreenNum()+movieSchedule.getScreeningDate();   // 이번에 추가하려는 영화스케쥴 인스턴스로부터 키 정보 가져옴.
            final String addScreenNum = movieSchedule.getScreenNum();                                   // 이번에 추가하려는 영화스케쥴 인스턴스로부터 상영관 번호정보 가져옴.

            // 런닝타임 정보를 더해 끝나는 시간과 분 계산, 24시 후의 경우에 관해서는 계산되지 않았음.
            // 25시, 26시 ...일때는 날짜가 넘어가지만 그대로 25시 26시로 간주.
            final int endMin = movieSchedule.getEndMin();
            final int endHour = movieSchedule.getEndHour();

            //Toast.makeText(getApplicationContext(),startHour+" "+startMin,Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(),endHour+" "+endMin,Toast.LENGTH_SHORT).show();

            // DB의 스케쥴 노드 아래, 추가하려는 날짜 노드 아래에서 검사함
            scheduleReference.child(strDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // 최종적으로 해당 스케쥴을 추가할 수 있는지를 판단하는 변수
                    isAddable = true;

                    // 같은 날짜에 같은 상영관에서 상영하는 영화스케쥴을 모은 객체ArrayList
                    ArrayList<MovieSchedule> sameScreenMovies = new ArrayList<>();

                    // for문으로 해당 날짜의 모든 스케쥴을 검사함.
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        MovieSchedule ms=data.getValue(MovieSchedule.class);
                        // 영화의 상영관 번호가 추가하려는 스케쥴의 상영관 번호와 같다면 객체ArrayList에 추가
                        if(ms.getScreenNum().equals(addScreenNum))
                            sameScreenMovies.add(ms);
                    }
                    // sameScreenMovies를 차례로 검사하면서 지금 추가하려는 영화의 상영시간과 비교하여 겹치는지 검사함
                    // 만약 겹친다면 바로 for문을 탈출해 추가할 수 없다고 알림.
                    for(int i=0;i<sameScreenMovies.size();i++){
                        isAddable = true;
                        MovieSchedule ms = sameScreenMovies.get(i);
                        Date cmpDate = ms.getScreeningDate();           // sameScreenMovies의 영화 시작시간, 끝시간을 계산함.
                        int cmpStartHour = cmpDate.getHours();          // 시작 시각
                        int cmpStartMin = cmpDate.getMinutes();         // 시작 분
                        int cmpEndMin = ms.getEndMin();                 // 종료 시각
                        int cmpEndHour = ms.getEndHour();               // 종료 분

                        //Toast.makeText(getApplicationContext(), sameScreenMovies.get(i).getMovieTitle()+" "
                         //       +cmpStartHour+"시 "+cmpStartMin+"분 ~ "+cmpEndHour+"시 "+cmpEndMin+"분 까지",Toast.LENGTH_SHORT).show();
                        // 상영시간이 겹치는지 겹치지 않는지 검사함.
                        if(startHour<cmpEndHour || ((startHour==cmpEndHour)&&(startMin<=cmpEndMin))){
                            if(endHour>cmpStartHour || ((endHour==cmpStartHour)&&(endMin>=cmpStartMin))) {
                                isAddable = false;
                                break;
                            }
                        }
                    }

                    if(isAddable){
                        // 데이터베이스에 해당 순서대로 스케쥴객체 삽입.
                        scheduleReference.child(strDate).child(scheduleKey).setValue(movieSchedule);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"해당 스케쥴은 상영관의 중복입니다.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
//            // DB의 movie노드 아래에서 추가하려는 영화의 런닝타임 정보를 가져옴
//            movieReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for(DataSnapshot data : dataSnapshot.getChildren()){
//                        Movie tmp = data.getValue(Movie.class);
//                        // DB의 모든 영화를 검사하면서 타이틀이 같다면 런닝타임 정보를 가져옴.
//                        if(tmp.getTitle().equals(movieSchedule.getMovieTitle())) {
//                            runningTime = tmp.getRunningTime();
//                            //Toast.makeText(getApplicationContext(),runningTime+"",Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                        // 만약 DB에 해당 영화의 정보다 없다면 runningTime은 -1에서 변동되지 않을 것임.
//                    }
//                    if(runningTime == -1){
//                        // 영화 타이틀을 관리자가 선택할 때에는 디비에서 받아온 정보롤 영화제목 선택 스피너를 구성하므로 사실상 들어올 수 없는 분기.
//                        Toast.makeText(getApplicationContext(), "추가하려는 영화가 DB에 없습니다!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    // 추가하려는 영화의 시작시간과 런닝타임 정보로 차지하는 시간 정보 계산
//                    else{
//                                            }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

        }
        else {
            // 틀린 점이 있음
            try {
                throw new InputException();
            } catch (InputException e) {
                Toast.makeText(this, "입력을 확인하세요.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // args가 모두 -1이 아니여야만 true 반환
    public boolean inputCheck(int[] args) {
        for(int i=0; i<args.length; i++) {
            if(args[i] == -1)
                return false;
        }
        return true;
    }

    // 입력 값에 따라 다른 다이어로그 생성
    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            // 날짜 다이어로그
            case DATE_DIALOG:
                Calendar calendar = new GregorianCalendar(Locale.KOREA);
                DatePickerDialog dateDialog = new DatePickerDialog
                        (this, // 현재화면의 제어권자
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                                        //Toast.makeText(getApplicationContext(),year+"년 "+(monthOfYear+1)+"월 "+dayOfMonth+"일 을 선택했습니다",Toast.LENGTH_SHORT).show();
                                        // 설정 후 변수에 저장
                                        showYear = year;
                                        showMonth = monthOfYear;
                                        showDay = dayOfMonth;
                                        dateBtn.setText(year+"년 "+(monthOfYear+1)+"월 "+dayOfMonth+"일"); // 버튼에 선택 날짜 반영
                                    }
                                }
                                ,// 사용자가 날짜설정 후 다이얼로그 빠져나올때 호출할 리스너 등록
                                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)); // 기본값 연월일
                dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                return dateDialog;
            // 시간 다이어로그
            case TIME_DIALOG:
                TimePickerDialog timeDialog =
                        new TimePickerDialog(this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        //Toast.makeText(getApplicationContext(), hourOfDay +"시 " + minute+"분 을 선택했습니다",Toast.LENGTH_SHORT).show();
                                        // 설정 후 변수에 저장
                                        startHour = hourOfDay;
                                        startMin = minute;
                                        timeBtn.setText(hourOfDay +"시 " + minute+"분");
                                    }
                                }, // 값설정시 호출될 리스너 등록
                                0,0, false); // 기본값 시분 등록
                // true : 24 시간(0~23) 표시
                // false : 오전/오후 항목이 생김
                return timeDialog;
        }
        return super.onCreateDialog(id);
    }

    // 이전 날짜 버튼 클릭 리스너
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
            schAdapter = new ScheduleListAdapter(this, SCH_LIST_ROW_LAYOUT_RESOURCE, scheduleData[dateCount],this,str,dateCount);
            dayScheduleList.setAdapter(schAdapter); // 어댑터 새로 설정
            schAdapter.notifyDataSetChanged(); // 변경 통지
        }
    }

    // 이후 날짜 버튼 클릭 리스너
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
            schAdapter = new ScheduleListAdapter(this,SCH_LIST_ROW_LAYOUT_RESOURCE, scheduleData[dateCount],this, str,dateCount);
            dayScheduleList.setAdapter(schAdapter); // 어댑터 새로 설정
            schAdapter.notifyDataSetChanged(); // 변경 통지
        }
    }

    // n일 후 날짜 스트링 반환
    public String dateCalculator(int n) {
        String str;
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, n);
        Date future = today.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        str = sdf.format(future);
        return str;
    }

    // 스케줄 삭제버튼에 대한 어댑터의 클릭이벤트 - 삭제하기
    @Override
    public void onScheduleDeleteBtnClick(final int position, final String strDateKey, final int dateCount) {

        /*
        1. 스케쥴데이터베이스 구조
          schedule/(key1)yyyy년 MM월 dd일/(key2)상영관번호+상영시간객체/스케쥴객체
        2. 파라미터 설명
         1. position : 선택한 버튼이 속한 열(row)의 position
         2. strDateKey : 그 열(row)의 데이터인 스케쥴이 등록된 날짜. yyyy년 MM월 dd일 형태. 스케쥴DB에 접근하기 위한 첫번째 키(key1)
         3. dateCount : 그 열(row)의 데이터인 스케쥴이 등록된 날짜가 오늘로부터 몇일 후인 지 저장하고 있는 변수.
        */

        // key2
        final String scheduleKey = scheduleData[dateCount].get(position).getScreenNum() + scheduleData[dateCount].get(position).getScreeningDate();

        // 예매정보 존재검사 변수 초기화 -false : 없음을 나타냄.
        bookingExist = false;
        // 예매정보 데이터베이스 참조
        DatabaseReference bookingReference = firebaseDatabase.getReference(BOOKING_REF);
        // 예매정보 데이터베이스에 리스너 달기
        bookingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터베이스에 등록된 예매정보 객체를 탐색하면서
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    BookingInfo bi = data.getValue(BookingInfo.class);

                    // 얘매정보 중에 삭제하려고 선택한 영화와 같은 제목의 영화가 있다면
                    if(bi.getScheduleKey().equals(scheduleKey)){
                        // 예매정보가 존재한다. true
                        bookingExist = true;
                        Toast.makeText(getApplicationContext(),"해당 스케쥴에는 고객의 예매내역이 존재합니다.",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    // 같은 key의 스케쥴이 없다.
                    else{
                        bookingExist = false;
                    }
                }

                // 비동기 문제때문에 리스너 안에서 모든 걸 다 합니다.
                // 영화가 존재한다면 지울 수 있는 영화이다
                if(!bookingExist){
                    Toast.makeText(getApplicationContext(),"스케쥴 삭제 완료",Toast.LENGTH_SHORT).show();
                    // 스케쥴데이터베이스 구조대로 접근해 해당 객체를 null로 set
                    scheduleReference.child(strDateKey).child(scheduleKey).setValue(null);
                    // 스케줄 arrayList 객체 배열의 해당 포지션을 지운다. (객체배열의 인덱스 번호와 리스트뷰의 열 번호가 일치함)
                    scheduleData[dateCount].remove(position);
                    // 리스트뷰에 갱신
                    schAdapter.notifyDataSetChanged();
                }
                // 삭제할 수 없다면 알림
                else{
                    Toast.makeText(getApplicationContext(),"지울 수 없습니다.",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
