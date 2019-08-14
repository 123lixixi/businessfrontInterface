package com.neuedu.controller.portal;

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

       // ServerResponse serverResponse=userService.login()
        return null;
    }
}
