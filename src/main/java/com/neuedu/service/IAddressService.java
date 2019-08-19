package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;

public interface IAddressService {
    /**
     * 添加收货地址
     */
   public ServerResponse add(Integer userId,Shipping shipping);
   public ServerResponse del(Integer userId,Integer shippingId);
   public ServerResponse update(Shipping shipping);
   public ServerResponse select(Integer shippingId);
   public ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);
}
