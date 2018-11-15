package com.sunland.hangzhounews.dbHelper;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.sunland.hangzhounews.dbHelper.browser_history.History;
import com.sunland.hangzhounews.dbHelper.browser_history.HistoryDao;
import com.sunland.hangzhounews.dbHelper.news_collection.News;
import com.sunland.hangzhounews.dbHelper.news_collection.NewsDAO;


@Database(entities = {News.class, History.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract NewsDAO newsDAO();

    public abstract HistoryDao getHistoryDao();
}
