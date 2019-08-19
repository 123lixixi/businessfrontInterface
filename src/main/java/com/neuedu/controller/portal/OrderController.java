package com.neuedu.controller.portal;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IOrderService;
import com.neuedu.service.Impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    IOrderService orderService;
    /**
     * 创建订单
     */
    @RequestMapping("/create")
    public ServerResponse createOrder(HttpSession session,Integer shippingId){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return orderService.createOrder(userInfo.getId(),shippingId);
    }
    /**
     * 订单List
     */
    @RequestMapping("/list")
    public ServerResponse list(HttpSession session,
                               @RequestParam(name = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return orderService.list(userInfo.getId(),pageNum,pageSize);
    }
    /**
     * 订单详情detail
     */
    @RequestMapping("/detail")
    public ServerResponse detail(HttpSession session,Long orderNo){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return orderService.detail(userInfo.getId(),orderNo);
    }
    /**
     * 取消订单
     */
    @RequestMapping("/cancel")
    public ServerResponse cancel(HttpSession session,Long orderNo){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return orderService.cancel(userInfo.getId(),orderNo);
    }
    /**
     * 获取订单的商品信息
     */
    @RequestMapping("/get_order_cart_product")
    public ServerResponse get_order_cart_product(HttpSession session){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }
        return orderService.get_order_cart_product(userInfo.getId());
    }
    /**
     * 支付接口
     */
    @RequestMapping("/pay")
    public ServerResponse pay(HttpSession session,Long orderNo){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("需要登录");
        }

        return null;
    }
}
