package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constants.SystemConstants;
import com.domain.entity.LoginUser;
import com.domain.entity.User;
import com.mapper.MenuMapper;
import com.mapper.UserMapper;
import com.service.MenuService;
import com.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MenuService menuService;
    @Resource
    private  MenuMapper menuMapper;
    @Resource
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName,username);

        User user = userMapper.selectOne(wrapper);
        //判断是否查到用户 如果没有查到抛出异常
       if(Objects.isNull(user)){
           throw new RuntimeException("用户未登录");
       }

       //返回用户信息
        if(user.getType().equals(SystemConstants.ADMIN)){
            List<String> perms = menuService.selectPermsByUserKey(user.getId());
            return new LoginUser(user,perms);
        }
        return new LoginUser(user,null);
    }
}
