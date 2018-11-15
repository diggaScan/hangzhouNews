package com.sunland.hangzhounews.dbHelper.browser_history;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "BROWSER_HISTORY")
public class History {
    @ColumnInfo(name="TITLE")
    public String title;

    @PrimaryKey
    @ColumnInfo(name = "URL")
    @NonNull
    public String url;

    @ColumnInfo(name = "TIME_STAMP")
    public long timeStamp;
}
