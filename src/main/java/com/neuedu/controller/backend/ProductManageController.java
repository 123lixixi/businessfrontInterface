package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    IProductService productService;
    /**
     * 新增OR更新产品
     * */
    @RequestMapping("/saveOrUpdate")
    public ServerResponse saveOrUpdate(HttpSession session, Product product){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.saveOrUpdate(product);
    }
    /**
     * 查看商品详情
     * */
    @RequestMapping("/detail")
    public ServerResponse detail(HttpSession session,Integer productId){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.detail(productId);
    }
    /**
     * 后台-查看商品列表
     * */
    @RequestMapping("/list")
    public ServerResponse list(HttpSession session,
                               @RequestParam(name="pageNum",defaultValue = "1",required = false) Integer pageNum,
                               @RequestParam(name="pageSize",required = false,defaultValue = "10") Integer pageSize){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.list(pageNum,pageSize);
    }

}
