package com.sunland.hangzhounews.bean.i_newsList_bean;

import com.sunlandgroup.def.bean.result.ResultBase;

import java.util.List;

public class NewsListResponseBean extends ResultBase {

    private List<GeneralNewsInfo> generalNewsInfo;
    private int totalCount;
    private int totalPage;

    public void setGeneralNewsInfo(List<GeneralNewsInfo> generalNewsInfo) {
        this.generalNewsInfo = generalNewsInfo;
    }

    public List<GeneralNewsInfo> getGeneralNewsInfo() {
        return generalNewsInfo;
    }


    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalPage() {
        return totalPage;
    }


}
