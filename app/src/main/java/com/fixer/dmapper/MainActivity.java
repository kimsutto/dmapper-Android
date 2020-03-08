package com.fixer.dmapper;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fixer.dmapper.BottomBarFragment.LookupDataProducts;
import com.fixer.dmapper.BottomBarFragment.RequestHttpURLConnection;
import com.fixer.dmapper.BottomBarFragment.googlemaptab;
import com.fixer.dmapper.BottomBarFragment.kakaomaptab;
import com.fixer.dmapper.BottomBarFragment.lookupdatatab;
import com.fixer.dmapper.BottomBarFragment.settingtab;
import com.fixer.dmapper.BottomBarFragment.settingtab.rank;
import com.fixer.dmapper.PlaceRequest.PlaceAddRequest;
import com.fixer.dmapper.PlaceRequest.PlaceUpdateRequest;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static Boolean Map_foreground_selector_kakao = false;
    public static Boolean Map_foreground_selector_google = true;

    private com.fixer.dmapper.BottomBarFragment.googlemaptab googlemaptab;
    private com.fixer.dmapper.BottomBarFragment.kakaomaptab kakaomaptab;
    private com.fixer.dmapper.BottomBarFragment.lookupdatatab lookupdatatab;
    private com.fixer.dmapper.BottomBarFragment.settingtab settingtab;

    Animation translate_up;
    Animation translate_down;
    LinearLayout linearLayout;
    BottomBar bottomBar;
    private Button placeaddbtn;
    private Button placeupdatebtn;

    //로그인 되어있는 현재 회원정보
    public static String M_user_name;
    String M_user_email;
    public static String M_user_id;

    public static ArrayList<rank> arrayRank;
    public static ArrayList<LookupDataProducts> arrayMyloc;
    public static ArrayList<LookupDataProducts> arrayLoc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        googlemaptab = new googlemaptab();
        kakaomaptab = new kakaomaptab();
        lookupdatatab = new lookupdatatab();
        settingtab = new settingtab();

        placeaddbtn = (Button) findViewById(R.id.placeadd);
        placeupdatebtn = (Button) findViewById(R.id.placeupdate);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        translate_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);
        translate_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);

        M_user_id = getIntent().getStringExtra("myid");
        M_user_name = getIntent().getStringExtra("myname");
        M_user_email = getIntent().getStringExtra("myemail");

        //Toast.makeText(this, "id"+M_user_id+"name"+M_user_name+"email"+M_user_email, Toast.LENGTH_SHORT).show();
        String temp = "&id="+M_user_id+"&name=" +M_user_name+"&email="+M_user_email;
        InsertUser iu = new InsertUser(temp);
        iu.start();

        //각 디비 불러오기 fragment필요한 디비들 다 main에서 부르는게 낫다 오류 오지게난다
        NetworkTask_rank networkTask_rank = new NetworkTask_rank("http://52.79.214.170/get_rank.php", null);
        networkTask_rank.execute();

        NetworkTask_myloc NetworkTask_myloc = new NetworkTask_myloc("http://52.79.214.170/get_loc_user.php?&user_id=" + M_user_id, null);
        NetworkTask_myloc.execute();


        NetworkTask NetworTask = new NetworkTask("http://52.79.214.170/get_loc_db2.php", null);
        NetworTask.execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //initFragment();

        //정보추가 버튼 클릭
        placeaddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlaceAddRequest.class);
                startActivity(intent);
            }
        });
        //정보수정 버튼 클릭
        placeupdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlaceUpdateRequest.class);
                startActivity(intent);
            }
        });
        //클릭으로 탭 변경
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    case R.id.tab_registermap:
                        if (MainActivity.Map_foreground_selector_kakao == true || MainActivity.Map_foreground_selector_google == false) {
                            replaceFragment(kakaomaptab);
                            break;
                        } else if (MainActivity.Map_foreground_selector_google == true || MainActivity.Map_foreground_selector_kakao == false) {
                            replaceFragment(googlemaptab);
                            break;
                        }
                    case R.id.tab_LookupData:
                        replaceFragment(lookupdatatab);
                        break;
                    case R.id.tab_Setting:
                        replaceFragment(settingtab);
                        break;
                }
            }
        });
    }

    public void replaceFragment(Fragment newfragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, newfragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void StartAnimationDown() {

        linearLayout.setVisibility(View.INVISIBLE);
        linearLayout.startAnimation(translate_down);
    }

    public void StartAnimationInvisible() {
        linearLayout.setVisibility(View.INVISIBLE);
    }

    public void StartAnimationvisible() {
        linearLayout.setVisibility(View.VISIBLE);
    }

    public void StartAnimationgone() {
        linearLayout.setVisibility(View.GONE);
    }

    public void StartAnimationUp() {
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.startAnimation(translate_up);
    }

    //랭크 DB 가져와서 arrayRank에 넣음
    public class NetworkTask_rank extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask_rank(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            doJSONParser_rank(s);
        }

        void doJSONParser_rank(String str) {
            try {
                if (str != null) {
                    JSONObject order = new JSONObject(str);
                    JSONArray index = order.getJSONArray("root");
                    arrayRank = new ArrayList<>();
                    String rank_name = "";
                    for (int i = 0; i < index.length(); ) {
                        JSONObject tt = index.getJSONObject(i);
                        rank_name = tt.getString("name");
                        i++;
                        arrayRank.add(new settingtab.rank(i + "위", rank_name, ""));
                        if(i==10)
                            break;
                        //lookupDataAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                Log.d("JSON_RANK", "showResult : ", e);
            }
        }
    }

    //내가 쓴 장소 가져와 arrayMyloc에 넣음
    public class NetworkTask_myloc extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask_myloc(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            doJSONParser_myloc(s);
        }

        //값 파싱싱싱
        void doJSONParser_myloc(String str) {

            try {
                if (str != null) {
                    JSONObject order = new JSONObject(str);
                    JSONArray index = order.getJSONArray("root");
                    Bitmap getBlob;
                    arrayMyloc = new ArrayList<>();

                    for (int i = 0; i < index.length(); i++) {
                        String place_name = "";
                        String place_address = "";
                        String place_img = "";
                        String user_name = "";
                        String upload_date = "";
                        String phone = "-";
                        String etc_info = "-";
                        String category = " ";
                        double latitude = 0.0;
                        double longitude = 0.0;
                        boolean kakao = false;
                        boolean google = false;
                        boolean entrance = false;
                        boolean elevator = false;
                        boolean parking_ = false;
                        boolean toilet = false;
                        boolean seat = false;

                        JSONObject tt = index.getJSONObject(i);

                        place_name = tt.getString("name");
                        place_address = tt.getString("address");
                        //이미지 변환
                        place_img = tt.getString("image");
                        Log.d("imgStr", tt.getString("image"));
                        byte[] encodeByte = Base64.decode(place_img, 0);
                        Log.e("byte", encodeByte.toString());
                        getBlob = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                        user_name = tt.getString("userName");
                        //날짜 시간 자름
                        String upload_date_ = tt.getString("uploadDate");
                        String[] upload_date_s = upload_date_.split(" ");
                        upload_date = upload_date_s[0];

                        int i_platform = tt.getInt("platform");
                        //1이면 모두 2면 카카오만 3이면 구글만
                        if (i_platform == 1) {
                            google = true;
                            kakao = true;
                        } else if (i_platform == 2) {
                            kakao = true;
                        } else {
                            google = true;
                        }
                        latitude = tt.getDouble("latitude");
                        longitude = tt.getDouble("longitude");
                        int i_category = tt.getInt("category");
                        switch (i_category) {
                            case 1:
                                category = "장애인용 공용 화장실";
                                break;
                            case 2:
                                category = "숙박업소";
                                break;
                            case 3:
                                category = "보건소";
                                break;
                            case 4:
                                category = "음식점";
                                break;
                            case 5:
                                category = "주차장";
                                break;
                            case 6:
                                category = "문화센터";
                                break;
                            case 7:
                                category = "교육센터";
                                break;
                            case 8:
                                category = "병원";
                                break;
                        }
                        phone = tt.getString("phone");
                        etc_info = tt.getString("etc_info");
                        int i_entrance = tt.getInt("entrance");
                        if (i_entrance == 1) {
                            entrance = true;
                        }
                        int i_seat = tt.getInt("seat");
                        if (i_seat == 1) {
                            seat = true;
                        }
                        Log.d("hah", String.valueOf(parking_));
                        int i_parking = tt.getInt("parking");
                        if (i_parking == 1) {
                            parking_ = true;
                        }
                        Log.d("hah2", String.valueOf(parking_));
                        int i_toilet = tt.getInt("toilet");
                        if (i_toilet == 1) {
                            toilet = true;
                        }
                        int i_elevator = tt.getInt("elevator");
                        if (i_elevator == 1) {
                            elevator = true;
                        }

                        LookupDataProducts products = new LookupDataProducts();

                        products.setImage(getBlob);
                        products.setPlace_name(place_name);
                        products.setPlace_address(place_address);
                        products.setUser_name(user_name);
                        products.setUpload_date(upload_date);
                        products.setKakao(kakao);
                        products.setGoogle(google);
                        products.setLatitude(latitude);
                        products.setLongitude(longitude);
                        products.setEntrance(entrance);
                        products.setSeat(seat);
                        products.setParking(parking_);
                        products.setToilet(toilet);
                        products.setElevator(elevator);
                        products.setCategory(category);
                        products.setPhone(phone);
                        products.setEct_info(etc_info);


                        arrayMyloc.add(products);
                        //lookupDataAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //전체 조회
    class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                doJSONParser(s);
            }
        }

    }

    void doJSONParser(String str) { //이것만 되면 된다. 해보자 시발 ㅜ ..
        try {

            if (str != null) {

                JSONObject order = new JSONObject(str);
                JSONArray index = order.getJSONArray("root");
                Bitmap getBlob;
                arrayLoc = new ArrayList<>();

                for (int i = 0; i < index.length(); i++) {
                        String place_name = "";
                        String place_address = "";
                        String place_img = "";
                        String user_name = "";
                        String upload_date = "";
                        String phone = "-";
                        String etc_info = "-";
                        String category = " ";
                        double latitude = 0.0;
                        double longitude = 0.0;
                        boolean kakao = false;
                        boolean google = false;
                        boolean entrance = false;
                        boolean elevator = false;
                        boolean parking_ = false;
                        boolean toilet = false;
                        boolean seat = false;

                        JSONObject tt = index.getJSONObject(i);

                        place_name = tt.getString("name");
                        place_address = tt.getString("address");
                        //이미지 변환
                        place_img = tt.getString("image");
                        Log.d("imgStr", tt.getString("image"));
                        byte[] encodeByte = Base64.decode(place_img, 0);
                        Log.e("byte", encodeByte.toString());
                        getBlob = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                        user_name = tt.getString("userName");
                        //날짜 시간 자름
                        String upload_date_ = tt.getString("uploadDate");
                        String[] upload_date_s = upload_date_.split(" ");
                        upload_date = upload_date_s[0];

                        int i_platform = tt.getInt("platform");
                        //1이면 모두 2면 카카오만 3이면 구글만
                        if (i_platform == 1) {
                            google = true;
                            kakao = true;
                        } else if (i_platform == 2) {
                            kakao = true;
                        } else {
                            google = true;
                        }
                        latitude = tt.getDouble("latitude");
                        longitude = tt.getDouble("longitude");
                        int i_category = tt.getInt("category");
                        switch (i_category) {
                            case 1:
                                category = "장애인용 공용 화장실";
                                break;
                            case 2:
                                category = "숙박업소";
                                break;
                            case 3:
                                category = "보건소";
                                break;
                            case 4:
                                category = "음식점";
                                break;
                            case 5:
                                category = "주차장";
                                break;
                            case 6:
                                category = "문화센터";
                                break;
                            case 7:
                                category = "교육센터";
                                break;
                            case 8:
                                category = "병원";
                                break;
                        }
                        phone = tt.getString("phone");
                        etc_info = tt.getString("etc_info");
                        int i_entrance = tt.getInt("entrance");
                        if (i_entrance == 1) {
                            entrance = true;
                        }
                        int i_seat = tt.getInt("seat");
                        if (i_seat == 1) {
                            seat = true;
                        }
                        Log.d("hah", String.valueOf(parking_));
                        int i_parking = tt.getInt("parking");
                        if (i_parking == 1) {
                            parking_ = true;
                        }
                        Log.d("hah2", String.valueOf(parking_));
                        int i_toilet = tt.getInt("toilet");
                        if (i_toilet == 1) {
                            toilet = true;
                        }
                        int i_elevator = tt.getInt("elevator");
                        if (i_elevator == 1) {
                            elevator = true;
                        }

                        LookupDataProducts products = new LookupDataProducts();

                        products.setImage(getBlob);
                        products.setPlace_name(place_name);
                        products.setPlace_address(place_address);
                        products.setUser_name(user_name);
                        products.setUpload_date(upload_date);
                        products.setKakao(kakao);
                        products.setGoogle(google);
                        products.setLatitude(latitude);
                        products.setLongitude(longitude);
                        products.setEntrance(entrance);
                        products.setSeat(seat);
                        products.setParking(parking_);
                        products.setToilet(toilet);
                        products.setElevator(elevator);
                        products.setCategory(category);
                        products.setPhone(phone);
                        products.setEct_info(etc_info);

                        arrayLoc.add(products);
                        //lookupDataAdapter.notifyDataSetChanged();
                    }
                }
            } catch(JSONException e){
                Log.d("JSON", "showResult : ", e);
            }
        }


    }







