package com.atguigu.gmall0422.manageservice.mapper;

import com.atguigu.gmall0422.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String skuId, String spuId);
}

