package com.example.luventsuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.luventsuser.Adapters.SlidePagerAdapterFragment;
import com.example.luventsuser.Fragments.BookmarkFragment;
import com.example.luventsuser.Fragments.HomeFragment;
import com.example.luventsuser.Fragments.ProfileFragment;
import com.example.luventsuser.R;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import java.util.ArrayList;

public class BottomNavigation extends AppCompatActivity {

     ViewPager viewPager;
     BubbleNavigationLinearView bubbleNavigationLinearView;
    int profileTab=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        ArrayList<Fragment> fragmentArrayList=new ArrayList<>();

        fragmentArrayList.add(new HomeFragment());
        fragmentArrayList.add(new BookmarkFragment());

        fragmentArrayList.add(new ProfileFragment());

        SlidePagerAdapterFragment pagerAdapterFragment=new SlidePagerAdapterFragment(fragmentArrayList, getSupportFragmentManager());
         bubbleNavigationLinearView=findViewById(R.id.bubble_nav_linear_id);
        bubbleNavigationLinearView.setTypeface(null);

        bubbleNavigationLinearView.setBadgeValue(0,null);
        bubbleNavigationLinearView.setBadgeValue(1,null);
        bubbleNavigationLinearView.setBadgeValue(2,null);


        Intent intent=getIntent();
        profileTab=intent.getIntExtra("tab",0);

        viewPager=findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapterFragment);

        switchTab();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                bubbleNavigationLinearView.setCurrentActiveItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position,true);
            }
        });

    }

    private void switchTab() {

        if (profileTab==3)
        {
            viewPager.setCurrentItem(2);
            bubbleNavigationLinearView.setCurrentActiveItem(2);
        }
        else if (profileTab==2){
            viewPager.setCurrentItem(1);
            bubbleNavigationLinearView.setCurrentActiveItem(1);
        }
        else if (profileTab==1){
            viewPager.setCurrentItem(0);
            bubbleNavigationLinearView.setCurrentActiveItem(0);
        }

    }
}
