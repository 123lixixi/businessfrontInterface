package com.neuedu.controller.backend;

import com.neuedu.annotation.MD5Utils;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    IUserService userService;
    /**
     * 管理员登录
     * 方法里的形参名字和接口里的一致时，可以省略@RequestParam
     * */
    @RequestMapping("/login")
    public ServerResponse login(UserInfo userInfo, HttpSession session, HttpServletResponse response){

        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        ServerResponse serverResponse=userService.login(userInfo,0);
        if(serverResponse.isSucess()){
            UserInfo userInfo1=(UserInfo)serverResponse.getData();
            session.setAttribute(Const.CURRENT_USER,userInfo1);
        }

        return serverResponse;
    }
    /**
     * 用户列表
     * */
    @RequestMapping("/list")
    public ServerResponse list( HttpSession session,@RequestParam(name = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.createServerResponseByFail(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        ServerResponse serverResponse=userService.list(pageNum,pageSize);
        return serverResponse;
    }


}
