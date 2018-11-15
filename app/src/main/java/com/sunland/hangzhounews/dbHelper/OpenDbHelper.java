package com.sunland.hangzhounews.dbHelper;

import android.arch.persistence.room.Room;
import android.content.Context;

public class OpenDbHelper {
    public static final String DB_FLAG = "Mdb";


    private Context mContext;

    private static MyDatabase mDb;


    public static void createDb(Context context) {
        mDb = Room.databaseBuilder(context, MyDatabase.class, DB_FLAG).build();
    }

    public static MyDatabase getDb() {
        return mDb;
    }

    public static void closeDb() {
        mDb.close();
    }


}
