package com.neuedu.controller;

import com.neuedu.common.ServerResponse;
import com.neuedu.config.AppConfig;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    IUserService userService;
    @RequestMapping("/login")
    public UserInfo login(UserInfo userInfo){
        UserInfo userInfo1=userService.login(userInfo);
        return userInfo1;
    }
//    @Value("${jdbc.driver}")
//   private String driver;
//    @Value("${jdbc.username}")
//    private String username;
//    @Value("${jdbc.password}")
//    private String password;
    @Autowired
    AppConfig appConfig;
    @RequestMapping("/test")
    public String getDriver(){
      return appConfig.getDriver()+" "+appConfig.getUsername()+" "+appConfig.getPassword();
    }
    @RequestMapping("/res")
    public ServerResponse ress(){
        return ServerResponse.createServerResponseBySucess(null,"hello");
    }
}
