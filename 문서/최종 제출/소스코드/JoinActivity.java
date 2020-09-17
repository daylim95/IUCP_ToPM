package com.example.knk.topm;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.knk.topm.Object.InputException;
import com.example.knk.topm.Object.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JoinActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;      //firebaseDatabase
    private DatabaseReference rootReference;        //rootReference
    private static String user_ref = "user";        //레퍼런스할 이름 - 여기서는 회원가입이므로 user를 root로 참조해 그 아래에 데이터 추가.

    private String input_id;                        //사용자 ID 저장 변수
    private String input_pw;                        //사용자 PW 저장 변수
    private String input_name;                      //사용자 Name 저장 변수
    private User newbie;                            //사용자 객체 인스턴스
    private Button joinBtn;                         //회원가입 완료 버튼 : 아이디체크가 완료돼야만 활성화 됨. 완료안되면 비활성화 상태.
    private Button idCheckBtn;                      //아이디 중복체크 버튼이자 아이디 재입력 버튼 : 아이디체크가 통과되면 재입력용 버튼으로 바뀜
    private boolean idCheckBtnState;                //아이디 중복체크를 했는지 기억하는 변수. false - 체크안됨, true - 체크통과
    private EditText inputIdEditText;               //아이디를 입력하는 editText : 아이디체크가 완료돼면 비활성화됨(입력 불가), 재입력 버튼을 누르면 다시 활성화됨(입력 가능).
    private RadioGroup join_type;                   //일반사용자 혹은 관리자 둘중 하나의 선택을 받는 라디오버튼 그룹
    private boolean isAdmin;                        //일반사용자 혹은 관리자 둘중 하나의 선택을 저장하는 변수 : true - admin, false - normal user
    private EditText birthEditText;                 //생일을 입력받는 에딧텍스트 : DatePickerDialog로부터 입력 받은 값 보여주는 곳
    private Date birthOutput;                       //User객체에 넣을 생일정보 포맷(yyMMdd)에 맞게 저장하느 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        init();                                     //초기화
    }

    //초기화 함수
    public void init(){
        //데이터베이스 연결 작업
        firebaseDatabase = FirebaseDatabase.getInstance();          //파이어베이스 데이터베이스 인스턴스 호출
        rootReference = firebaseDatabase.getReference(user_ref);    //"user"라는 이름으로 루트참조 생성
        //버튼, 에딧텍스트 초기화
        joinBtn = findViewById(R.id.joinCompleteBtn);                       //가입 버튼 초기화
        idCheckBtn = findViewById(R.id.idCheckBtn);                 //아이디 중복체크 버튼 초기화
        inputIdEditText = findViewById(R.id.id_join);              //아이디 입력하는 에딧텍스트 초기화 - 다른 에딧텍스트와 다르게 중복체크작업 때문에 따로 생성함.

        //아이디를 체크했는지 기억하는 변수 : false - 체크 안됨.
        idCheckBtnState = false;
        //체크상태에 따라 각종 버튼, 에딧텍스트 상태 바꾸는 함수
        setIdInput(idCheckBtnState);

        //라디어그룹 초기화 및 체크드체인지리스너 달아주기
        join_type = findViewById(R.id.type_join);
        join_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //체크된 라디오가 일반사용자라면 staff는 false
                if(checkedId == R.id.normalRB)
                    isAdmin = false;
                //체크된 라디오가 관리자라면 admin은 true
                else if(checkedId == R.id.adminRB)
                    isAdmin = true;
                //그 외는 오류
                else
                    Toast.makeText(JoinActivity.this,"가입 유형이 제대로 저장되지 않음",Toast.LENGTH_SHORT);
            }
        });
        //생일 보여주는 에딧텍스트 초기화
        birthEditText = findViewById(R.id.birth_join);
        //초기화
        birthOutput = null;
    }

    //아이디가 중복체크됐는지 여부에 따라 각종 버튼, 에딧텍스트 상태 바꾸는 함수
    public void setIdInput(boolean isIdChecked){
        //아이디가 체크되지 않았다면
        if(!isIdChecked){
            joinBtn.setEnabled(false);                              //가입 완료 버튼은 비활성화 상태 - 누를 수 없다.
            inputIdEditText.setEnabled(true);                       //아이디를 입력하는 에딧텍스트는 활성화 상태 - 입력할 수 있다.
            idCheckBtn.setText("중복 확인");                         //중복체크 버튼인 상태
        }
        //아이디가 체크 됐다면
        else{
            joinBtn.setEnabled(true);                               //가입 완료 버튼은 활성화 상태 - 누를 수 있다.
            inputIdEditText.setEnabled(false);                      //아이디를 입력하는 에딧텍스트는 비활성화 상태 - 입력할 수 없다. 체크한 아이디를 다시 수정하는 일이 없도록
            idCheckBtn.setText("재입력하기");                        //재입력하는 버튼인 상태 - 이 버튼을 누를 시 다시 아이디가 체크되지 않은 상태로 전환된다.
        }

    }

    //ID 중복검사 버튼 클릭이벤트 함수
    public void idCheckClick(View view) {

        //아이디가 체크되지 않은 상태 - 중복체크 작업
        if(idCheckBtnState==false){
            input_id = inputIdEditText.getText().toString();        //아이디를 입력하는 에딧텍스트의 입력값을 가져와서 input_id에 저장.
            //Log.v("Input_ID",input_id);

            //아이디에 공백은 입력 불가, 혹은 스페이스가 처음으로 들어가게 입력불가
            if(input_id.equals("")||input_id.charAt(0)==' '){
                Toast.makeText(this,"공백으로 시작하는 아이디는 입력 불가",Toast.LENGTH_SHORT).show();
            }
            //아이디에 한 글자 이상 입력된 경우
            else{
                //"user"로 루트 레퍼런스를 맞추어 놓은 상황 -> singleValueEvent 처리하는 함수 달기
                rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    boolean isValidID = true;           //유효한 아이디 인지 기억하는 변수 : true - 유효
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){                            //현재 파이어베이스에 저장된 데이터의 스냅샷을 차례로 가져와 data에 저장한다.
                            if(data.getKey().equals(input_id)) {                                        //data의 키 = user_id로 설정돼 있음. 즉, data의 키가 input_id와 같은지 검사한다.
                                Log.d("DataSnapshot", "onValueAdded:" + data);               //일치하는 정보를 제대로 가져오는지 로그 찍어보기
                                isValidID = false;                                                      //같은 정보가 있다면 아이디 중복이므로 isVaildID = false
                                break;                                                                  //for문 탈출
                            }
                        }

                        //Alert Dialog 생성
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(JoinActivity.this);  //context는 직접 명시해야 함.
                        //유효한 아이디일 때 - 승인을 알리는 Dialog 생성
                        if(isValidID){
                            idCheckBtnState=true;                                                       //아이디 체크 됐으므로 true
                            setIdInput(idCheckBtnState);                                                //각종 버튼, 에딧텍스트 상태 수정
                            alertBuilder.setTitle("아이디 승인!");
                            alertBuilder.setMessage(input_id + "은(는) 사용가능한 아이디입니다.");
                            alertBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                        }
                        else{
                            alertBuilder.setTitle("아이디 중복!");
                            alertBuilder.setMessage(input_id + "은(는) 이미 존재하는 아이디입니다. 다시 시도해주세요.");
                            alertBuilder.setPositiveButton("close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        //아이디가 체크되지 않은 상태 - 재입력 작업
        else{
            idCheckBtnState=false;              //아이디 체크되지 않았으므로 false
            setIdInput(idCheckBtnState);        //각종 버튼, 에딧텍스트 상태 수정
        }
    }


    //회원 가입 완료 버튼 클릭이벤트 함수
    public void joinCompleteClick(View view) {
        //ID 중복검사 버튼을 반드시 누르고 그 검사에 통과한 후에 활성화돼야 함.
        if(idCheckBtnState){
            input_id = ((EditText)findViewById(R.id.id_join)).getText().toString();
            input_pw = ((EditText)findViewById(R.id.pw_join)).getText().toString();
            input_name = ((EditText)findViewById(R.id.name_join)).getText().toString();

            // 입력이 누락된 경우
            if(input_pw.length() <= 0 || input_name.length() <= 0 || birthOutput == null) {
                try {
                    throw new InputException();
                } catch (InputException e) {
                    Toast.makeText(this, "모두 입력하세요.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else{
                //뉴비 생성
                newbie = new User(input_id,input_pw,input_name,birthOutput,isAdmin);
                rootReference.child(newbie.id).setValue(newbie);
                Toast.makeText(this,"가입을 축하드립니다.",Toast.LENGTH_SHORT).show();
                this.finish();
            }

        }
        //중복검사 안하고는 어짜피 가입버튼 활성화되지 않아서 누를수 없지만 일단 분기
        else{
            Toast.makeText(this,"ID 중복확인을 먼저 해주세요.",Toast.LENGTH_SHORT).show();
        }

    }

    //생일 선택시 DatePickerDialog 생성하는 버튼
    public void birthClick(View view) {
        //현재 시간 받아오는 Calendear 객체
        final Calendar calendar = Calendar.getInstance();
        //데이트픽커 다이얼로그
        DatePickerDialog datePickerDialog = new DatePickerDialog(JoinActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //입력 받은 데이터를 Date 객체에 저장 - User의 멤버변수 birth에 저장될 값
                birthOutput = new Date(year,month,dayOfMonth);
                // Toast.makeText(JoinActivity.this,birthOutput.toString(),Toast.LENGTH_SHORT).show();

                //month는 출력 상으로 -1돼서 나오므로 +1 처리
                month = month+1;
                //생일 에딧텍스트에 입력 받은 값 표시해줌.
                birthEditText.setText(year+"년 "+ month+"월 "+dayOfMonth+"일");
            }
            //Dialog가 띄워졌을 때, 현재 시간으로 기본 설정
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DATE));
        //현재 날짜 이후에는 선택할 수 없게 맥스를 오늘로 설정
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        //쇼.
        datePickerDialog.show();
    }
}