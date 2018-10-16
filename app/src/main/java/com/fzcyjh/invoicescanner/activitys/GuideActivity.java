package com.fzcyjh.invoicescanner.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.fzcyjh.invoicescanner.MainActivity;
import com.fzcyjh.invoicescanner.R;
import com.fzcyjh.invoicescanner.manager.ConfigManager;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements View.OnClickListener {
    private ViewPager   mViewPager;
    private List<View> mViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        initData();
        initView();
    }

    private  void initData(){
        mViewList = new ArrayList<View>(2);
        View view1 = LayoutInflater.from(this).inflate(R.layout.pageview_guide1, null);
        mViewList.add(view1);

        View view2 = LayoutInflater.from(this).inflate(R.layout.pageview_guide2, null);
        view2.findViewById(R.id.btn_guide2_iknow).setOnClickListener(this);
        view2.findViewById(R.id.btn_guide2_nevertip).setOnClickListener(this);
        mViewList.add(view2);
    }

    private void initView(){
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mViewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mViewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mViewList.get(position));
                return mViewList.get(position);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private  void enterMainActivity(){
        Intent mainIntent = new Intent(this,
                MainActivity.class);
        this.startActivity(mainIntent);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_guide2_iknow:{
                enterMainActivity();
            }
            break;
            case R.id.btn_guide2_nevertip:{
                ConfigManager.setBoolean(ConfigManager.CONF_GUIDE_NEVER_TIP, true);
                enterMainActivity();
            }
            break;
        }
    }
}
