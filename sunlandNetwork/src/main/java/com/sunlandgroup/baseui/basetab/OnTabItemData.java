package com.sunlandgroup.baseui.basetab;


import java.util.Objects;

/**
 * Created by LSJ on 2017/6/9.
 * 用于主activity中获取tab下各fragment的接口
 */

public interface OnTabItemData<T> {
    public T getTabItemData();
}
