package com.sunland.hangzhounews.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConvertor {

    public static String Stamp2date(long timeStamp){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date=new Date(timeStamp);
        return simpleDateFormat.format(date);
    }
}
