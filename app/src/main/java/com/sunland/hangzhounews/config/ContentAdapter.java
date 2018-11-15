package com.sunland.hangzhounews.config;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ContentAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;
    private List<Fragment> dataSet;
    private List<String> tabNames;


    public ContentAdapter(FragmentManager fm, List<Fragment> list, List<String> tabNames) {
        super(fm);
        this.fm = fm;
        this.dataSet = list;
        this.tabNames = tabNames;
    }

    @Override
    public Fragment getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return tabNames.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position);
    }


}
