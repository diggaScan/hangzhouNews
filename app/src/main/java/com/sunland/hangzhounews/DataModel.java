package com.sunland.hangzhounews;

public class DataModel {

    public final static String TERRITORY_LIST_REQNAME = "queryTerritoryList";

    public final static String NEWS_CATEGORY_LIST_REQNAME = "queryCategoryList";

    public final static String NEWS_LIST_REQNAME = "queryNewsList";

    public final static String NEWS_DETAIL_REQNAME = "queryNewsDetail";

    public final static String ATTACH_FILE_DIR = "hzydjwAttachment";

    public static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".apk", "application/vnd.android.package-archive"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".pdf", "application/pdf"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".txt", "text/plain"},
            {".wps", "application/vnd.ms-works"},
            {".png", "image/png"}
    };
}
