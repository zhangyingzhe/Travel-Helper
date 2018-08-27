package com.example.zyz.hw9android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.zyz.hw9android.MESSAGE";
    private final String url = "http://csci571yingzhez-env.us-east-2.elasticbeanstalk.com/";
    String TAG="MainActivity";
    String detail_url = "http://10.120.73.207:9004/place_detail?placeid=ChIJ7aVxnOTHwoARxKIntFtakKo";
    String nearby_url = "http://csci571yingzhez-env.us-east-2.elasticbeanstalk.com/nearby_search?distance=16093.44&category=default&keyword=usc&location=34.0266,-118.2831";

    Toolbar toolbar;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    List<String> titles;
    List<Fragment> fragments;
    private int[] tabIcons={
            R.drawable.search,
            R.drawable.heart_fill_white
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }
    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("Places Search");

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);

        fragments = new ArrayList<>();
        fragments.add(SearchFragment.newInstance());
        fragments.add(FavoriteFragment.newInstance());

        titles = new ArrayList<>();
        titles.add("SEARCH");
        titles.add("FAVORITES");

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG,"onTabSelect" + tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

        AppSectionPagerAdapter adapter = new AppSectionPagerAdapter(getSupportFragmentManager(),fragments,titles);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onpageselect" + position);
                if(position == 1) {
                    FavoriteFragment frag = (FavoriteFragment) fragments.get(position);
                    frag.changeData();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupTabIcons();


    }

    private void setupTabIcons(){
        mTabLayout.getTabAt(0).setCustomView(getTabView(0));
        mTabLayout.getTabAt(1).setCustomView(getTabView(1));

    }
    public View getTabView(int position) {
        View view =
                LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(titles.get(position));
        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
        img_title.setImageResource(tabIcons[position]);
        return view;
    }

}
