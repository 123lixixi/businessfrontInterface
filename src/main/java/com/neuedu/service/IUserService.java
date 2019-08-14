package com.neuedu.service;

import com.neuedu.exception.MyException;
import com.neuedu.pojo.UserInfo;

import java.util.List;

public interface IUserService {
    public UserInfo login(UserInfo userInfo) throws MyException;
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
    public int addUserInfo(UserInfo userInfo)throws MyException;
    /**
     * 根据用户id查询用户信息
     */
    public UserInfo findUserInfoById(int userInfoId);

}
