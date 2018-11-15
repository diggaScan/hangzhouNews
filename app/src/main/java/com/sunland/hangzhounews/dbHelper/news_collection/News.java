package com.sunland.hangzhounews.dbHelper.news_collection;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "News_collection")
public class News {

    @ColumnInfo(name = "TIME_STAMP")
    public long timeStamp;

    @ColumnInfo(name = "title")
    public String title;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "newsId")
    public int newsId;

    @ColumnInfo(name = "lbid")
    public int lbid;

    @ColumnInfo(name = "dqid")
    public String dqid;

}
