package com.neuedu.controller.backend;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    ICategoryService categoryService;
    /**
     * 获取品类子节点（平级）
     */
    @RequestMapping("get_category")
    public ServerResponse get_category(HttpSession session, Integer integer){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
         return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return categoryService.get_category(integer);
    }
    /**
     * 获取当前分类id及递归子节点categoryId
     */
    @RequestMapping("get_deep_category")
    public ServerResponse get_deep_category(HttpSession session, Integer integer){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return categoryService.get_deep_category(integer);
    }

}
