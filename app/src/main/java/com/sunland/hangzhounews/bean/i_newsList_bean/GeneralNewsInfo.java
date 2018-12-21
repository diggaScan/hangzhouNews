package com.sunland.hangzhounews.bean.i_newsList_bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "NEWS_LIST")
public class GeneralNewsInfo implements Comparable<GeneralNewsInfo> {
    @ColumnInfo
    public String addTime;
    @ColumnInfo
    public String content;
    @ColumnInfo
    public String editname;
    @ColumnInfo
    public String edittime;
    @ColumnInfo
    public String lmid;
    @PrimaryKey
    @ColumnInfo
    @NonNull
    public String newsid;
    @ColumnInfo
    public int readCount;
    @ColumnInfo
    public String title;
    @ColumnInfo
    public int lbid;

    public int getLbid() {
        return lbid;
    }

    public void setLbid(int lbid) {
        this.lbid = lbid;
    }


    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEditname() {
        return editname;
    }

    public void setEditname(String editname) {
        this.editname = editname;
    }

    public String getEdittime() {
        return edittime;
    }

    public void setEdittime(String edittime) {
        this.edittime = edittime;
    }

    public String getLmid() {
        return lmid;
    }

    public void setLmid(String lmid) {
        this.lmid = lmid;
    }

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(@NonNull String newsid) {
        this.newsid = newsid;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int compareTo(GeneralNewsInfo o) {
        return o.getNewsid().compareTo(this.newsid);
    }
}

