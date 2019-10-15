package com.atguigu.gmall0422.service;

import com.atguigu.gmall0422.bean.SkuLsInfo;
import com.atguigu.gmall0422.bean.SkuLsParams;
import com.atguigu.gmall0422.bean.SkuLsResult;

public interface ListService {

    void saveSkuInfo(SkuLsInfo skuLsInfo);

    /**
     * 动态生产dsl语句，从es中检索结果集
     */
    SkuLsResult search(SkuLsParams skuLsParams);
}
