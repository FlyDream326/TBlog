package com.handler.security;

import com.alibaba.fastjson.JSON;
import com.common.ResponseResult;
import com.enums.AppHttpCodeEnum;
import com.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        authException.printStackTrace();
        ResponseResult result =null;
        //InsufficientAuthenticationException
        //BadCredentialsException
        if(authException instanceof BadCredentialsException){
             result =
                    ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_ERROR);//505 用户名或密码错误
        }else if(authException instanceof InsufficientAuthenticationException){
            result =
                    ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);//401 需要登录后操作
        }else {
            result =
                    ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"认证或授权失败");//500
        }
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}
