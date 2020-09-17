package com.example.knk.topm.AdminActivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.knk.topm.CustomAdapters.MovieListAdapter;
import com.example.knk.topm.Object.BookingInfo;
import com.example.knk.topm.Object.InputException;
import com.example.knk.topm.Object.Movie;
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

public class MovieManageActivity extends AppCompatActivity implements MovieListAdapter.MovieDeleteBtnClickListener {

    private EditText editTitle;                     // 영화이름 입력하는 에딧텍스트
    private EditText editDir;                       // 감독이름 입력하는 에딧텍스트
    private EditText editRun;                       // 런닝타임 입력하는 에딧텍스트
    private String title;                           // 영화이름 저장하는 변수
    private String director;                        // 감독이름 저장하는 변수
    private String runningTime;                     // 런닝타임 저장하는 변수

    public ArrayList<Movie> movieData;              // 현재 등록된 영화인스턴스를 저장하는 객체배열
    private ListView movieManageList;               // 현재 등록된 영화리스트를 보여주는 리스트뷰
    private MovieListAdapter adapter;               // 리스트뷰의 커스텀 어댑터

    // 데이터베이스
    private FirebaseDatabase firebaseDatabase;      // firebaseDatabase
    private DatabaseReference rootReference;        // rootReference
    private static String MOVIE_REF = "movie";      // 레퍼런스할 이름 - 여기서는 영화 등록이므로 movie를 root로 참조해 그 아래에 데이터 추가.
    private static String BOOKING_REF = "bookingInfo"; // 레퍼런스할 이름 - 여기서는 영화 삭제검사이므로  bookingInfo를 root로 참조해 그 아래에 데이터 검색.
    private static String SCH_REF = "schedule"; // 레퍼런스할 이름 - 여기서는 영화 삭제검사이므로  bookingInfo를 root로 참조해 그 아래에 데이터 검색.
    public boolean bookingExist = false;            // 해당 영화에 예약이 존재하는지 검사하는 변수
    public String movieTitle;                       // bookingInfo 레퍼런스에서 가져올 예매내역의 영화이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_manage);
        init();                                     // 초기화
    }

    public void getMovieFromFB(){

        // 데이터베이스
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootReference = firebaseDatabase.getReference(MOVIE_REF);

        // 데이터 베이스에서 정보 받아와서 movieData에 저장
        rootReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // 모든 데이터 가지고 오기
                Movie movie = dataSnapshot.getValue(Movie.class);
                if(movie == null)
                    Toast.makeText(MovieManageActivity.this, "nullllll", Toast.LENGTH_SHORT).show();
                else {
                    movieData.add(movie); // movieData에 삽입
                    adapter.notifyDataSetChanged();
                }
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


    // 각종 변수 초기화 함수
    public void init() {

        // 1. 리스트뷰 부분
        // ListView 초기화
        movieManageList = findViewById(R.id.movieList);
        // 영화 객체 배열 초기화
        movieData = new ArrayList<>();

        // 파이어베이스로부터 데이터 가져오기
        getMovieFromFB();

        // 리스트 어댑터 초기화, context, 리스트 한 줄의 레이아웃, 뿌려줄 데이터, 삭제버튼 클릭리스너 전달
        adapter =  new MovieListAdapter(this, R.layout.movie_list_adapter_row, movieData,this);
        // 리스트뷰에 어댑터 set
        movieManageList.setAdapter(adapter);
        // 데이터 갱신 통지
        adapter.notifyDataSetChanged();

        // 2. 입력 창 부분
        // 입력 위젯 초기화
        editTitle = findViewById(R.id.movie_name);
        editDir = findViewById(R.id.movie_director);
        editRun = findViewById(R.id.movie_runningtime);
    }

    // 영화 추가 버튼 누를시 클릭이벤트 함수
    public void completebtn(View view) {

        // 입력 값 받아옴
        title = editTitle.getText().toString();
        director = editDir.getText().toString();
        runningTime = editRun.getText().toString();

        // 모든 입력 창에 하나라도 입력이 누락된 경우
        if(title.length() <= 0 || director.length() <= 0 || runningTime.length() <= 0) {
            try {
                throw new InputException();
            } catch (InputException e) {
                Toast.makeText(this, "모두 입력하세요.", Toast.LENGTH_SHORT).show();       //메시지 출력
                e.printStackTrace();
            }
        }
        // 모든 입력 창에 모두 입력이 있는 경우 - 데이터 베이스에 입력
        else {
            // 런닝타임 int형으로 변경
            int runTime = Integer.parseInt(runningTime);
            if(runTime>0){
                Movie movie = new Movie(runTime, title, director, 0); // 객체 생성 후
                // movie/'영화이름'을 키로 해서 데이터베이스에 추가
                rootReference.child(movie.getTitle()).setValue(movie);
                // adapter.notifyDataSetChanged();   //메인화면으로 이동하므로 따로 리스트뷰 갱신하지 않음

                // 다시 관리자 메인으로 이동
                this.finish();
            }
            else{
                Toast.makeText(this, "상영시간은 자연수로 입력할 수 있습니다.", Toast.LENGTH_SHORT).show();       //메시지 출력
            }
        }
    }

    // 어댑터에 선언한 interface 오버라이드 함수
    @Override
    public void onMovieDeleteBtnClick(final int position) {
        // 해당 포지션의 movie의 title
        movieTitle = movieData.get(position).getTitle();

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
                    if(bi.getTitle().equals(movieTitle)){
                        // 예매정보가 존재한다. true
                        bookingExist = true;
                        Toast.makeText(getApplicationContext(),"해당 영화는 고객의 예매내역이 존재하는 영화입니다.",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    // 같은 제목의 영화가 없다.
                    else{
                        bookingExist = false;
                    }
                }

                // 비동기 문제때문에 리스너 안에서 모든 걸 다 합니다.
                // 영화가 존재한다면 지울 수 있는 영화이다
                if(!bookingExist){
                    Toast.makeText(getApplicationContext(),"영화 삭제 완료",Toast.LENGTH_SHORT).show();
                    // 영화 데이터베이스 삭제
                    // 데이터베이스에 해당 리스트뷰의 포지션에 있는 영화 이름으로 가서 값을 지운다.
                    rootReference.child(movieTitle).removeValue();
                    // 영화객체 배열의 해당 포지션을 지운다. (객체배열의 인덱스 번호와 리스트뷰의 열 번호가 일치함)
                    movieData.remove(position);
                    // 변경된 것 어댑터에 반영
                    adapter.notifyDataSetChanged();
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


