package com.sunland.hangzhounews.bean.i_territory_bean;

import com.sunlandgroup.def.bean.result.ResultBase;

import java.util.List;

public class TerritoryResponseBean extends ResultBase {

    private String depcode;
    private List<TerritoryInfo> territoryInfo;

    public List<TerritoryInfo> getTerritoryInfo() {
        return territoryInfo;
    }

    public void setTerritoryInfo(List<TerritoryInfo> territoryInfo) {
        this.territoryInfo = territoryInfo;
    }

    public String getDepcode() {
        return depcode;
    }

    public void setDepcode(String depcode) {
        this.depcode = depcode;
    }
}
