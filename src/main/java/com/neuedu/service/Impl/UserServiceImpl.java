package com.neuedu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neuedu.annotation.MD5Utils;
import com.neuedu.annotation.TokenCache;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.exception.MyException;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
   @Autowired
   UserInfoMapper userInfoMapper;
    @Override
    public ServerResponse login(UserInfo userInfo,int role) throws MyException {

        //step1:参数的非空判断
        if(userInfo.getUsername()==null||userInfo.getUsername().equals("")){
            return ServerResponse.createServerResponseByFail("用户名不能为空");
        }
        if(userInfo.getPassword()==null||userInfo.getPassword().equals("")){
            return ServerResponse.createServerResponseByFail("密码不能为空");
        }
        //step2:判断用户名是否存在
          int username_result=userInfoMapper.existsUsername(userInfo.getUsername());
          if(username_result==0){
              return ServerResponse.createServerResponseByFail("用户名不存在");
          }

        //step3:根据用户名和密码登录
        UserInfo userInfo1=userInfoMapper.findByUsernameAndPassword(userInfo);
          if(userInfo1==null)
              return ServerResponse.createServerResponseByFail("密码不正确");
        //step4:判断权限

            if(userInfo1.getRole()==0&&role==1){//不是用户
                return ServerResponse.createServerResponseByFail("没有用户权限");
        }
            if(userInfo1.getRole()==1&&role==0){//不是管理员
                return ServerResponse.createServerResponseByFail("没有管理员权限");}

        return ServerResponse.createServerResponseBySucess(userInfo1);
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
    public ServerResponse addUserInfo(UserInfo userInfo) throws MyException {
        //step1:参数的非空判断
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("参数不能为空");
        }
        //step2:校验用户名
        int result=userInfoMapper.existsUsername(userInfo.getUsername());
        if(result==1){
            return ServerResponse.createServerResponseByFail("用户名已存在");
        }
        //step3:校验邮箱
       int result_email= userInfoMapper.existsEmail(userInfo.getEmail());
        if(result_email>0){
            return ServerResponse.createServerResponseByFail("邮箱不能重复");
        }
        //step4:注册
        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int count=userInfoMapper.insert(userInfo);
        if(count>0){
            return ServerResponse.createServerResponseBySucess("注册成功");
        }
        //step5:返回结果
        return ServerResponse.createServerResponseByFail("注册失败");
    }

    @Override
    public UserInfo findUserInfoById(int userInfoId) {
        return userInfoMapper.selectByPrimaryKey(userInfoId);
    }

    @Override
    public ServerResponse forget_get_question(String username) {
        //step1:参数非空校验
        if(username==null||username.equals("")){
            return ServerResponse.createServerResponseByFail("用户名不能为空");
        }
        //step2:校验用户名
        int result=userInfoMapper.existsUsername(username);
        if(result==0){
            return ServerResponse.createServerResponseByFail("用户名不存在");
        }
        //step3:查找密保问题
        String question=userInfoMapper.selectQuestionByUsername(username);
        if(question==null||question.equals("")){
            return ServerResponse.createServerResponseByFail("密保问题空");
        }

        return ServerResponse.createServerResponseBySucess(question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {
        //step1:参数校验
        if(username==null||username.equals("")){
            return ServerResponse.createServerResponseByFail("用户名不能为空");
        }
        if(question==null||question.equals("")){
            return ServerResponse.createServerResponseByFail("问题不能为空");
        }
        if(answer==null||answer.equals("")){
            return ServerResponse.createServerResponseByFail("答案不能为空");
        }

        //step2:根据username和question，answer查询
        int count=userInfoMapper.selectByUsernameAndQuestionAndAnswer(username,question,answer);
        if(count==0){
            //答案错误
            return ServerResponse.createServerResponseByFail("答案错误");
        }
        //step3:服务端生成一个token保存并将token返回给客户端
        String forgetToken= UUID.randomUUID().toString();
        //guava cache
        TokenCache.set(username,forgetToken);

        return ServerResponse.createServerResponseBySucess(forgetToken);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String password, String forgetToken) {
        //step1:参数校验
        if(username==null||username.equals("")){
            return ServerResponse.createServerResponseByFail("用户名不能为空");
        }
        if(password==null||password.equals("")){
            return ServerResponse.createServerResponseByFail("密码不能为空");
        }
        if(forgetToken==null||forgetToken.equals("")){
            return ServerResponse.createServerResponseByFail("token不能为空");
        }
        //step2:token校验
        String token=TokenCache.get(username);
        if(token==null){
            return ServerResponse.createServerResponseByFail("token过期");
        }
        if(!token.equals(forgetToken)){
            return ServerResponse.createServerResponseByFail("无效的token");
        }
        //step3:修改密码
        String passworda=MD5Utils.getMD5Code(password);
        int count=userInfoMapper.updateUserpassword(username,passworda);
        if(count>0){
            return ServerResponse.createServerResponseBySucess();
        }

        return ServerResponse.createServerResponseByFail("修改失败");
    }

    @Override
    public ServerResponse check_valid(String str, String type) {
        //step1:参数非空校验
        if(str==null||str.equals("")){
            return ServerResponse.createServerResponseByFail("用户名或邮箱不能为空");
        }
        if(type==null||type.equals("")){
            return ServerResponse.createServerResponseByFail("校验的参数类型不能为空");
        }
        //step2:校验用户名
        if(type.equals("username")){
         int result=userInfoMapper.existsUsername(str);
         if(result>0){
             return ServerResponse.createServerResponseByFail("用户名已存在");
         }else{
             return ServerResponse.createServerResponseBySucess("校验成功");
         }
        }
        //step3:校验邮箱
       else if(type.equals("email")){
         int result1=userInfoMapper.existsEmail(str);
         if(result1>0){
             return ServerResponse.createServerResponseByFail("邮箱已存在");
         }else{
             return ServerResponse.createServerResponseBySucess("校验成功");
         }
        }
    else
        return ServerResponse.createServerResponseByFail("参数类型错误");
    }

    @Override
    public ServerResponse reset_password(String passwordOld, String passwordNew,HttpSession session) {

        //step1:参数非空校验
        if(passwordOld==null||passwordOld.equals("")){
            return ServerResponse.createServerResponseByFail("旧密码不能为空");
        }
        if(passwordNew==null||passwordNew.equals("")){
            return ServerResponse.createServerResponseByFail("新密码不能为空");
        }
        //step2：修改密码
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("用户未登录，无法获取当前用户信息");
        }
        else{
            userInfo.setPassword(MD5Utils.getMD5Code(passwordOld));
            UserInfo userInfo1=userInfoMapper.findByUsernameAndPassword(userInfo);
            if(userInfo1==null){
                return ServerResponse.createServerResponseByFail("旧密码输入错误");
            }
            userInfo1.setPassword(MD5Utils.getMD5Code(passwordNew));
            int count= userInfoMapper.updateUserpassword(userInfo1.getUsername(),userInfo1.getPassword());
            if(count>0){
                userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
                return ServerResponse.createServerResponseBySucess("修改密码成功");
            }
        }
         return ServerResponse.createServerResponseByFail("密码修改失败");
    }

    @Override
    public ServerResponse update_information(String email, String phone, String question, String answer, HttpSession session) {
        //step1:参数非空校验
        if(email==null||email.equals("")){
            return ServerResponse.createServerResponseByFail("邮箱不能为空");
        }
        if(phone==null||phone.equals("")){
            return ServerResponse.createServerResponseByFail("电话不能为空");
        }
        if(question==null||question.equals("")){
            return ServerResponse.createServerResponseByFail("密保问题不能为空");
        }
        if(answer==null||answer.equals("")){
            return ServerResponse.createServerResponseByFail("密保答案不能为空");
        }
        //修改用户信息
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENT_USER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByFail("用户未登录");
        }
        userInfo.setEmail(email);
        userInfo.setPhone(phone);
        userInfo.setAnswer(answer);
        userInfo.setQuestion(question);
        int count=userInfoMapper.updateByPrimaryKey(userInfo);
        UserInfo u=userInfoMapper.findByUsernameAndPassword(userInfo);
        session.setAttribute(Const.CURRENT_USER,u);

        if(count>0){
            return ServerResponse.createServerResponseBySucess("更新个人信息成功");
        }
     return ServerResponse.createServerResponseByFail("更新个人信息失败");
    }

    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<UserInfo> userInfoList=userInfoMapper.selectAll();
        Iterator<UserInfo> userInfoIterator = userInfoList.iterator();
        while (userInfoIterator.hasNext()){
            UserInfo userInfo = userInfoIterator.next();
            userInfo.setPassword("");
        }
        PageInfo pageInfo = new PageInfo(userInfoList);
        return ServerResponse.createServerResponseBySucess(pageInfo);

    }
}
