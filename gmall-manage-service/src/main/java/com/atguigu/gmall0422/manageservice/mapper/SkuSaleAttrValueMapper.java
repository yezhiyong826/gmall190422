package com.atguigu.gmall0422.manageservice.mapper;

import com.atguigu.gmall0422.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue>{

    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(String spuId);
}
