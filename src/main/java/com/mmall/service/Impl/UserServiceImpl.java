package com.mmall.service.Impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: UserServiceImpl
 * @Description:
 * @Author
 * @Date 2021/2/11
 * @Version 1.0
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
  @Autowired
  private UserMapper userMapper;
  
  @Override
  public ServerResponse<User> login(String username, String password) {
    int resultCount = userMapper.checkUsername(username);
    if (resultCount == 0) {
      return ServerResponse.createByErrorMessage("用户名不存在");
    }
    // todo 密码登录MD5
  
    User user = userMapper.selectLogin(username, password);
    if (user == null) {
      return ServerResponse.createByErrorMessage("密码错误");
    }
    user.setPassword(StringUtils.EMPTY);
    return ServerResponse.createdBySuccessMessage("登录成功");
  }
}
