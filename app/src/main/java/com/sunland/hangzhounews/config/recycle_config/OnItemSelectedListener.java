package com.sunland.hangzhounews.config.recycle_config;

import com.sunland.hangzhounews.dbHelper.news_collection.News;

public interface OnItemSelectedListener{
    void onClick(int position,News news);
}