package com.example.knk.topm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.knk.topm.AdminActivities.AdminMainActivity;
import com.example.knk.topm.Object.User;
import com.example.knk.topm.UserActivities.UserMainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private BackPressedHandler backPressedHandler;      // 뒤로가기 핸들러
    private EditText login_id;                          // 로그인 아이디를 입력하는 에딧텍스트
    private EditText login_pw;                          // 로그인 비밀번호를 입력하는 에딧텍스트
    private FirebaseDatabase firebaseDatabase;          // 파이어베이스
    private DatabaseReference rootReference;            // 파이어베이스 데이터베이스 참조
    private final String USER_REF = "user";             // 여기서 데이터베이스는 user 참조
    private String USER_PUTEXTRA_TAG = "user";          // 인텐트로 다음 액티비티로 넘길 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        backPressedHandler = new BackPressedHandler(this, 0);   // 뒤로가기 핸들러 초기화

        init();                                         // 초기화
    }

    // 뒤로가기 눌렀을 때 처리하는 함수
    @Override
    public void onBackPressed() {
        backPressedHandler.onBackPressed();
    }

    // 초기화
    public void init(){
        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        firebaseDatabase = FirebaseDatabase.getInstance();          // 파이어베이스 데이터베이스 인스턴스 호출
        rootReference = firebaseDatabase.getReference(USER_REF);    // "user"라는 이름으로 루트참조 생성
    }
    //회원 가입 액티비티로
    public void joinClick(View view) {
        login_id.setText("");
        login_pw.setText("");
        startActivity(new Intent(this, JoinActivity.class));
    }

    //로그인 판단 후 다음 메인 액티비티로
    public void loginClick(View view) {
        //한번만 데이터베이스를 참조하므로 Single Value Event
        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean idExist = false;
            User user;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    //아이디가 같은지 판단
                    if(data.getKey().equals(login_id.getText().toString())) {
                        //Log.d("DataSnapshot", "onValueAdded:" + data);
                        //아이디가 같다면 유저 인스턴스를 하나 생성해서 데이터를 받아온 다음
                        user = data.getValue(User.class);
                        //아이디가 user 데이터베이스에 존재함을 표시한다.
                        idExist = true;
                        break;
                    }
                }

                //아이디가 존재한다면
                if(idExist){
                    //유저 인스턴스의 비밀번호가 입력한 비밀번호와 일치한다면 -> 로그인 성공
                    if(user.getPw().equals(login_pw.getText().toString())) {
                        Intent intent;
                        //유저 인스턴스가 관리자라면
                        if (user.isAdmin())  //관리자 액티비티로
                            intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                            //유저 인스턴스가 일반 사용자라면
                        else                //일반 사용자 액티비티로
                            intent = new Intent(getApplicationContext(), UserMainActivity.class);
                        //유저 인스턴스의 객체정보를 다음 액티비티로 전달한다.
                        intent.putExtra(USER_PUTEXTRA_TAG, user);
                        //로그인 정보 입력창을 다시 빈칸으로 초기화한 다음 (뒤로가기 눌렀을 때 정보가 남아있는 것을 방지)
                        login_pw.setText("");
                        login_id.setText("");
                        //해당 액티비티로 이동
                        startActivity(intent);
                    }
                    //유저 인스턴스의 비밀번호가 입력한 비밀번호와 일치하지 않는다면 -> 로그인 실패
                    else{
                        Toast.makeText(getApplicationContext(),"비밀번호를 확인하세요.",Toast.LENGTH_SHORT).show();
                        login_pw.setText("");
                    }

                }
                //아이디가 존재하지 않는다면 -> 로그인 실패
                else{
                    Toast.makeText(getApplicationContext(),"해당 아이디는 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    login_id.setText("");
                    login_pw.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
