package com.sunland.hangzhounews.dbHelper.newsl_list;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.sunland.hangzhounews.bean.i_newsList_bean.GeneralNewsInfo;
import com.sunland.hangzhounews.dbHelper.news_collection.News;

import java.util.List;

@Dao
public interface NewsListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertList(List<GeneralNewsInfo> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(GeneralNewsInfo info);

    @Delete
    public void deleteNews(News news);

    @Query("SELECT * FROM NEWS_LIST WHERE lbid=:lbid ORDER BY addTime desc ")
    public List<GeneralNewsInfo> loadNewsBylm(int lbid);

    @Query("SELECT * FROM NEWS_LIST")
    public List<GeneralNewsInfo> loadAllNews();

    @Query("DELETE FROM NEWS_LIST")
    public void deleteAllRecords();

}
