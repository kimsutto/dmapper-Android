package com.fixer.dmapper.ImageViewPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ImageViewPagerAdapter extends FragmentPagerAdapter {
    // ViewPager에 들어갈 Fragment들을 담을 리스트
    public ArrayList<Fragment> fragments = new ArrayList<>();

    // 필수 생성자
    public ImageViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public void removeitem(int position){
        fragments.remove(position);
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
    // List에 Fragment를 담을 함수
    public void addItem(Fragment fragment) {
        fragments.add(fragment);
    }
}