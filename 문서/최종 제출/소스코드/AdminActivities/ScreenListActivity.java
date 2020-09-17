package com.example.knk.topm.AdminActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.knk.topm.R;

public class ScreenListActivity extends AppCompatActivity implements View.OnClickListener {

    final int SCREEN_COUNT = 5; // 상영관 개수
    Button[] screenEdits;
    LinearLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_list);

        init();
    }

    public void init() {

        // 객체 생성
        screenEdits = new Button[SCREEN_COUNT];
        buttonLayout = findViewById(R.id.buttonLayout);

        // 상영관의 개수가 달라져도 SCREEN_COUNT만 바꾸면 버튼이 늘어나도록 버튼을 직접 자바코드로 작성
        for(int i=0; i<SCREEN_COUNT; i++) {
            screenEdits[i] = new Button(this);
            screenEdits[i].setId(i+1);          // id 추가함
            screenEdits[i].setText(String.valueOf(i+1)+"관");
            screenEdits[i].setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            screenEdits[i].setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonLayout.addView(screenEdits[i]);

            screenEdits[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int screenNum = v.getId(); // 클릭한 버튼 몇번째인지 알아옴
        Intent intent1 = new Intent(this, ScreenShowActivity.class);
        intent1.putExtra("SCREENID1", screenNum); // 관 ID 정보 전송
        startActivity(intent1);
    }
}
