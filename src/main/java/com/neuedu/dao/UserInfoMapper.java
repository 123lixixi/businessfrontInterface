package com.neuedu.dao;

import com.neuedu.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int insert(@Param("user") UserInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    UserInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    List<UserInfo> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_user
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(@Param("user") UserInfo record);
    /**
     * 判断用户名是否存在
     * */
    int existsUsername(@Param("username") String username);
    /**
     * 判断邮箱是否存在
     * */
    int existsEmail(@Param("email") String email);
    UserInfo findByUsernameAndPassword(@Param("user") UserInfo userInfo);
    /**
     * 根据用户名查询密保问题
     * */
    String selectQuestionByUsername(@Param("username")String username);
    /**
     * 根据用户名、密保问题、答案查询
     * */
    int selectByUsernameAndQuestionAndAnswer(@Param("username")String username,
                                             @Param("question") String question,
                                             @Param("answer")String answer);

/**
 * 修改用户密码接口
 * */
 int updateUserpassword(@Param("username")String username,@Param("password")String password);


}