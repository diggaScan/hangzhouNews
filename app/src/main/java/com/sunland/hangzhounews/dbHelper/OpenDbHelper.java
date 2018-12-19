package com.sunland.hangzhounews.dbHelper;

import android.arch.persistence.room.Room;
import android.content.Context;

public class OpenDbHelper {
    public static final String DB_FLAG = "Mdb";

    private Context mContext;

    private static MyDatabase mDb;

    public static MyDatabase getDb(Context context) {
        if (mDb == null) {
            synchronized (OpenDbHelper.class) {
                if (mDb == null) {
                    mDb = Room.databaseBuilder(context, MyDatabase.class, DB_FLAG).build();
                }
            }
        }
        return mDb;
    }

    public static void closeDb() {
        mDb.close();
    }


}
