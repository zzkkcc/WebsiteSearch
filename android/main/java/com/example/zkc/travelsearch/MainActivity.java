package com.example.zkc.travelsearch;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zkc.travelsearch.fragment.FavoriteFragment;
import com.example.zkc.travelsearch.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //TabLayout
    private TabLayout tabLayout;
    //ViewPager
    private ViewPager viewPager;
    //Title
    private List<String> title;
    //Fragment
    private List<Fragment> fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //去阴影
        getSupportActionBar().setElevation(0f);
        //设置标题
        getSupportActionBar().setTitle("Place Search");

        initData();
        initView();
    }
    private void initData(){
        title = new ArrayList<>();
        title.add("Search");
        title.add("Favorite");

        fragment = new ArrayList<>();
        fragment.add(new SearchFragment());
        fragment.add(new FavoriteFragment());
    }
    private void initView(){
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //预加载
        viewPager.setOffscreenPageLimit(fragment.size());
        //设置适配器
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragment.get(position);
            }

            @Override
            public int getCount() {
                return fragment.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return title.get(position);
            }

        });
        //绑定
        tabLayout.setupWithViewPager(viewPager);
    }
}
