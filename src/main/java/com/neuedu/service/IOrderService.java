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
    ServerResponse get_order_cart_product(Integer userId);
    ServerResponse list_manage(Integer pageNum,Integer pageSize);
    ServerResponse search(Long orderNo);
    ServerResponse send_goods(Long orderNo);
    /**
     * 支付接口
     */
    ServerResponse pay(Integer userId,Long orderNo);
}
