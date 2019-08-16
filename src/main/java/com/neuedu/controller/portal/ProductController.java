package com.neuedu.controller.portal;

import com.neuedu.common.ServerResponse;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    IProductService productService;
    /**
     * 商品详情
     * */
    @RequestMapping("/detail")
    public ServerResponse detail(Integer productId){
      return productService.detail_portal(productId);
    }
    /**
     * 前台-搜索商品并排序
     * */
    @RequestMapping("/list")
    public ServerResponse list(@RequestParam(required = false)Integer categoryId,
                               @RequestParam(required = false)String keyword,
                               @RequestParam(name="pageNum",defaultValue = "1",required = false) Integer pageNum,
                               @RequestParam(name="pageSize",required = false,defaultValue = "10") Integer pageSize,
                               @RequestParam(name="orderBy",required = false,defaultValue = "") String orderBy){


        return productService.list_portal(categoryId,keyword,pageNum,pageSize,orderBy);
    }
    /**
     * 前台-获取产品分类
     * */
    @RequestMapping("/topcategory")
   ServerResponse topcategory(@RequestParam(name="sid",defaultValue = "0",required = false)Integer sid){
        return productService.topcategory(sid);
    }
}
