package com.sunland.hangzhounews.utils;

import com.sunland.hangzhounews.DataModel;

public class FileUtils {
    /**
     * 根据文件后缀名获得对应的MIME类型。
     */
    public static String getMIMEType(String filename) {
        String type = filename.substring(filename.lastIndexOf("."), filename.length());
        if (type.isEmpty())
            return "*/*";
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < DataModel.MIME_MapTable.length; i++) {
            if (type.equals(DataModel.MIME_MapTable[i][0]))
                type = DataModel.MIME_MapTable[i][1];
        }
        return type;
    }
}
