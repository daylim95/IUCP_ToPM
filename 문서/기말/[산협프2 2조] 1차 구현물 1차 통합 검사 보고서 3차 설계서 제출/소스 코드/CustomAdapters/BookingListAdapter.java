package com.example.knk.topm.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knk.topm.Object.BookingInfo;
import com.example.knk.topm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BookingListAdapter extends ArrayAdapter<BookingInfo> {
    private Context context;                                                // 어댑터를 선언한 액티비티의 context를 저장함
    private ArrayList<BookingInfo> data;                                  // 어댑터를 선언한 액티비티의 스케쥴객체배열을 저장함
    private int resource;                                                   // 어댑터가 적용될 레이아웃을 저장함(여기서는 리스트 뷰의 열(row)의 레이아웃)
    // 어댑터를 선언한 액티비티로부터 받아온 정보 - 이 어댑터에서 쓰지 않고 삭제버튼 클릭 시 어댑터선언한 액티비티에게 다시 알려주기 위해 보관하는 용도

    private final int RESOURCE_TYPE_FOR_BOOKING_LIST = R.layout.booking_list_adapter_row;


    public BookingListAdapter(@NonNull Context context, int resource, ArrayList<BookingInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public BookingInfo getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //리스트뷰의 한 열(row)를 구성해주는 오버라이드 함수
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = view;          // 파라미터 옮겨놓기
        TextView date;          // 날짜를 보여주는 텍스트뷰
        // TextView time;          // 영화 시간을 보여주는 텍스트뷰
        TextView screen;        // 상영관 번호를 보여주는 텍스트뷰
        TextView title;         // 영화 제목을 보여주는 텍스트뷰
        TextView seats;         // 예매 자리를 보여주는 텍스트뷰

        if(v == null){          // inflate할 뷰 생성
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

        // 현재 포지션에 데이터에 인덱스 순으로 예매 정보 인덱스 생성
        BookingInfo bookingInfo = data.get(position);
        date = v.findViewById(R.id.dateTextView);
        // time = v.findViewById(R.id.timeTextView);
        screen = v.findViewById(R.id.screenTextView);
        title = v.findViewById(R.id.titleTextView);
        seats = v.findViewById(R.id.seatsTextView);

        if(bookingInfo != null){
            if(date != null && /* time != null &&  */ screen != null && title != null && seats != null){
                if(resource == RESOURCE_TYPE_FOR_BOOKING_LIST) {

                    SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 hh시 mm분");
                    date.setText(sdf.format(bookingInfo.getScreeningDate()));


                    screen.setText(Character.toString(bookingInfo.getScheduleKey().charAt(0)) + "관");


                    title.setText(bookingInfo.getTitle());

                    String seatsString = "";  // 예매한 좌석을 모두 기록하는 문자열
                    for(int i=0; i<bookingInfo.getBookedSeats().size(); i++) {
                        seatsString += bookingInfo.getBookedSeats().get(i)+ "  ";
                    }
                    seats.setText(seatsString);

                }
            }
            else{
                Toast.makeText(getContext(),"텍스트 뷰가 제대로 초기화되지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getContext(),"예매 정보를 받아오는데 실패했습니다.",Toast.LENGTH_SHORT).show();
        }


        return v;
    }
}
