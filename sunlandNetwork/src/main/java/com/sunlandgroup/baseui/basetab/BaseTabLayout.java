package com.sunlandgroup.baseui.basetab;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sunlandgroup.baseui.About;
import com.sunlandgroup.baseui.R;
import com.sunlandgroup.utils.DialogUtils;

public class BaseTabLayout extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private final int VIEWPAGER_OFFSCREEN_SIZE = 20;
    private ViewPager mViewPager;
    private ViewPagerAdapter mPageAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basetablayout);

        mTabLayout = (TabLayout) findViewById(R.id.tl_head);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //增加viewpager的预加载数
        mViewPager.setOffscreenPageLimit(VIEWPAGER_OFFSCREEN_SIZE);
        //设置Adapter
        mViewPager.setAdapter(mPageAdapter);
        //设置监听
        mViewPager.setOnPageChangeListener(this);
        setTabFixed(true);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = mTabLayout.getSelectedTabPosition();
                if (index >= 0) {
                    mViewPager.setCurrentItem(index, false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        findViewById(R.id.btn_add0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mTabLayout.getTabCount();
                String info = "违法信息" + index;
                About ab = new About();
                Bundle bd = new Bundle();
                bd.putString("info", info);
                ab.setArguments(bd);
                // addTab(info, ab);
                addTab(0, info, ab);
                //UtilsDialog.showQuestionMessage(BaseTabLayout.this, "标题", "询问", "确定", "取消", null, null);
            }
        });

        findViewById(R.id.btn_add1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mTabLayout.getTabCount();
                String info = "违法信息" + index;
                About ab = new About();
                Bundle bd = new Bundle();
                bd.putString("info", info);
                ab.setArguments(bd);
                addTab(1, info, ab);
//                String item[] = {"测试1", "测试2", "测试3", "测试4"};
//                UtilsDialog.showSingleChoiceItemsNoCancel(BaseTabLayout.this, "标题", item, 1, null);
            }
        });

        findViewById(R.id.btn_cur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    setCurrentTab(1);
                int cur = mTabLayout.getSelectedTabPosition();
                if (cur < 0)
                    return;

                if (mPageAdapter.getItem(cur) instanceof OnTabItemData) {
                    String info = ((OnTabItemData) mPageAdapter.getItem(cur)).getTabItemData().toString();
                    DialogUtils.showInfoMessage(BaseTabLayout.this, "提示", info, null);
                }
            }
        });

        findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTabLayout.getTabCount() > 0) {
                    int index = mTabLayout.getSelectedTabPosition();
                    if (index < 0)
                        return;

                    removeTabByIndex(1);
                }
                //UtilsDialog.showBlackProgressDlg(BaseTabLayout.this, "正在刷新");
            }
        });

        findViewById(R.id.btn_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mode = mTabLayout.getTabMode();
                setTabFixed(mode == 0 ? true : false);
            }
        });

        {
            String info = "基本信息";
            About ab = new About();
            Bundle bd = new Bundle();
            bd.putString("info", info);
            ab.setArguments(bd);
            addTab(info, ab);
        }
        {
            String info = "违法信息";
            About ab = new About();
            Bundle bd = new Bundle();
            bd.putString("info", info);
            ab.setArguments(bd);
            addTab(info, ab);
        }
        {
            String info = "车辆信息";
            About ab = new About();
            Bundle bd = new Bundle();
            bd.putString("info", info);
            ab.setArguments(bd);
            addTab(info, ab);
        }
//        {
//            String info = "强制措施";
//            About ab = new About();
//            Bundle bd = new Bundle();
//            bd.putString("info", info);
//            ab.setArguments(bd);
//            addTab(info, ab);
//        }
    }

    /*设置tab页头是否固定true：固定false：可滑动*/
    protected final void setTabFixed(boolean isFix) {
        mTabLayout.setTabMode(isFix ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);
    }

    /**
     * 根据标签页title删除指定tab页
     *
     * @param title
     */
    protected final void removeTabByText(String title) {
        int index = -1;
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            if (title.equals(mTabLayout.getTabAt(i).getText())) {
                index = i;
                break;
            }
        }
        if (index < 0)
            return;

        removeTabByIndex(index);
    }

    /**
     * 删除指定索引tab页
     *
     * @param index
     */
    protected final void removeTabByIndex(int index) {
        if (index >= mTabLayout.getTabCount() || index < 0)
            return;
        mTabLayout.removeTabAt(index);
        mPageAdapter.removeByIndex(index);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setCurrentTab(mTabLayout.getSelectedTabPosition());
            }
        }, 200);
    }

    /*根据标签名设置当前tab页面*/
    protected final void setCurrentTab(String title) {
        int index = -1;
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            if (title.equals(mTabLayout.getTabAt(i).getText())) {
                index = i;
                break;
            }
        }
        if (index < 0)
            return;
        setCurrentTab(index);
    }

    /*根据索引设置当前tab页面*/
    protected final void setCurrentTab(int index) {
        if (index >= mTabLayout.getTabCount() || index < 0)
            return;

        try {
            mTabLayout.getTabAt(index).select();
            mViewPager.setCurrentItem(index, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新建tab页面
     *
     * @param text     标签页名称
     * @param fragment 标签页内容
     * @return 标签页id
     */
    protected final void addTab(String text, Fragment fragment) {
        addTab(mPageAdapter.getCount(), text, fragment);
    }

    /**
     * 在指定位置插入tab页
     *
     * @param index    插入位置
     * @param text     标签页名称
     * @param fragment 标签页内容
     * @return 标签页id
     */
    protected final void addTab(int index, String text, Fragment fragment) {

        mTabLayout.addTab(mTabLayout.newTab().setText(text), index);//添加tab选项卡
        //调用ViewPagerAdapter中封装的add方法
        mPageAdapter.addFragment(index, fragment);

//        if(mIdList.size() == 1){
//            preSelect = currentSelect = 0;
//            selectedTabId = mIdList.get(0);
//        }

        final int currentSelect = mTabLayout.getSelectedTabPosition();
        //如果插入视图在当前视图之前，那么视图需要重新设置到当前所选视图需要自己重新设置
        if (index <= currentSelect) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentTab(currentSelect);
                }
            }, 200);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        if (mTabLayout != null && mTabLayout.getTabCount() >= arg0)
            mTabLayout.getTabAt(arg0).select();
    }
}
