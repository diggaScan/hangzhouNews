package com.sunland.hangzhounews.dbHelper.news_collection;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(News news);

    @Delete
    public void deleteNews(News news);

    @Query("SELECT * FROM News_collection")
    public News[] loadAllNews();

    @Query("SELECT title FROM News_collection  ")
    public List<String> loadALlTitles();

    @Query("SELECT newsId FROM NEWS_COLLECTION ")
    public List<Integer> loadAllNewsId();

    @Query("SELECT * FROM News_collection ORDER BY TIME_STAMP desc")
    public News[] loadAllTitlesDesc();
}
