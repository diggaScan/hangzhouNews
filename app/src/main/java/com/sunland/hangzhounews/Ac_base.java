package com.sunland.hangzhounews;

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

import butterknife.ButterKnife;

public abstract class Ac_base extends AppCompatActivity {
    public Toolbar toolbar;
    public LinearLayout container;
    public ImageView iv_nav_back;
    public TextView tv_title;
    public RelativeLayout toolbar_container;
    public DrawerLayout drawerLayout;
    public NavigationView navView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_base);
        toolbar = findViewById(R.id.news_toolbar);
        iv_nav_back = findViewById(R.id.nav_back);
        container = findViewById(R.id.main_container);
        tv_title = findViewById(R.id.toolbar_title);
        toolbar_container = findViewById(R.id.toolbar_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navView=findViewById(R.id.nav_view);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        iv_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void setContentLayout(int layout) {
        View view = LayoutInflater.from(this).inflate(layout, container, true);
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
