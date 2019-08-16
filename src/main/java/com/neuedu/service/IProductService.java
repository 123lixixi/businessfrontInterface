package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import org.springframework.web.bind.annotation.RequestParam;

public interface IProductService {
    /**
     * 商品上下架
     * */
    ServerResponse saveOrUpdate(Product product);
    /**
     * 后台商品详情
     * */
    ServerResponse detail(Integer productId);
    ServerResponse detail_portal(Integer productId);
    /**
     * 前台商品搜索
     * */
    ServerResponse list_portal(Integer categoryId,
                               String keyword,
                               Integer pageNum,
                               Integer pageSize,
                               String orderBy);
    /**
     * 后台商品搜索-分页
     * */
    ServerResponse list(Integer pageNum, Integer pageSize);
    /**
     * 前台-获取产品分类
     * */
    ServerResponse topcategory(Integer sid);
    }
