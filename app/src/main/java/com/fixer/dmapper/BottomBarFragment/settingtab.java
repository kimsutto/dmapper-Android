package com.fixer.dmapper.BottomBarFragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixer.dmapper.Lookup_detail;
import com.fixer.dmapper.MainActivity;
import com.fixer.dmapper.NoticeActivity;
import com.fixer.dmapper.R;

import java.util.ArrayList;

public class settingtab extends Fragment {

    ListView list_notice;
    ListView list_ranking;
    RecyclerView recy_myrequest;
    LookupDataAdapter lookupDataAdapter;
    private ArrayList<notice> notice_list;

    ConstraintLayout myrequest_seemorebtn;
    ConstraintLayout ranking_seemorebtn;

    TextView notice_tv;
    TextView myrequest_tv;
    TextView ranking_tv;

    ImageView myrequest_iv;
    ImageView ranking_iv;

    Boolean myrequest_Click_state = false;
    Boolean ranking_Click_state = false;
    View view;

    public settingtab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settingtab, container, false);
        notice_list = new ArrayList<notice>();
        init_variable();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).StartAnimationInvisible();


        onListItemButtonClick();



        myrequest_seemorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myrequest_Click_state == false) {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1500);
                    recy_myrequest.setLayoutParams(lp);
                    myrequest_tv.setText("접기");
                    myrequest_iv.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    myrequest_Click_state = true;
                } else {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 500);
                    recy_myrequest.setLayoutParams(lp);
                    myrequest_tv.setText("더 보기");
                    myrequest_iv.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    myrequest_Click_state = false;
                }
            }
        });

        ranking_seemorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ranking_Click_state == false) {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1500);
                    list_ranking.setLayoutParams(lp);
                    ranking_tv.setText("접기");
                    ranking_iv.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    ranking_Click_state = true;
                } else {
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 500);
                    list_ranking.setLayoutParams(lp);
                    ranking_tv.setText("더 보기");
                    ranking_iv.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    ranking_Click_state = false;
                }
            }
        });

    }

    public void init_variable() {

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>" + "설정" + "</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#829FD9")));

        ((MainActivity) getActivity()).StartAnimationgone();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        myrequest_seemorebtn = (ConstraintLayout) view.findViewById(R.id.seemore_myrequest);
        ranking_seemorebtn = (ConstraintLayout) view.findViewById(R.id.seemore_ranking);

        notice_tv = (TextView) view.findViewById(R.id.notice_tv);
        myrequest_tv = (TextView) view.findViewById(R.id.myrequest_tv);
        ranking_tv = (TextView) view.findViewById(R.id.ranking_tv);

        myrequest_iv = (ImageView) view.findViewById(R.id.myrequest_imageView);
        ranking_iv = (ImageView) view.findViewById(R.id.ranking_imageView);

        list_notice = (ListView) view.findViewById(R.id.list_notice);
        list_ranking = (ListView) view.findViewById(R.id.list_ranking);

        //공지 리스트뷰 설정
        noticeAdapter noticeAdapter = new noticeAdapter(getContext(), notice_list);
        notice_list.add(new notice("공지사항", ""));
        notice_list.add(new notice("이벤트 관련 공지", ""));
        notice_list.add(new notice("서비스 이용약관", ""));
        notice_list.add(new notice("개인정보 처리방침", ""));

        list_notice.setAdapter(noticeAdapter);

        //클릭시 해당 공지 이동
        list_notice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String n =notice_list.get(position).getNoticeTitle();
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                intent.putExtra("title",n);
                startActivity(intent);
            }
        });

        //내가 추가한 정보 리사이클러뷰 설정
        recy_myrequest = (RecyclerView) view.findViewById(R.id.recy_myrequest);
        recy_myrequest.setLayoutManager(new LinearLayoutManager(getContext()));
        lookupDataAdapter = new LookupDataAdapter();
        if(MainActivity.arrayMyloc != null){
            lookupDataAdapter.setArrayList(MainActivity.arrayMyloc,getContext());
        }
        recy_myrequest.setAdapter(lookupDataAdapter);
        lookupDataAdapter.notifyDataSetChanged();

        //랭킹 리스트뷰 설정
        rankAdapter rankAdapter = new rankAdapter(getContext(),MainActivity.arrayRank);
        list_ranking.setAdapter(rankAdapter);


    }

    //장소 상세 정보 보기
    public void onListItemButtonClick(){
        lookupDataAdapter.setOnItemClickListener(new LookupDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //이미지 오류 오지게나서 그냥 값 하나씩 넘김
                Intent intent = new Intent(getActivity(), Lookup_detail.class);
                String name =lookupDataAdapter.place_list.get(pos).getPlace_name();
                String address = lookupDataAdapter.place_list.get(pos).getPlace_address();

                String user = lookupDataAdapter.place_list.get(pos).getUser_name();
                String date = lookupDataAdapter.place_list.get(pos).getUpload_date();

                String category = lookupDataAdapter.place_list.get(pos).getCategory();
                String phone = lookupDataAdapter.place_list.get(pos).getPhone();
                String etc = lookupDataAdapter.place_list.get(pos).getEct_info();

                double lat = lookupDataAdapter.place_list.get(pos).getLatitude();
                double lon = lookupDataAdapter.place_list.get(pos).getLongitude();

                boolean entrance = lookupDataAdapter.place_list.get(pos).isEntrance();
                boolean seat = lookupDataAdapter.place_list.get(pos).isSeat();
                boolean parking = lookupDataAdapter.place_list.get(pos).isParking();

                boolean toilet = lookupDataAdapter.place_list.get(pos).isToilet();
                boolean elevator = lookupDataAdapter.place_list.get(pos).isElevator();

                boolean kakao = lookupDataAdapter.place_list.get(pos).isKakao();
                boolean google = lookupDataAdapter.place_list.get(pos).isGoogle();

                Bitmap img = lookupDataAdapter.place_list.get(pos).getImage();

                intent.putExtra("name",name);
                intent.putExtra("address",address);
                intent.putExtra("date",date);
                intent.putExtra("userName",user);
                intent.putExtra("category",category);
                intent.putExtra("phone",phone);
                intent.putExtra("etc",etc);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                intent.putExtra("entrance",entrance);
                intent.putExtra("seat",seat);
                intent.putExtra("parking",parking);
                intent.putExtra("toilet",toilet);
                intent.putExtra("elevator",elevator);
                intent.putExtra("kakao",kakao);
                intent.putExtra("google",google);
                intent.putExtra("img", img);

                startActivity(intent);
            }
        });
    }

    class notice {
        private String noticeTitle;

        public notice(String noticeTitle, String noticeContent) {
            this.noticeTitle = noticeTitle;
        }

        public String getNoticeTitle() {
            return noticeTitle;
        }

        public void setNoticeTitle(String noticeTitle) {
            this.noticeTitle = noticeTitle;
        }
    }

    class noticeAdapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<notice> notices;

        public noticeAdapter(Context context, ArrayList<notice> data) {
            mContext = context;
            notices = data;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return notices.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public notice getItem(int position) {
            return notices.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.notice_item, null);
            TextView notice_title = (TextView) view.findViewById(R.id.notice_title);
            notice_title.setText(notices.get(position).getNoticeTitle());

            return view;
        }
    }

    public static class rank {
        private String rankO;
        private String userName;
        private String uploadNum;

        public rank(String rankO, String userName, String uploadNum) {
            this.rankO = rankO;
            this.userName = userName;
            this.uploadNum = uploadNum;
        }

        public String getRankO() {
            return rankO;
        }

        public void setRankO(String rankO) {
            this.rankO = rankO;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUploadNum() {
            return uploadNum;
        }

        public void setUploadNum(String uploadNum) {
            this.uploadNum = uploadNum;
        }
    }

    class rankAdapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<rank> ranks;

        public rankAdapter(Context context, ArrayList<rank> data) {
            mContext = context;
            ranks = data;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (ranks != null){
                return ranks.size();
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public rank getItem(int position) {
            return ranks.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.rank_item, null);

            TextView rank0= (TextView) view.findViewById(R.id.rankO);
            TextView userName = (TextView) view.findViewById(R.id.userName);
            TextView uploadNum = (TextView) view.findViewById(R.id.uploadNum);

            rank0.setText(ranks.get(position).getRankO());
            userName.setText(ranks.get(position).getUserName());
            uploadNum.setText(ranks.get(position).getUploadNum());

            return view;
        }
    }


}