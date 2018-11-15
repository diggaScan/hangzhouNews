package com.sunland.hangzhounews.bean;

import com.sunlandgroup.def.bean.result.ResultBase;

import java.util.List;

public class TerritoryResponseBean extends ResultBase {

    private List<TerritoryInfo> territoryInfo;

    public List<TerritoryInfo> getTerritoryInfo() {
        return territoryInfo;
    }

    public void setTerritoryInfo(List<TerritoryInfo> territoryInfo) {
        this.territoryInfo = territoryInfo;
    }
}
