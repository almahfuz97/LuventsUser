package com.example.luventsuser.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class SlidePagerAdapterFragment extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragmentArrayList;
    FragmentManager fragmentManager;

    public SlidePagerAdapterFragment(@NonNull ArrayList<Fragment> fragmentArrayList, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragmentArrayList = fragmentArrayList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
}