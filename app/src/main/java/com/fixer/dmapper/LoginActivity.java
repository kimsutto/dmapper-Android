package com.fixer.dmapper;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fixer.dmapper.Tutorial.TutorialActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    //권한 관련 변수
    private static final int MY_PERMISSIONS_REQUEST_MULTI =1 ;
    ArrayList<String> permissions = new ArrayList<String>();

    //회원 DB
    private String user_name;
    private String user_email = " ";
    private String user_id;

    //카카오톡 간편 로그인 관련 변수
    private com.kakao.usermgmt.LoginButton kakaologinButton;//kakaoAPI버튼
    private Button fakebuttonkakao;//커스텀한 가짜 kakao버튼
    private SessionCallback callback;


    //구글 간편 로그인 관련 변수(Firebase 사용)
    SignInButton googleloginButton;
    private Button fakebuttongoogle;
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    //공지
    private TextView service, privateS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.hide();

        // 앱 최초 실행시에 튜토리얼 액티비티로
        SharedPreferences pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
        boolean checkFirst = pref.getBoolean("checkFirst", false);
        if(checkFirst==false){
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("checkFirst",true);
            editor.commit();

            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);

        }else{
        }
        //권한 창
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissions.size() > 0) {
            String[] reqPermissionArray = new String[permissions.size()];
            reqPermissionArray = permissions.toArray(reqPermissionArray);
            ActivityCompat.requestPermissions(this, reqPermissionArray, MY_PERMISSIONS_REQUEST_MULTI);
        }

        if(Session.getCurrentSession().isOpened()) {
            ProgressDialog asyncDialog = new ProgressDialog(this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로그인 중 입니다..");
            // show dialog
            asyncDialog.show();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                user_name = user.getDisplayName();
                user_email = user.getEmail();
                user_id = user.getUid();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                infoPutextra(intent,user_name,user_email,user_id);
            } else {
                requestMe();
            }
        }

        //공지 밑줄 효과
        service = (TextView) findViewById(R.id.service_text);
        privateS = (TextView) findViewById(R.id.service_private);

        SpannableString content = new SpannableString("서비스 이용약관");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        service.setText(content);

        SpannableString content2 = new SpannableString("개인정보 처리방침,");
        content2.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        privateS.setText(content2);

        /**카카오톡 관련 변수 초기화**/
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        fakebuttonkakao = (Button)findViewById(R.id.fake_kakao_login_button);
        fakebuttonkakao.setOnClickListener(this);
        kakaologinButton = (com.kakao.usermgmt.LoginButton) findViewById(R.id.kakao_login_button);

        /**구글 관련 변수 초기화**/
        fakebuttongoogle = (Button)findViewById(R.id.fake_google_login_button);
        fakebuttongoogle.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        googleloginButton = findViewById(R.id.Google_Login_button);
    }

    //권한
    @Override
    public void onRequestPermissionsResult(int requestCode, @afu.org.checkerframework.checker.nullness.qual.NonNull String[] permissions, @afu.org.checkerframework.checker.nullness.qual.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "현재 위치 접근 권한 승인함", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "현재 위치 접근 권한 거부함", Toast.LENGTH_SHORT).show();
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "사진 접근 권한 승인함", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "사진 접근 권한 거부함", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //공지 이동
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.putExtra("title","서비스 이용약관");
                startActivity(intent);
            }
        });
        privateS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                intent.putExtra("title","개인정보 처리방침");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fake_kakao_login_button:
                kakaologinButton.performClick();
                break;
            case R.id.fake_google_login_button:
                googleloginButton.performClick();
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
                break;
        }
    }

    //인터넷 연결상태 확인
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Toast.makeText(this, "들어옴", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
            }
        }

        /**카카오 관련**/

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "구글 로그인 인증 성공", Toast.LENGTH_SHORT).show();
                            //현 로그인 유저 정보 가져옴
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                user_name = user.getDisplayName();
                                user_email = user.getEmail();
                                user_id = user.getUid();
                            }
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            infoPutextra(intent,user_name,user_email,user_id);
                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /** 카카오톡 간편로그인 콜백 메서드 **/
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태. 일반적으로 로그인 후의 다음 activity로 이동한다.
            if(Session.getCurrentSession().isOpened()){ // 한 번더 세션을 체크해주었습니다.
                requestMe();
            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
    private void requestLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e("onFailure", errorResult + "");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("onSessionClosed",errorResult + "");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.e("onSuccess",userProfile.toString());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                user_name = userProfile.getNickname();
                user_id = String.valueOf(userProfile.getId());
                user_email = userProfile.getEmail();
                infoPutextra(intent,user_name,user_email,user_id);
            }

            @Override
            public void onNotSignedUp() {
                Log.e("onNotSignedUp","onNotSignedUp");
            }
        });
    }
    //정보 main에 전달
    public void infoPutextra(Intent intent , String name, String email, String id){
        intent.putExtra("myname",name);
        intent.putExtra("myemail",email);
        intent.putExtra("myid",id);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

}