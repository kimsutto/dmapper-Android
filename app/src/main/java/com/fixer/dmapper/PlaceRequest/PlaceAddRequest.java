package com.fixer.dmapper.PlaceRequest;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
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
import com.fixer.dmapper.BottomBarFragment.RequestHttpURLConnection;
import com.fixer.dmapper.BottomBarFragment.googlemaptab;
import com.fixer.dmapper.BottomBarFragment.kakaomaptab;
import com.fixer.dmapper.ImageViewPager.ImageViewPager;
import com.fixer.dmapper.ImageViewPager.ImageViewPagerAdapter;
import com.fixer.dmapper.MainActivity;
import com.fixer.dmapper.R;

import org.w3c.dom.Text;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class PlaceAddRequest extends AppCompatActivity{

    //소켓 관련 변수
    Socket socket;
    private String ip = "52.79.214.170"; // IP 주소
    private int port = 8888; // PORT번호'
    private Handler socketHandler;

    //이미지 따로 보냄 소켓으로 길이땜시
    Bitmap bm = null;

    /* 이미지 여러개.. 나중에 시간남으면 수정하세요..
    public static int imageitemposition = 0;
    public List<String> imagePathList;
    public ImageViewPagerAdapter fragmentAdapter;
    final int PICK_IMAGE_MULTIPLE = 100;
    String imagePath;
    ArrayList<Uri> mArrayUri;
    */

    private static final int PICK_FROM_ALBUM = 1;


    Button submit_btn;

    TextInputLayout textInputLayout,textInputLayout2,textInputLayout3,textInputLayout4;
    TextInputEditText place_name_et,address_name_et,phonenumber_et,etcinfo_et;

    CheckBox kakao_map_check, google_map_check, entrance_check, seat_check, parking_check ,restroom_check, elevator_check;

    private Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    //ViewPager viewPager;
    ImageView image_upload_btn;
    ImageView image_View;

    InputMethodManager imm;

    kakaomaptab kakaomaptab;
    googlemaptab googlemaptab;

    String place_name_st,address_name_st,category_name_st,phonenumber_st,etcinfo_st;
    Boolean kakao_bool,google_bool,entrance_bool,seat_bool,parking_bool,restroom_bool,elevator_bool;

    String user_id;
    String user_name;
    String place_type = "1"; //추가는 1

    public double latitude;
    public double longitude;
    String latitude_st;
    String longitude_st;

    boolean map_platform_check_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place_add_request);

        init_variable();
        init_spinner_list();
        init_BindValue();


        image_upload_btn = findViewById(R.id.image_upload_button);
        image_View = (ImageView) findViewById(R.id.image_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getValue();


        etcinfo_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 50){
                    textInputLayout4.setErrorEnabled(true);
                    textInputLayout4.setError("최대 허용 길이를 초과하였습니다");
                }else{
                    textInputLayout4.setErrorEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        image_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doTakeMultiAlbumAction();
                albumAction();
            }
        });


         /*
        if(place_name_st.trim().length() == 0 || address_name_st.trim().length() == 0 || category_name_st.trim().length() == 0 || google_bool == false || kakao_bool == false){
            submit_btn.setBackgroundColor(Color.GRAY);
            submit_btn.setEnabled(false);
            Toast.makeText(this, "여기", Toast.LENGTH_SHORT).show();
        }else{
            submit_btn.setBackgroundColor(Color.rgb(130,159,217));
            submit_btn.setEnabled(true);
        }*/

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i;
                if(place_name_et.getText().toString().trim().length() > 0 && address_name_et.getText().toString().trim().length() > 0 && (google_map_check.isChecked() || kakao_map_check.isChecked())){
                    for(i=0; i<MainActivity.arrayLoc.size(); i++){
                        if(MainActivity.arrayLoc.get(i).getPlace_name().equals(place_name_et.getText().toString())) {
                            Toast.makeText(PlaceAddRequest.this, "동일한 장소가 있습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if(i== MainActivity.arrayLoc.size()) {
                        Log.d("zz", String.valueOf(i));
                        Toast.makeText(PlaceAddRequest.this, "완료", Toast.LENGTH_SHORT).show();
                        //이미지는 php로 디비로
                        BitMapToString(bm);
                        //추가 정보 소켓서버로
                        socketHandler = new Handler();
                        ConnectThread th = new ConnectThread();
                        th.start();
                        finish(); //액티비티 닫음
                    }
                }else{
                    Toast.makeText(PlaceAddRequest.this, "필수정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
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
                        "," + longitude_st;

                Log.i("@@", "pl" + place_name_st + "ad" + address_name_st + "ca" + category_name_st + "et" + etcinfo_st +
                        "ka" + kakao_bool + "go" + google_bool + "en" + entrance_bool + "se" + seat_bool + "pa" + parking_bool + "re" + restroom_bool + "el" + elevator_bool);


                //데이터 전송
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(sndMsg);

                LookupDataProducts a = new LookupDataProducts(place_name_st,address_name_st,bm, user_name, null, null,latitude,longitude,category_name_st,phonenumber_st,etcinfo_st,entrance_bool,elevator_bool,parking_bool,restroom_bool,seat_bool,kakao_bool,google_bool);
                MainActivity.arrayLoc.add(a);
                MainActivity.arrayMyloc.add(a);

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void init_focus(){
        address_name_et.clearFocus();
        place_name_et.clearFocus();
        imm.hideSoftInputFromWindow(place_name_et.getWindowToken(),0);
        imm.hideSoftInputFromWindow(address_name_et.getWindowToken(),0);
    }

    public void init_spinner_list(){
        arrayList = new ArrayList<>();
        arrayList.add("장애인용 화장실");
        arrayList.add("숙박업소");
        arrayList.add("보건소");
        arrayList.add("음식점");
        arrayList.add("주차장");
        arrayList.add("문화센터");
        arrayList.add("교육센터");
        arrayList.add("병원");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner = (Spinner)findViewById(R.id.category_spinner);
        spinner.setAdapter(arrayAdapter);
    }

    public void init_variable(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(Html.fromHtml("<font color='#746E66'>"+"누락된 장소 추가"+"</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        submit_btn = (Button)findViewById(R.id.place_add_submit_btn);

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

        //Toast.makeText(getApplicationContext(), ""+MainActivity.Map_foreground_selector_kakao, Toast.LENGTH_SHORT).show();

        if(MainActivity.Map_foreground_selector_kakao == true || MainActivity.Map_foreground_selector_google == false){
            address_name_et.setText(kakaomaptab.ReverseGeoCodeValue);
        }else if(MainActivity.Map_foreground_selector_google == true || MainActivity.Map_foreground_selector_kakao == false){
            address_name_et.setText(googlemaptab.ReverseGeoCodeValue);
        }
        kakao_map_check.setChecked(false);
        google_map_check.setChecked(false);
        entrance_check.setChecked(false);
        seat_check.setChecked(false);
        parking_check.setChecked(false);
        restroom_check.setChecked(false);
        elevator_check.setChecked(false);
    }

    private void getValue(){

        place_name_st = place_name_et.getText().toString();
        address_name_st = address_name_et.getText().toString();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),arrayList.get(i)+"가 선택되었습니다.", Toast.LENGTH_SHORT).show();
                category_name_st = arrayList.get(i);
                init_focus();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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

        latitude = LatLngCarrier.latitude;
        longitude = LatLngCarrier.longitude;
        latitude_st = String.valueOf(latitude);
        longitude_st = String.valueOf(longitude);
        //Toast.makeText(this, "latitude_st : "+latitude_st + "longitude_st : "+longitude_st, Toast.LENGTH_SHORT).show();

    }

    public void albumAction() { //사진 선택 하나만으로 바꿈
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE); // 갤러리 진입
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            if (resultCode == RESULT_OK) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //bm = resize(bm);
                    //image_View.setImageBitmap(bm);
                    System.out.println();
                    Glide.with(this)
                            .asBitmap()
                            .load(bm)
                            .override(300,200)
                            .centerCrop()
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    image_View.setImageBitmap(resource);
                                    bm = resource;
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
    //Bitmap 줄이기 그래야 넘어감
    /*
    private Bitmap resize(Bitmap bm){
        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=800)
            bm = Bitmap.createScaledBitmap(bm, 400, 240, true);
        else if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
    }*/


    //이미지 BLOB형식 저장위해 str로 변경하고 디비로 보냄
    public void BitMapToString(Bitmap bitmap) {
        String ii=" ";
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);    //bitmap compress
            byte[] arr = baos.toByteArray();
            String image = Base64.encodeToString(arr, Base64.DEFAULT);

            try {
                ii = URLEncoder.encode(image, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            String IP_ADDRESS = "52.79.214.170";
            // temp = "&image=" + URLEncoder.encode(image, "utf-8");
            //temp = "&image="+image;
            InsertData task = new InsertData();
            task.execute("http://" + IP_ADDRESS + "/add_img2.php", ii);

        } catch (Exception e) {
            Log.e("exception", e.toString());
            }
        }


    static class InsertData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... params) {

            String image = (String) params[1];
            String serverURL = (String) params[0];
            String postParameters = "image=" + image;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("POST", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d("Error", "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }

        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}

    /* 이건 multialbumaction

    public void doTakeMultiAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK  && data != null){

            imagePathList = new ArrayList<>();
            if(data.getClipData() != null){
                ClipData mClipData = data.getClipData();
                mArrayUri = new ArrayList<Uri>();
                for (int i=0; i<mClipData.getItemCount(); i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    //getImageUri(imageUri);
                    getImageFilePath(imageUri);//2. 절대경로 구함 쓸데가 있겠지?
                }
            }
            else if(data.getData() != null){
                Uri imgUri = data.getData();
                getImageFilePath(imgUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    public void getImageFilePath(Uri uri) {

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagePathList.add(imagePath);
            cursor.close();

            viewPager = (ViewPager) findViewById(R.id.image_viewpager);
            fragmentAdapter = new ImageViewPagerAdapter(getSupportFragmentManager());
            // ViewPager와  FragmentAdapter 연결
            viewPager.setAdapter(fragmentAdapter);

            viewPager.setClipToPadding(false);
            int dpValue = 16;
            float d = getResources().getDisplayMetrics().density;
            int margin = (int) (dpValue * d);
            viewPager.setPadding(margin, 0, margin, 0);
            viewPager.setPageMargin(margin / 2);

            Log.i("image 가져옴",""+imagePathList);
            // FragmentAdapter에 Fragment 추가, Image 개수만큼 추가
            for (int i = 0; i < imagePathList.size(); i++) {
                ImageViewPager imageFragment = new ImageViewPager();
                Bundle bundle = new Bundle();
                bundle.putString("imgRes", imagePathList.get(i));
                imageFragment.setArguments(bundle);
                fragmentAdapter.addItem(imageFragment);
            }
            fragmentAdapter.notifyDataSetChanged();
        }
        getItemposition(viewPager);

    }

    public void getItemposition(final ViewPager viewPager){

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                imageitemposition = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
    */
