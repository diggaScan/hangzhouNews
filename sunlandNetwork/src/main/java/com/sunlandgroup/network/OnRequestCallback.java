package com.sunlandgroup.network;

import com.sunlandgroup.def.bean.result.ResultBase;
import com.sunlandgroup.utils.JsonUtils;

/**
 * Created by LSJ on 2017/10/12.
 */

public abstract interface OnRequestCallback {
    public abstract <T> void onRequestFinish(String reqId, String reqName, T bean);

    public abstract <T extends ResultBase> Class<?> getBeanClass(String reqId, String reqName);
}
