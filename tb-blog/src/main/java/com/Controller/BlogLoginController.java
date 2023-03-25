package com.Controller;

import com.annotation.SystemLog;
import com.common.ResponseResult;
import com.entity.User;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {

    @Autowired
    private BlogLoginService blogLoginService;

    @SystemLog(businessName = "登录") //需要被增强的方法
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
       return blogLoginService.login(user);
    }


    @PostMapping("/logout")
    public ResponseResult logout(){
        return blogLoginService.logout();
    }
}
