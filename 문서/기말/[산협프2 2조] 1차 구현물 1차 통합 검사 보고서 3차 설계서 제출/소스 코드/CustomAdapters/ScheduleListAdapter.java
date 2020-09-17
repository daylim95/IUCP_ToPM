package com.example.knk.topm.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knk.topm.Object.MovieSchedule;
import com.example.knk.topm.R;

import java.util.ArrayList;

public class ScheduleListAdapter extends ArrayAdapter<MovieSchedule> implements View.OnClickListener{   // 삭제버튼 때문에 클릭리스너 implements

    private Context context;                                                // 어댑터를 선언한 액티비티의 context를 저장함
    private ArrayList<MovieSchedule> data;                                  // 어댑터를 선언한 액티비티의 스케쥴객체배열을 저장함
    private int resource;                                                   // 어댑터가 적용될 레이아웃을 저장함(여기서는 리스트 뷰의 열(row)의 레이아웃)
    private ScheduleDeleteBtnClickListener scheduleDeleteBtnClickListener;  // 인터페이스로 선언한 클릭리스너
    // 어댑터를 선언한 액티비티로부터 받아온 정보 - 이 어댑터에서 쓰지 않고 삭제버튼 클릭 시 어댑터선언한 액티비티에게 다시 알려주기 위해 보관하는 용도
    private String strDateKey;      // 어댑터에 들어있는 스케쥴객체의 상영날짜 스트링
    private int dateCount;          // 그 상영날짜가 오늘날짜로부터 얼마나 뒤의 날인지 저장

    private final int RESOURCE_TYPE_FOR_SCHEDULE_MANAGE = R.layout.schedule_list_adapter_row;
    private final int RESOURCE_TYPE_FOR_ADMIN_MAIN = R.layout.schedule_list_adpater_row2;

    // 생성자 오버로딩 1
    // ScheduleManageActivity에서 사용하는 생성자. 한 줄(row) 마다 삭제 버튼이 있음.
    // 어댑터에는 현재액티비티의 context, 뿌려줄 리스트뷰 한 줄(row)의 레아이웃, 뿌려줄 데이터 정보, 삭제버튼 클릭리스너, 현재설정된 날짜, 현재선택한날짜-오늘날짜
    public ScheduleListAdapter(@NonNull Context context, int resource, ArrayList<MovieSchedule> objects, ScheduleDeleteBtnClickListener listener, String strDateKey, int dateCount) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
        this.resource = resource;
        this.scheduleDeleteBtnClickListener = listener;
        this.strDateKey = strDateKey;
        this.dateCount = dateCount;
    }
    // 생성자 오버로딩 2
    // AdminMainActivity에서 사용하는 생성자. 버튼은 사용하지 않고 오직 정보만 보여줌.
    public ScheduleListAdapter(@NonNull Context context, int resource, ArrayList<MovieSchedule> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
        this.resource = resource;
        //필요없는 부분 - null로 둠
        this.scheduleDeleteBtnClickListener = null;
        this.strDateKey = null;
        this.dateCount = -1;
    }

    //삭제버튼 클릭리스너 인터페이스
    public interface ScheduleDeleteBtnClickListener{
        //현재 클릭한 row의 리스트뷰에서의 position을 인자로 받음. -> 어댑터를 선언한 액티비티에서 position을 알 수 있게 됨.
        //마찬가지로 strDateKey와 dateCount 알 수 있게 됨.
        void onScheduleDeleteBtnClick(int position, String strDateKey, int dateCount);
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

    //리스트뷰의 한 열(row)를 구성해주는 오버라이드 함수
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = view;          //파라미터 옮겨놓기
        TextView title;         //영화 제목을 보여주는 텍스트뷰
        TextView time;          //영화 시간을 보여주는 텍스트뷰
        TextView screen;        //상영관 번호를 보여주는 텍스트뷰
        Button deleteBtn;       //스케쥴을 삭제할 버튼

        if(v == null){          //inflate할 뷰 생성
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

        //현재 포지션에 데이터에 인덱스 순으로 스케쥴인스턴스 생성
        MovieSchedule movieSchedule = data.get(position);
        title = v.findViewById(R.id.titleTextView);
        time = v.findViewById(R.id.timeTextview);
        screen = v.findViewById(R.id.screenTextview);

        if(movieSchedule != null){
            if(title!=null&&time!=null&&screen!=null){
                // 영화제목, 시간, 상영관,(for 영화스케쥴 추가) 삭제버튼(for 영화스케쥴 삭제) 이 모든 것이 필요한 경우 = ScheduleManageActivity
                if(resource == RESOURCE_TYPE_FOR_SCHEDULE_MANAGE){
                    title.setText(movieSchedule.getMovieTitle());
                    time.setText(movieSchedule.screeningDate.getHours()+":" + movieSchedule.screeningDate.getMinutes());
                    screen.setText(movieSchedule.getScreenNum()+"관 ");
                    deleteBtn = v.findViewById(R.id.deleteBtn);
                    if (deleteBtn!=null){
                        // 삭제버튼에 해당 포지션 태그 달아주고
                        deleteBtn.setTag(position);
                        // 클릭리스너도 달아줌
                        deleteBtn.setOnClickListener(this);
                    }
                }
                // 위의 것들이 필요 없는 경우 = AdminMainActivity
                else if(resource == RESOURCE_TYPE_FOR_ADMIN_MAIN){
                    title.setText(movieSchedule.getMovieTitle());
                    time.setText(movieSchedule.screeningDate.getHours()+":" + movieSchedule.screeningDate.getMinutes());
                    screen.setText(movieSchedule.getScreenNum()+"관 ");
                }
            }
            else{
                Toast.makeText(getContext(),"텍스트 뷰가 제대로 초기화되지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getContext(),"스케쥴 정보를 받아오는데 실패했습니다.",Toast.LENGTH_SHORT).show();
        }


        return v;
    }

    //해당 리스너를 implements한 액티비티에 오버라이딩되는 함수
    @Override
    public void onClick(View v) {
        if(this.scheduleDeleteBtnClickListener != null){
            //어댑터를 선언했던 액티비티에 해당 정보를 파라미터로 넘겨줌
            this.scheduleDeleteBtnClickListener.onScheduleDeleteBtnClick((int)v.getTag(), strDateKey, dateCount);
        }
    }
}
