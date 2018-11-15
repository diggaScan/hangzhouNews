package com.sunlandgroup.baseui.basetab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LSJ on 2017/6/9.
 * 用于tab页
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fmList;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fmList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fmList.get(arg0);
    }

    @Override
    public int getCount() {
        return fmList.size();
    }

    /*
     * 删除指定索引的viewpager内容
     * @param index
     */
    public void removeByIndex(int index) {
        fmList.remove(index);
        notifyDataSetChanged();
    }

    public void addFragment(int index, Fragment fragment) {
        fmList.add(index, fragment);
        notifyDataSetChanged();
    }

    //使adatper误认为空，强制刷新,作用等于setAdapter(null)再setAdapter(mPageAdapter)
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
