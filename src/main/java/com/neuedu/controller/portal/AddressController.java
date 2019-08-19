package com.neuedu.controller.portal;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipping")
public class AddressController {
    @Autowired
    IAddressService addressService;
    /**
     * 添加地址
     */
    @RequestMapping("/add")
    public ServerResponse add(HttpSession session, Shipping shipping){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return addressService.add(userInfo.getId(),shipping);
    }
    /**
     * 删除地址
     */
    @RequestMapping("/del")
    public ServerResponse del(HttpSession session, Integer shippingId){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return addressService.del(userInfo.getId(),shippingId);
    }
    /**
     * 登录状态更新地址
     */
    @RequestMapping("/update")
    public ServerResponse update(HttpSession session, Shipping shipping){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        shipping.setUserId(userInfo.getId());
        return addressService.update(shipping);
    }
    /**
     * 选中查看具体的地址
     */
    @RequestMapping("/select")
    public ServerResponse select(HttpSession session, Integer shippingId){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return addressService.select(shippingId);
    }
    /**
     * 地址列表
     */
    @RequestMapping("/list")
    public ServerResponse list(HttpSession session,
                               @RequestParam(name = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }

        return addressService.list(userInfo.getId(),pageNum,pageSize);
    }
}
