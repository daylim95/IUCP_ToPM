package com.example.knk.topm.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knk.topm.Object.MovieSchedule;
import com.example.knk.topm.R;

import java.util.ArrayList;

public class NormalScheduleListAdapter extends ArrayAdapter<MovieSchedule> {

    private Context context;                                                // 어댑터를 선언한 액티비티의 context를 저장함
    private ArrayList<MovieSchedule> data;                                  // 어댑터를 선언한 액티비티의 스케쥴객체배열을 저장함
    private int resource;                                                   // 어댑터가 적용될 레이아웃을 저장함(여기서는 리스트 뷰의 열(row)의 레이아웃)
    // 어댑터를 선언한 액티비티로부터 받아온 정보 - 이 어댑터에서 쓰지 않고 삭제버튼 클릭 시 어댑터선언한 액티비티에게 다시 알려주기 위해 보관하는 용도

    public NormalScheduleListAdapter(@NonNull Context context, int resource, ArrayList<MovieSchedule> objects) {
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
    public MovieSchedule getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // 리스트뷰의 한 열(row)를 구성해주는 오버라이드 함수
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = view;          // 파라미터 옮겨놓기
        TextView title;         // 영화 제목을 보여주는 텍스트뷰
        TextView time;          // 영화 시간을 보여주는 텍스트뷰
        TextView screen;        // 상영관 번호를 보여주는 텍스트뷰

        if(v == null){          // inflate할 뷰 생성
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

        //현재 포지션에 데이터에 인덱스 순으로 스케쥴인스턴스 생성
        MovieSchedule movieSchedule = data.get(position);
        title = v.findViewById(R.id.titleTextView);
        time = v.findViewById(R.id.timeTextview);
        screen = v.findViewById(R.id.screenTextview);

        if(movieSchedule != null){
            if (title != null && time != null && screen != null){
                title.setText(movieSchedule.getMovieTitle());
                time.setText(movieSchedule.screeningDate.getHours()+":" + movieSchedule.screeningDate.getMinutes());
                screen.setText(movieSchedule.getScreenNum()+"관 ");
            }else{
                if(title==null)
                    Toast.makeText(getContext(),"title이 null이다.",Toast.LENGTH_SHORT).show();
                if(time==null)
                    Toast.makeText(getContext(),"time이 null이다.",Toast.LENGTH_SHORT).show();
                if(screen==null)
                    Toast.makeText(getContext(),"screen이 null이다.",Toast.LENGTH_SHORT).show();
            }
        }

        return v;
    }
}

