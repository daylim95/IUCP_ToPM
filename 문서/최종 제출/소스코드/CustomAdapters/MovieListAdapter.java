package com.example.knk.topm.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.knk.topm.Object.Movie;
import com.example.knk.topm.R;

import java.util.ArrayList;


public class MovieListAdapter extends ArrayAdapter<Movie> implements View.OnClickListener{  //삭제버튼 때문에 클릭리스너 implements

    private Context context;                                    //어댑터를 선언한 액티비티의 context를 저장함
    private ArrayList<Movie> data;                              //어댑터를 선언한 액티비티의 영화객체배열을 저장함
    private int resource;                                       //어댑터가 적용될 레이아웃을 저장함(여기서는 리스트 뷰의 열(row)의 레이아웃)
    private MovieDeleteBtnClickListener movieDeleteBtnClickListener;    //인터페이스로 선언한 클릭리스너

    //삭제버튼 클릭리스너 인터페이스
    public interface MovieDeleteBtnClickListener{
        void onMovieDeleteBtnClick(final int position);         //현재 클릭한 row의 리스트뷰에서의 position을 인자로 받음. -> 어댑터를 선언한 액티비티에서 position을 알 수 있게 됨.
    }

    //생성자 : 인자 - 어댑터를 넣을 곳의 context, 어댑터가 뷰를 뿌려줄 레이아웃 정보, 뷰에 뿌릴 정보, 삭제버튼을 위한 클릭리스너
    public MovieListAdapter(Context context, int resource, ArrayList objects, MovieDeleteBtnClickListener clickListener) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
        this.resource = resource;
        this.movieDeleteBtnClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Movie getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //리스트뷰의 한 열(row)를 구성해주는 오버라이드 함수
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = view;              //파라미터 옮겨놓기
        TextView movieTitle;        //영화 이름을 출력할 텍스트뷰
        Button deleteBtn;           //영화를 삭제할 버튼

        if(v==null){                //inflate할 뷰 생성
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

        //현재 포지션에 데이터에 인덱스 순으로 무비인스턴스 생성
        Movie movie = data.get(position);
        //정상적으로 인스턴스가 생성됐다면
        if(movie!=null){
            //타이틀 텍스트뷰 초기화후
            movieTitle = v.findViewById(R.id.movielistTextview);
            deleteBtn = v.findViewById(R.id.deleteBtn);
            //정상적 초기화 됐을 때
            if (movieTitle!=null){
                //영화 이름으로 setText
                movieTitle.setText(movie.getTitle());
            }
            if(deleteBtn!=null){
                //삭제버튼에 해당 포지션 태그 달아주고
                deleteBtn.setTag(position);
                //클릭리스너도 달아줌
                deleteBtn.setOnClickListener(this);
            }
        }

        //뷰를 리턴
        return v;
    }


    @Override
    public void onClick(View v) {
        if(this.movieDeleteBtnClickListener != null){
            this.movieDeleteBtnClickListener.onMovieDeleteBtnClick((int)v.getTag());
        }
    }
}
