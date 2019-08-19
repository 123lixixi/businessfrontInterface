package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface IOrderService {
    /**
     *创建订单
     */
    ServerResponse createOrder(Integer userId,Integer shippingId);
    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);
    ServerResponse detail(Integer userId,Long orderNo);
    ServerResponse cancel(Integer userId,Long orderNo);
}
