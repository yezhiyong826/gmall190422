package com.atguigu.gmall0422.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0422.bean.SpuImage;
import com.atguigu.gmall0422.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("spuImageList")
    public List<SpuImage> spuImageList(String spuId){
        return manageService.getSpuImageList(spuId);
    }
}
