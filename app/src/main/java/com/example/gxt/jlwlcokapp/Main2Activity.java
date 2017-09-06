package com.example.gxt.jlwlcokapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class Main2Activity extends AppCompatActivity {


    private List<Fragment> mFragments;
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;
    private BottomNavigationView bottomNavigationView;

    private android.support.v4.app.FragmentManager mFragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_door_state:
                    mViewPager.setCurrentItem(0, false);
                    return true;
                case R.id.navigation_record:
                    mViewPager.setCurrentItem(1, false);
                    return true;
                case R.id.navigation_user:
                    mViewPager.setCurrentItem(2, false);
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //获取login以及后台传过来的信息
        Intent intent = getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle = intent.getExtras();//.getExtras()得到intent所附带的额外数据
        String bname= bundle.getString("bname");
        String usr=bundle.getString("usr");
        String building=bundle.getString( "building" );

        Bundle bundle1 = new Bundle();
        bundle1.putString("DATA",bname);//这里的values就是我们要传的值
        bundle1.putString("DATA1",usr);
        bundle1.putString("DATA3",building);
        // 初始化fragments
        mFragments = new ArrayList<>();
        DoorStateFragment doorStateFragment = new DoorStateFragment();
        doorStateFragment.setArguments(bundle1);
        RecordFragment recordFragment = new RecordFragment();
        recordFragment.setArguments(bundle1);
        UserFragment userFragment = new UserFragment();
        mFragments.add(doorStateFragment);
        mFragments.add(recordFragment);
        mFragments.add(userFragment);
        // 初始化ViewPager
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFragmentManager = getSupportFragmentManager();
        mAdapter = new MyFragmentPagerAdapter(mFragmentManager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation2);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.selector_bottomnavigationview_text);
        bottomNavigationView.setItemTextColor(csl);
        bottomNavigationView.setItemIconTintList(csl);
        /**设置MenuItem默认选中项**/
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {


//        if ( != null)
//        {
//            transaction.hide();
//        }
//
//        if (userCenterFragment!= null)
//        {
//            transaction.hide(userCenterFragment);
//        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            switch (position) {
                case 0:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_door_state);
                    break;
                case 1:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_record);
                    break;
                case 2:
                    bottomNavigationView.setSelectedItemId(R.id.navigation_user);
                    break;
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}

