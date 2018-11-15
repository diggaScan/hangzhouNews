package com.sunland.hangzhounews;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.hangzhounews.bean.BaseRequestBean;
import com.sunland.hangzhounews.bean.NewsCategory;
import com.sunland.hangzhounews.bean.NewsKindRequestBean;
import com.sunland.hangzhounews.bean.NewsKindResponseBean;
import com.sunland.hangzhounews.bean.TerritoryInfo;
import com.sunland.hangzhounews.bean.TerritoryRequestBean;
import com.sunland.hangzhounews.bean.TerritoryResponseBean;
import com.sunland.hangzhounews.config.ContentAdapter;
import com.sunlandgroup.Global;
import com.sunlandgroup.def.bean.result.ResultBase;
import com.sunlandgroup.network.OnRequestCallback;
import com.sunlandgroup.network.RequestManager;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class Ac_main extends CheckSelfPermissionActivity implements OnRequestCallback {

    @BindView(R.id.region_picker)
    public SpinButton sb_regions;
    @BindView(R.id.index_title)
    public TextView tv_title;
    @BindView(R.id.fragments_container)
    public ViewPager vp_frg_container;
    @BindView(R.id.tab_titles)
    public TabLayout tl_categories;

    private RequestManager mRequestManager;
    private String selected_dq_code;
    private List<String> dq_codes = new ArrayList<>();
    private List<Integer> category_codes = new ArrayList<>();
    private List<String> category_names = new ArrayList<>();
    private List<Fragment> frg_list;
    public ContentAdapter mContentAdapter;


    private int backPressed_num = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarLayout(R.layout.toolbar_layout);
        setContentLayout(R.layout.ac_main);
        mRequestManager = new RequestManager(this, this);
        frg_list = new ArrayList<>();
        queryYdjwData(DataModel.TERRITORY_LIST_REQNAME);
        initView();
    }

    public void queryYdjwData(String reqName) {
        mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, reqName, assembleRequestObj(reqName), 15000);
        mRequestManager.postRequest();
    }

    private BaseRequestBean assembleRequestObj(String reqName) {
        switch (reqName) {
            case DataModel.TERRITORY_LIST_REQNAME:
                TerritoryRequestBean requestBean = new TerritoryRequestBean();
                assembleBaseInfo(requestBean);
                return requestBean;
            case DataModel.NEWS_CATEGORY_LIST_REQNAME:
                NewsKindRequestBean newsKindRequestBean = new NewsKindRequestBean();
                assembleBaseInfo(newsKindRequestBean);
                newsKindRequestBean.setDqid(selected_dq_code);
                return newsKindRequestBean;
        }
        return null;
    }

    public void assembleBaseInfo(BaseRequestBean requestBean) {
        requestBean.setYhdm("test");
        requestBean.setImei(Global.imei);
        requestBean.setImsi(Global.imsi1);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pda_time = simpleDateFormat.format(date);
        requestBean.setPdaTime(pda_time);
    }

    private void initView() {

        tv_title.setText("公安资讯");
        sb_regions.setOnItemSelectedListener(new SpinButton.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                selected_dq_code = dq_codes.get(position);
                queryYdjwData(DataModel.NEWS_CATEGORY_LIST_REQNAME);
            }
        });

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.collect:
                        hop2Activity(Ac_personal_track.class);
                        break;
                    case R.id.setting:
                        // TODO: 2018/11/14/014
                        Toast.makeText(Ac_main.this, "建设中", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
                        // TODO: 2018/11/14/014
                        Toast.makeText(Ac_main.this, "建设中", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.attach:
                        hop2Activity(Ac_attach.class);
                        break;
                }
                return false;
            }
        });
    }

    public void initNewsCategories() {

        if (category_names.size() <= 5) {
            tl_categories.setTabMode(TabLayout.MODE_FIXED);
        }
        clearFragmentCache();
        frg_list.clear();
        for (int i = 0; i < category_names.size(); i++) {
            Frg_news_list frg = new Frg_news_list();
            frg.setQueryParams(selected_dq_code, category_codes.get(i));
            frg_list.add(frg);
        }
        mContentAdapter = new ContentAdapter(getSupportFragmentManager(), frg_list, category_names);
        vp_frg_container.setAdapter(mContentAdapter);
        vp_frg_container.setOffscreenPageLimit(10);
        tl_categories.setupWithViewPager(vp_frg_container);
    }

    private void clearFragmentCache() {
        try {
            if (mContentAdapter == null) {
                return;
            }
            int size = frg_list.size();

            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mCurTransaction = mFragmentManager.beginTransaction();
            Class<FragmentPagerAdapter> cls = FragmentPagerAdapter.class;
            Class<?>[] parameterTypes = {int.class, long.class};
            Method method = cls.getDeclaredMethod("makeFragmentName",
                    parameterTypes);
            method.setAccessible(true);
            for (int i = 0; i < size; i++) {
                String name = (String) method.invoke(this, vp_frg_container.getId(), i);
                Fragment fragment = mFragmentManager.findFragmentByTag(name);
                if (fragment != null) {
                    mCurTransaction.remove(fragment);
                }
            }
            mCurTransaction.commitNowAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnClick(R.id.burger)
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.burger:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }

    public RequestManager getmRequestManager() {
        return mRequestManager;
    }


    @Override
    public <T> void onRequestFinish(String reqId, String reqName, T bean) {
        switch (reqName) {
            case DataModel.TERRITORY_LIST_REQNAME:
                TerritoryResponseBean responseBean = (TerritoryResponseBean) bean;
                if (responseBean != null) {
                    if (responseBean.getCode().equals("0")) {
                        List<TerritoryInfo> list = responseBean.getTerritoryInfo();
                        if (list == null || list.isEmpty()) {
                            Toast.makeText(this, "地址列表返回为空", Toast.LENGTH_SHORT).show();
                        } else {
                            List<String> dateSet = new ArrayList<>();
                            for (TerritoryInfo info : list) {
                                dateSet.add(info.getDqmc());
                                dq_codes.add(info.getDqid());
                            }
                            sb_regions.setDataSet(dateSet);
                            sb_regions.setHeaderTitle("地区选择");
                            sb_regions.show();
                        }
                    } else {
                        Toast.makeText(this, "服务异常，无法获取数据", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(this, "数据接入错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case DataModel.NEWS_CATEGORY_LIST_REQNAME:
                NewsKindResponseBean newsKindResponseBean = (NewsKindResponseBean) bean;
                if (newsKindResponseBean != null) {
                    if (newsKindResponseBean.getCode().equals("0")) {
                        List<NewsCategory> list = newsKindResponseBean.getNewsCategory();
                        if (list == null || list.isEmpty()) {
                            Toast.makeText(this, "新闻类别列表返回为空", Toast.LENGTH_SHORT).show();
                            category_names.clear();
                            mContentAdapter.notifyDataSetChanged();
                            category_codes.clear();
                            initNewsCategories();
                        } else {
                            category_names.clear();
                            category_codes.clear();
                            for (NewsCategory info : list) {
                                category_codes.add(info.getLbid());
                                category_names.add(info.getLbmc());
                            }
                            initNewsCategories();
                        }
                    } else {
                        Toast.makeText(this, "服务异常，无法获取数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "数据接入错误", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public <T extends ResultBase> Class<?> getBeanClass(String reqId, String reqName) {
        switch (reqName) {
            case DataModel.TERRITORY_LIST_REQNAME:
                return TerritoryResponseBean.class;
            case DataModel.NEWS_CATEGORY_LIST_REQNAME:
                return NewsKindResponseBean.class;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (backPressed_num != 1) {
            backPressed_num++;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed_num--;
                }
            }, 2500);
            Toast.makeText(this, "再按一次，退出应用", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

    }
}
