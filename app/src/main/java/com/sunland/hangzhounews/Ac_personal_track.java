package com.sunland.hangzhounews;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sunland.hangzhounews.config.Rv_Item_decoration;
import com.sunland.hangzhounews.config.recycle_config.OnItemSelectedListener;
import com.sunland.hangzhounews.config.recycle_config.TrackRecycAdapter;
import com.sunland.hangzhounews.dbHelper.MyDatabase;
import com.sunland.hangzhounews.dbHelper.OpenDbHelper;
import com.sunland.hangzhounews.dbHelper.news_collection.News;

import java.util.Arrays;

import butterknife.BindView;

public class Ac_personal_track extends Ac_base {

    @BindView(R.id.recycler_view)
    public RecyclerView rv_collection;

    private TrackRecycAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.ac_personal_track);
        setToolbarTitle("收藏夹");
        setNavVisible(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showCollection();
    }

    private void showCollection() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                News[] news_table = (News[]) msg.obj;

                initNewsCollect(news_table);

            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                OpenDbHelper.createDb(Ac_personal_track.this);
                MyDatabase db = OpenDbHelper.getDb();
                Message msg = handler.obtainMessage();
                News[] news;
                try {
                    news = db.newsDAO().loadAllTitlesDesc();
                } catch (NullPointerException e) {
                    return;
                }
                msg.obj = news;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void initNewsCollect(final News[] news_table) {
        TrackRecycAdapter trackRecycAdapter = new TrackRecycAdapter(Ac_personal_track.this, Arrays.asList(news_table));
        mAdapter = trackRecycAdapter;
        trackRecycAdapter.setOnItemSelectedItem(new OnItemSelectedListener() {
            @Override
            public void onClick(int position, News news) {
                Bundle bundle = new Bundle();
                bundle.putString("dqid", news.dqid);
                bundle.putInt("lbid", news.lbid);
                bundle.putInt("newsId", news.newsId);
                hop2Activity(Ac_news_detail.class, bundle);
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(Ac_personal_track.this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_collection.setLayoutManager(lm);
        rv_collection.setAdapter(trackRecycAdapter);
        rv_collection.addItemDecoration(new Rv_Item_decoration(Ac_personal_track.this));
    }

}
