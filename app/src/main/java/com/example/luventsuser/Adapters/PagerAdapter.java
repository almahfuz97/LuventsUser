package com.example.luventsuser.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.luventsuser.Fragments.AboutFragment;
import com.example.luventsuser.Fragments.DiscussionFragment;

public class PagerAdapter  extends FragmentPagerAdapter {
    private int noOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.noOfTabs=behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AboutFragment();

            case 1:
                return new DiscussionFragment();
            default:
                return null;

        }


    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
