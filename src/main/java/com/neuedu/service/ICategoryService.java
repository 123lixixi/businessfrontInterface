package com.neuedu.service;

import com.neuedu.common.ServerResponse;

import java.util.List;

public interface ICategoryService {

    /**
     * 获取品类子节点（平级）
     * */
    ServerResponse get_category(Integer integer);
    /**
     * 获取当前分类id及递归子节点categoryId
     * */
    ServerResponse get_deep_category(Integer categoryId);

}
