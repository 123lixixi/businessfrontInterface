package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.exception.MyException;
import com.neuedu.pojo.UserInfo;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface IUserService {
    public ServerResponse login(UserInfo userInfo) throws MyException;
    /**
     * 查询用户
     * */
    public List<UserInfo> findAll()throws MyException;
    /**
     * 修改用户
     * */
    public int updateUserInfo(UserInfo userInfo)throws MyException;
    /**
     * 删除用户
     * */
    public int deleteUserInfo(int userInfoId)throws MyException;
    /**
     * 添加用户
     * */
    public ServerResponse addUserInfo(UserInfo userInfo)throws MyException;
    /**
     * 根据用户id查询用户信息
     */
    public UserInfo findUserInfoById(int userInfoId);
    /**
     * 根据用户id查询密保问题
     */
    public ServerResponse forget_get_question(String username);
    /**
     * 提交问题答案
     * */
    public ServerResponse forget_check_answer(String username,String question,String answer);
    ServerResponse forget_reset_password(String username,String password,String forgetToken);
    /**
     * 检查用户名或邮箱是否有效
     * */
    ServerResponse check_valid(String str,String type);
    /**
     * 登录中状态重置密码
     * */
    ServerResponse reset_password(String passwordOld, String passwordNew,HttpSession session);
    /**
     * 登录状态更新个人信息
     * */
    ServerResponse update_information(String email, String phone,String question,String answer,HttpSession session);

}
