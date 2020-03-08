package com.fixer.dmapper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.hide();
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000); //3초
    }

    private class splashhandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), LoginActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        //초반 스플래쉬에서 넘어갈때 뒤로가기 버튼 X
    }
}