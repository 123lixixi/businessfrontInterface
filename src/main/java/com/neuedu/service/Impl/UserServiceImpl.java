package com.neuedu.service.Impl;

import com.neuedu.dao.UserInfoMapper;
import com.neuedu.exception.MyException;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
   @Autowired
   UserInfoMapper userInfoMapper;
    @Override
    public UserInfo login(UserInfo userInfo) throws MyException {
        //step1:参数的非空判断
//        if(userInfo==null){
//            throw new MyException("参数不能为空");
//        }
        if(userInfo.getUsername()==null||userInfo.getUsername().equals("")){
            throw new MyException("用户名不能为空","login");
        }
        if(userInfo.getPassword()==null||userInfo.getPassword().equals("")){
            throw new MyException("密码不能为空","login");
        }
        //step2:判断用户名是否存在
          int username_result=userInfoMapper.existsUsername(userInfo.getUsername());
          if(username_result==0){
              throw new MyException("用户名不存在","login");
          }

        //step3:根据用户名和密码登录
        UserInfo userInfo1=userInfoMapper.findByUsernameAndPassword(userInfo);
          if(userInfo1==null)
              throw new MyException("密码不正确","login");
        //step4:判断权限
        if(userInfo1.getRole()!=1){//不是用户
            throw new MyException("没有管理员权限","login");
        }
        return userInfo1;
    }

    @Override
    public List<UserInfo> findAll() throws MyException {
       return userInfoMapper.selectAll();
    }

    @Override
    public int updateUserInfo(UserInfo userInfo) throws MyException {
        return userInfoMapper.updateByPrimaryKey(userInfo);
    }

    @Override
    public int deleteUserInfo(int userInfoId) throws MyException {
        return userInfoMapper.deleteByPrimaryKey(userInfoId);
    }

    @Override
    public int addUserInfo(UserInfo userInfo) throws MyException {
        return userInfoMapper.insert(userInfo);
    }

    @Override
    public UserInfo findUserInfoById(int userInfoId) {
        return userInfoMapper.selectByPrimaryKey(userInfoId);
    }
}
