package com.sunland.hangzhounews.dbHelper.browser_history;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(History history);

    @Delete
    public void delete(History history);

    @Query("SELECT * FROM BROWSER_HISTORY")
    public History[] loadAllHistory() throws NullPointerException;

    @Query("SELECT * FROM BROWSER_HISTORY ORDER BY TIME_STAMP desc")
    public History[] loadAllHistoryDesc();


}
