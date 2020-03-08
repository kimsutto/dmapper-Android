package com.fixer.dmapper.PlaceRequest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.location.Geocoder;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fixer.dmapper.BottomBarFragment.LookupDataProducts;

import com.fixer.dmapper.LoginActivity;
import com.fixer.dmapper.MainActivity;
import com.fixer.dmapper.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PlaceUpdateRequest extends AppCompatActivity {



    //소켓 관련 변수
    Socket socket;
    private String ip = "52.79.214.170"; // IP 주소
    private int port = 8888; // PORT번호'
    private Handler socketHandler;
    private Handler mHandler;

    Button submit_btn;
    ImageView Address_Reload_btn;

    TextInputLayout textInputLayout,textInputLayout2,textInputLayout3,textInputLayout4;
    TextInputEditText place_name_et,address_name_et,phonenumber_et,etcinfo_et;

    CheckBox kakao_map_check, google_map_check, entrance_check, seat_check, parking_check ,restroom_check, elevator_check;
    //카테고리 추가햇음
    private Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    String user_id;
    String user_name;

    InputMethodManager imm;

    com.fixer.dmapper.BottomBarFragment.kakaomaptab kakaomaptab;
    com.fixer.dmapper.BottomBarFragment.googlemaptab googlemaptab;

    String place_name_st,address_name_st,category_name_st,phonenumber_st,etcinfo_st;
    Boolean kakao_bool,google_bool,entrance_bool,seat_bool,parking_bool,restroom_bool,elevator_bool;

    String place_type = "2";// 수정은 2


    public double latitude;
    public double longitude;
    String latitude_st;
    String longitude_st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_update_request);

        init_variable();
        init_spinner_list();
        init_BindValue();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //getValue();
        onClickBox();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(place_name_et.getText().toString().trim().length() > 0 && address_name_et.getText().toString().trim().length() > 0 && (google_map_check.isChecked() || kakao_map_check.isChecked())) {
                    Toast.makeText(PlaceUpdateRequest.this, "완료", Toast.LENGTH_SHORT).show();
                    mHandler = new Handler();
                    //넘버 맞춰줄라고 걍 빈값 넣음
                    String IP_ADDRESS = "52.79.214.170";
                    String ii = " ";
                    PlaceAddRequest.InsertData task = new PlaceAddRequest.InsertData();
                    task.execute("http://" + IP_ADDRESS + "/add_img2.php", ii);

                    ConnectThread th = new ConnectThread();
                    th.start();

                    finish();
                }else{
                    Toast.makeText(PlaceUpdateRequest.this, "필수정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    class ConnectThread extends Thread {
        public void run() {
            try {
                //소켓 생성
                InetAddress serverAddr = InetAddress.getByName(ip);
                socket = new Socket(serverAddr, port);
                getValue();
                //입력 메시지
                String sndMsg = "" + place_name_st + "," + address_name_st + "," + category_name_st + "," + etcinfo_st +
                        "," + kakao_bool + "," + google_bool + "," + entrance_bool + "," + seat_bool + "," + parking_bool +
                        "," + restroom_bool + "," + elevator_bool + "," + phonenumber_st+ "," +user_id +","+place_type+","+latitude_st +
                        "," + longitude_st;;

                Log.i("@@", "pl" + place_name_st + "ad" + address_name_st + "ca" + category_name_st + "et" + etcinfo_st +
                        "ka" + kakao_bool + "go" + google_bool + "en" + entrance_bool + "se" + seat_bool + "pa" + parking_bool + "re" + restroom_bool + "el" + elevator_bool);

                //데이터 전송
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(sndMsg);

                LookupDataProducts a = new LookupDataProducts(place_name_st,address_name_st, null, user_name, null, null,latitude,longitude,category_name_st,phonenumber_st,etcinfo_st,entrance_bool,elevator_bool,parking_bool,restroom_bool,seat_bool,kakao_bool,google_bool);
                MainActivity.arrayLoc.add(a);
                MainActivity.arrayMyloc.add(a);

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void init_spinner_list() {
        arrayList = new ArrayList<>();
        arrayList.add("장애인용 공용 화장실");
        arrayList.add("숙박업소");
        arrayList.add("보건소");
        arrayList.add("음식점");
        arrayList.add("주차장");
        arrayList.add("문화센터");
        arrayList.add("교육센터");
        arrayList.add("병원");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner = (Spinner) findViewById(R.id.category_spinner);
        spinner.setAdapter(arrayAdapter);
    }

    public void init_variable(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(Html.fromHtml("<font color='#746E66'>"+"정보 수정"+"</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        submit_btn = (Button)findViewById(R.id.place_add_submit_btn);
        Address_Reload_btn = (ImageView) findViewById(R.id.reload_address_btn);

        textInputLayout = (TextInputLayout)findViewById(R.id.textinputlayout);
        place_name_et = (TextInputEditText)findViewById(R.id.placename_et);

        textInputLayout2 = (TextInputLayout)findViewById(R.id.textinputlayout2);
        address_name_et = (TextInputEditText)findViewById(R.id.addressname_et);

        textInputLayout3 = (TextInputLayout)findViewById(R.id.textinputlayout3);
        phonenumber_et = (TextInputEditText)findViewById(R.id.phonenumber_et);

        textInputLayout4 = (TextInputLayout)findViewById(R.id.textinputlayout4);
        etcinfo_et = (TextInputEditText)findViewById(R.id.etcinfo_et);

        kakao_map_check = (CheckBox)findViewById(R.id.kakao_map_check);
        google_map_check = (CheckBox)findViewById(R.id.google_map_check);
        entrance_check = (CheckBox)findViewById(R.id.wheel_Entrance_check);
        seat_check = (CheckBox)findViewById(R.id.wheel_seat_check);
        parking_check = (CheckBox)findViewById(R.id.wheel_parking_check);
        restroom_check = (CheckBox)findViewById(R.id.wheel_restroom_check);
        elevator_check = (CheckBox)findViewById(R.id.wheel_elevator_check);

        user_id = MainActivity.M_user_id;
        user_name = MainActivity.M_user_name;

        textInputLayout4.setCounterEnabled(true);
        textInputLayout4.setCounterMaxLength(50);

    }

    private void init_BindValue(){


            if (MainActivity.Map_foreground_selector_kakao == true || MainActivity.Map_foreground_selector_google == false) {
                if(kakaomaptab.place_name_query_result.equals("") && kakaomaptab.address_name_query_result.equals("")){
                    place_name_et.setText("검색하여 수정할 장소를 선택해주세요");
                    address_name_et.setText("검색하여 수정할 장소를 선택해주세요");
                }else {
                    place_name_et.setText(kakaomaptab.place_name_query_result);
                    if(getReload_Address_Value() == null) {
                        address_name_et.setText(kakaomaptab.address_name_query_result);
                    }else{
                        address_name_et.setText(getReload_Address_Value());
                    }
                }
            } else if (MainActivity.Map_foreground_selector_google == true || MainActivity.Map_foreground_selector_kakao == false) {
                if(googlemaptab.place_name_query_result.equals("") && googlemaptab.address_name_query_result.equals("")){
                    place_name_et.setText("검색하여 수정할 장소를 선택해주세요");
                    address_name_et.setText("검색하여 수정할 장소를 선택해주세요");
                }else {
                    place_name_et.setText(googlemaptab.place_name_query_result);
                    if(getReload_Address_Value() == null){
                        address_name_et.setText(googlemaptab.address_name_query_result);
                    }else{
                        address_name_et.setText(getReload_Address_Value());
                    }
                }
            }

        kakao_map_check.setChecked(false);
        google_map_check.setChecked(false);
        entrance_check.setChecked(false);
        seat_check.setChecked(false);
        parking_check.setChecked(false);
        restroom_check.setChecked(false);
        elevator_check.setChecked(false);


        latitude = LatLngCarrier.latitude;
        longitude = LatLngCarrier.longitude;
        latitude_st = String.valueOf(latitude);
        longitude_st = String.valueOf(longitude);

        System.out.println("@@@"+latitude_st);

        //Log.i("position2","latitude"+latitude_st+" longitude"+longitude_st);
        //Toast.makeText(this, "latitude_st : "+latitude_st + "longitude_st : "+longitude_st, Toast.LENGTH_SHORT).show();
    }
    private void getValue(){

        place_name_st = place_name_et.getText().toString();
        address_name_st = address_name_et.getText().toString();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category_name_st = arrayList.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        user_id = user_id;

        phonenumber_st = phonenumber_et.getText().toString();
        etcinfo_st = etcinfo_et.getText().toString();

        if (kakao_map_check.isChecked()) kakao_bool = true;
        else kakao_bool = false;


        if (google_map_check.isChecked()) google_bool = true;
        else google_bool = false;

        if (entrance_check.isChecked()) entrance_bool = true;
        else entrance_bool = false;

        if (seat_check.isChecked()) seat_bool = true;
        else seat_bool = false;

        if (parking_check.isChecked()) parking_bool = true;
        else parking_bool = false;

        if (restroom_check.isChecked()) restroom_bool = true;
        else restroom_bool = false;

        if (elevator_check.isChecked()) elevator_bool = true;
        else elevator_bool = false;

    }

    private void onClickBox(){
        Address_Reload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceUpdateRequest.this,GoogleAddressReSelect.class);
                startActivity(intent);
            }
        });
    }
    private String getReload_Address_Value(){
        String getAddress = getIntent().getStringExtra("Reload_Address_Value");
        return getAddress;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
