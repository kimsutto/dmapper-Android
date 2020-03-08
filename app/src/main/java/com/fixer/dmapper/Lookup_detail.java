package com.fixer.dmapper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Lookup_detail extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * 서버에서 값 가져오면 DataContainer_init_variable() 함수 이용하면 돼
     * 저기에 매개변수 넣어놔서 서버에서 값 가져오면 저 함수통해서 변수들 초기화 되도록 하고
     * DataContainer_init_variable() 이걸로 싹다 set해버리면 돼
     */
    SupportMapFragment mapFragment;
    private GoogleMap gMap;
    CoordinatorLayout mRootLayout;
    CollapsingToolbarLayout mCollapsingToolbarLayout;


    private LatLng Location_place;


    //placename은 텍스트뷰에 set해주는게 아니고 init_variable()함수에서 mCollapsingToolbarLayout.setTitle("My App Title"); 여기에 넣어주면 됌
    TextView category_tv, phone_num_tv, etc_tv,address_name_tv, address2_name_tv,register_user_name_tv, register_datetime_tv , register_kakao_tv, register_google_tv;
    ImageView place_iv;
    ImageView entrance_iv, elevator_iv, parking_iv, toilet_iv, seat_iv;

    LinearLayout entrance_layout, elevator_layout, parking_layout, toilet_layout, seat_layout;

    String place_url;
    String place_name; //장소명
    String category;// 카테고리
    String phone_num;
    String address_name;
    String etc;// 부가정보
    Bitmap place_img;
    boolean entrance =false;
    boolean elevator=false;
    boolean parking=false;
    boolean toilet, seat; //서버로부터 입구 엘리베이터 등등의 유무가 true(자료형은 자유인데 보내줄때 체크박스로 해서)라면 그에 맞게 setImageDrawable해주면 됌.
    //if문에서 조건문으로 쓰라고 만든 변수임.
    String register_user_name;
    String register_datetime;
    boolean kakao, google;
    double latitude_, longitude_;
    String name_, address_, date_, category_, phone_, etc_,userName_;
    boolean entrance_, elevator_,parking_,toilet_,seat_;
    boolean entrance__ = false;
    boolean parking__ = false;
    boolean kakao_,google_;
    Bitmap img_;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_detail);

        Intent intent = getIntent();
        name_ =  intent.getStringExtra("name");
        userName_ = intent.getStringExtra("userName");
        address_  = intent.getStringExtra("address");
        date_= intent.getStringExtra("date");
        category_=intent.getStringExtra("category");
        phone_=intent.getStringExtra("phone");
        etc_=intent.getStringExtra("etc");
        entrance_=intent.getBooleanExtra("entrance",false);
        elevator_=intent.getBooleanExtra("elevator",false);
        parking_=intent.getBooleanExtra("parking",false);
        toilet_=intent.getBooleanExtra("toilet",false);
        seat_=intent.getBooleanExtra("seat",false);
        kakao_ = intent.getBooleanExtra("kakao",false);
        google_ = intent.getBooleanExtra("google",false);
        img_ = (Bitmap)intent.getParcelableExtra("img");
        latitude_ = intent.getDoubleExtra("lat", 0.0);
        longitude_ = intent.getDoubleExtra("lon", 0.0);
        entrance__ = intent.getBooleanExtra("entrance", false);
        parking__ = intent.getBooleanExtra("parking",false);
        Location_place = new LatLng(latitude_, longitude_);
        Log.d("l", String.valueOf(latitude_));
        Log.d("ln", String.valueOf(longitude_));


        init_variable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataContainer_init_variable();
        DataContainer_Bind_View();
    }

    public void init_variable(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);


        category_tv = (TextView)findViewById(R.id.detail_category);
        phone_num_tv = (TextView)findViewById(R.id.detail_phone_num);
        etc_tv = (TextView)findViewById(R.id.detail_etc);
        address_name_tv = (TextView)findViewById(R.id.detail_location);//위치 넣는 칸이 두개임 최대한 많아보이게 하려고
        address2_name_tv = (TextView)findViewById(R.id.detail_location2);
        register_user_name_tv = (TextView)findViewById(R.id.detail_register_user);
        register_datetime_tv = (TextView)findViewById(R.id.detail_register_datetime);
        register_kakao_tv = (TextView)findViewById(R.id.detail_register_kakaotalk);
        register_google_tv = (TextView)findViewById(R.id.detail_register_google);
        place_iv = (ImageView)findViewById(R.id.parallax_header_imageview);


        entrance_layout = (LinearLayout)findViewById(R.id.entrance_layout);
        elevator_layout = (LinearLayout)findViewById(R.id.elevator_layout);
        parking_layout = (LinearLayout)findViewById(R.id.parking_layout);
        toilet_layout = (LinearLayout)findViewById(R.id.toilet_layout);
        seat_layout = (LinearLayout)findViewById(R.id.seat_layout);
        /*
        entrance_iv = (ImageView)findViewById(R.id.entrance_iv);
        elevator_iv = (ImageView)findViewById(R.id.elevator_iv);
        parking_iv = (ImageView)findViewById(R.id.parking_iv);
        toilet_iv = (ImageView)findViewById(R.id.toilet_iv);
        seat_iv = (ImageView)findViewById(R.id.seat_iv);*/

        mRootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorRootLayout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayoutAndroidExample);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_view);
        //Location_place = new LatLng(latitude_, longitude_);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Location_place,17.0f));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Location_place);
        gMap.addMarker(markerOptions);
    }

    public void DataContainer_init_variable(){

        place_name = name_;
        category = category_;
        phone_num = phone_;
        address_name = address_;


        etc=etc_;
        register_user_name = userName_;
        register_datetime = date_;

        place_img = img_;
        place_url = "https://www.travelopy.com/static/img/cover.jpg";
        entrance = entrance__;
        elevator = elevator_;
        parking = parking__;
        toilet = toilet_;
        seat = seat_;

        kakao = kakao_;
        google = google_;

    }

    public void DataContainer_Bind_View(){
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.bb_darkBackgroundColor));//타이틀컬러
        mCollapsingToolbarLayout.setTitle(place_name); //"장소명 대신 place_name 변수" 넣으면 돼
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        place_iv.setImageBitmap(place_img);
        //Glide.with(this).load(place_url).thumbnail(0.1f).into(place_iv);
        //Glide.with(this).asBitmap().thumbnail(0.1f).into(place_iv);

        //phone number랑 etc는 부가입력이라서 유저가 입력 안했을 수도 있기 때문에 if else 만들어주자 입력 안했으면 그냥 하이픈 하나 뜨게
        category_tv.setText(category);
        phone_num_tv.setText(phone_num);
        address_name_tv.setText(address_name);
        address2_name_tv.setText(address_name);
        etc_tv.setText(etc);
        register_user_name_tv.setText(register_user_name);
        register_datetime_tv.setText(register_datetime);


        if(entrance == false){
            entrance_layout.setVisibility(View.GONE);
            //entrance_iv.setImageResource(R.drawable.entrance);
        }
        if(elevator == false){
            elevator_layout.setVisibility(View.GONE);
            //elevator_iv.setImageResource(R.drawable.elevator);
        }
        if(parking == false){
            parking_layout.setVisibility(View.GONE);
            //parking_iv.setImageResource(R.drawable.parking);
        }
        if(toilet == false){
            toilet_layout.setVisibility(View.GONE);
            //toilet_iv.setImageResource(R.drawable.toilet);
        }
        if(seat == false){
            seat_layout.setVisibility(View.GONE);
            //seat_iv.setImageResource(R.drawable.seat);
        }
        if(kakao == true){
            register_kakao_tv.setText("카카오톡"); //텍스트로 해도되고 이쁜 이미지로 바꿔도돼
        }
        if(google == true){
            register_google_tv.setText("구글");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
