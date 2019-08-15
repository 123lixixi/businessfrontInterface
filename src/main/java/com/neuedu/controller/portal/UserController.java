package com.neuedu.controller.portal;


import com.neuedu.annotation.MD5Utils;
import com.neuedu.common.Const;
import com.neuedu.common.ResponseCode;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;
    /**
     * 登录
     * 方法里的形参名字和接口里的一致时，可以省略@RequestParam
     * */
    @RequestMapping("/login")
    public ServerResponse login(UserInfo userInfo, HttpSession session, HttpServletResponse response){

        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        ServerResponse serverResponse=userService.login(userInfo);
        if(serverResponse.isSucess()){
            UserInfo userInfo1=(UserInfo)serverResponse.getData();
            session.setAttribute(Const.CURRENT_USER,userInfo1);
        }

        return serverResponse;
    }
    /**
     * 注册
     * */
    @RequestMapping("/register")
    public ServerResponse register(HttpSession httpSession,UserInfo userInfo){
      ServerResponse serverResponse=userService.addUserInfo(userInfo);
      if(serverResponse.isSucess()){
          serverResponse.setData(userInfo);
      }
      return serverResponse;
    }
    /**
     * 根据用户名查询密保问题
     * */
    @RequestMapping("/forget_get_question")
    public ServerResponse forget_get_question(UserInfo userInfo){
        ServerResponse serverResponse=userService.forget_get_question(userInfo.getUsername());
        return serverResponse;
    }
    /**
     * 提交问题答案
     * */
    @RequestMapping("/forget_check_answer")
    public ServerResponse forget_check_answer(UserInfo userInfo){
        ServerResponse serverResponse=userService.forget_check_answer(userInfo.getUsername(),userInfo.getQuestion(),userInfo.getAnswer());
        return serverResponse;
    }
    /**
     * 忘记密码的重置密码
     * */

    @RequestMapping("/forget_reset_password")
    public ServerResponse forget_reset_password(String username,String password,String forgetToken){
        ServerResponse serverResponse=userService.forget_reset_password(username,password,forgetToken);
        return serverResponse;
    }
    /**
     * 检查用户名是否有效
     * */
    @RequestMapping("/check_valid")
    public ServerResponse check_valid(String str,String type){
        ServerResponse serverResponse=userService.check_valid(str,type);
        return serverResponse;
    }
    /**
     * 获取登录用户信息
     * */
    @RequestMapping("/get_user_info")
    public ServerResponse get_user_info(HttpSession session){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo!=null){
            userInfo.setPassword("");
            userInfo.setQuestion("");
            userInfo.setAnswer("");
            userInfo.setRole(null);
          return  ServerResponse.createServerResponseBySucess(userInfo);
        }
           return ServerResponse.createServerResponseByFail("用户未登录，无法获取当前用户信息");
}
    /**
     * 登录中状态重置密码
     * */
    @RequestMapping("/reset_password")
    public ServerResponse reset_password(String passwordOld, String passwordNew,HttpSession session){

        ServerResponse serverResponse=userService.reset_password(passwordOld,passwordNew,session);
        return serverResponse;
    }
    /**
     * 登录状态更新个人信息
     * */
    @RequestMapping("/update_information")
    public ServerResponse update_information(String email, String phone, String question, String answer,HttpSession sesssion){

        ServerResponse serverResponse=userService.update_information(email,phone,question,answer,sesssion);
        return serverResponse;
    }
    /**
     * 退出登录
     * */
    @RequestMapping("/logout")
    public ServerResponse logout(HttpSession session){
         session.removeAttribute(Const.CURRENT_USER);
         return ServerResponse.createServerResponseBySucess();

    }
    /**
     * 获取登录用户详细信息
     * */
    @RequestMapping("/get_inforamtion")
    public ServerResponse get_inforamtion(HttpSession session){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo!=null){
           // userInfo.setPassword("");
            return  ServerResponse.createServerResponseBySucess(userInfo);
        }
        return ServerResponse.createServerResponseByFail("用户未登录，无法获取当前用户信息");
    }
}
