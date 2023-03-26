package com.controller;

import com.annotation.SystemLog;
import com.domain.ResponseResult;
import com.domain.entity.User;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @SystemLog(businessName = "登录") //需要被增强的方法
    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
       return loginService.login(user);
    }


    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }
}
