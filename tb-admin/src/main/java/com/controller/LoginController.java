package com.controller;

import com.annotation.SystemLog;
import com.domain.ResponseResult;
import com.domain.entity.User;
import com.domain.vo.AdminUserInfoVo;
import com.domain.vo.RoutersVo;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        return loginService.getInfo();
    }

    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }

    @GetMapping("/getRouters")
    public ResponseResult getRouters(){
         return loginService.getRouters();
    }
}
