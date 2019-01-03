package com.sunland.hangzhounews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunland.hangzhounews.MyApplication;
import com.sunland.hangzhounews.R;
import com.sunland.hangzhounews.V_config;
import com.sunland.hangzhounews.bean.BaseRequestBean;
import com.sunland.hangzhounews.utils.DialogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import cn.com.cybertech.pdk.OperationLog;

public abstract class Ac_base extends AppCompatActivity {
    public Toolbar toolbar;
    public LinearLayout container;
    public ImageView iv_nav_back;
    public TextView tv_title;
    public RelativeLayout toolbar_container;
    public DrawerLayout drawerLayout;
    public NavigationView navView;
    public MyApplication mApplication;
    public DialogUtils dialogUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_base);

        mApplication = (MyApplication) getApplication();
        toolbar = findViewById(R.id.news_toolbar);
        iv_nav_back = findViewById(R.id.nav_back);
        container = findViewById(R.id.main_container);
        tv_title = findViewById(R.id.toolbar_title);
        toolbar_container = findViewById(R.id.toolbar_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navView = findViewById(R.id.nav_view);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        iv_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialogUtils = DialogUtils.getInstance();
    }

    public void setContentLayout(int layout) {
        LayoutInflater.from(this).inflate(layout, container, true);
        ButterKnife.bind(this);
    }

    /**
     * 需在setContentLayout(int)方法前调用
     *
     * @param layout
     */
    public void setToolbarLayout(int layout) {
        toolbar_container.setVisibility(View.GONE);
        LayoutInflater.from(this).inflate(layout, toolbar, true);
    }

    public void setNavVisible(boolean isVisible) {
        if (isVisible) {
            iv_nav_back.setVisibility(View.VISIBLE);
        } else {
            iv_nav_back.setVisibility(View.INVISIBLE);
        }
    }

    public void setToolbarTitle(String title) {
        this.tv_title.setText(title);
    }

    public void assembleBasicRequest(BaseRequestBean requestBean) {
        requestBean.setYhdm(V_config.YHDM);
        requestBean.setImei(V_config.imei);
        requestBean.setImsi(V_config.imsi1);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pda_time = simpleDateFormat.format(date);
        requestBean.setPdaTime(pda_time);
        requestBean.setGpsX(V_config.gpsX);
        requestBean.setGpsY(V_config.gpsY);
    }

    public void saveLog(int operateType, int operationResult, String operateCondition) {
        try {
            OperationLog.saveLog(this
                    , getApplication().getPackageName()
                    , getApplication().getPackageName()
                    , operateType
                    , operationResult
                    , 1
                    , operateCondition);
        } catch (Exception e) {
            //未适配Fileprovider
            e.printStackTrace();
        }

    }

    public String appendString(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if (i != strings.length - 1) {
                sb.append("@");
            }
        }
        return sb.toString();
    }

    public void hop2Activity(Class<? extends Ac_base> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void hop2Activity(Class<? extends Ac_base> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }
}
