package com.fixer.dmapper.Tutorial;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.fixer.dmapper.MainActivity;
import com.rd.PageIndicatorView;
import com.fixer.dmapper.LoginActivity;
import com.fixer.dmapper.MainActivity;
import com.fixer.dmapper.R;
import com.fixer.dmapper.Tutorial.page1;
import com.fixer.dmapper.Tutorial.page2;
import com.fixer.dmapper.Tutorial.page3;
import com.fixer.dmapper.Tutorial.page4;
import com.fixer.dmapper.Tutorial.page5;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

public class TutorialActivity extends AppCompatActivity {

    Button skipbutton;
    PageIndicatorView pageIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        pageIndicatorView = (PageIndicatorView)findViewById(R.id.tutorial_activity_page_indicator_view);
        skipbutton = (Button)findViewById(R.id.skipbutton);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.hide();
        ViewPager pager = findViewById(R.id.pager);
        //캐싱을 해놓을 프래그먼트 개수
        pager.setOffscreenPageLimit(5);

        //getSupportFragmentManager로 프래그먼트 참조가능
        TPagerAdapter adapter = new TPagerAdapter(getSupportFragmentManager());

        page1 fragment1 = new page1();
        adapter.addItem(fragment1);

        page2 fragment2 = new page2();
        adapter.addItem(fragment2);

        page3 fragment3 = new page3();
        adapter.addItem(fragment3);

        page4 fragment4 = new page4();
        adapter.addItem(fragment4);

        page5 fragment5 = new page5();
        adapter.addItem(fragment5);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pageIndicatorView.setSelection(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pageIndicatorView.setSelection(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        pager.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TutorialActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //어댑터 안에서 각각의 아이템을 데이터로서 관리한다
    class TPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public TPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item){
            items.add(item);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

}