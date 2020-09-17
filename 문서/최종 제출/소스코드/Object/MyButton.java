package com.example.knk.topm.Object;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MyButton extends android.support.v7.widget.AppCompatButton implements View.OnClickListener{

    /* 상수 */
    public final static boolean ABLED = true; // 좌석입니다.
    public final static boolean UNABLED = false; // 좌석이 아닙니다.
    public final static boolean BOOKED = true; // 예약되었습니다.
    public final static boolean UNBOOKED = false; // 예약 안 되었습니다.
    public final static boolean SPECIAL = true; // 우등 좌석입니다.
    public final static boolean UNSPECIAL = false; // 일반 좌석입니다.
    public final static boolean COUPLE = true;  // 커플석입니다.
    public final static boolean UNCOUPLE = false; // 커플석이 아닙니다.

    public MyButton(Context context) {
        // 자바 코드로 생성 시 호출되는 생성자
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        // XML에서 생성 시 호출되는 생성자
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {

    }
}
