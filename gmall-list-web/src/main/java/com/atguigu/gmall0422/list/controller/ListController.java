package com.atguigu.gmall0422.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0422.bean.SkuLsParams;
import com.atguigu.gmall0422.bean.SkuLsResult;
import com.atguigu.gmall0422.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @RequestMapping("list.html")
    //@ResponseBody
    public String getList(SkuLsParams skuLsParams, HttpServletRequest request){
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        request.setAttribute("skuLsResult",skuLsResult);
        return "list";
    }
}
