package com.sunland.hangzhounews.config;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sunland.hangzhounews.Frg_news_list;

import java.util.List;

public class ContentAdapter extends FragmentPagerAdapter {

    private List<Fragment> dataSet;

    public ContentAdapter(FragmentManager fm, List<Fragment> dataSet) {
        super(fm);
        this.dataSet = dataSet;
    }

    @Override
    public Fragment getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((Frg_news_list) dataSet.get(position)).getCategory_name();
    }


}
