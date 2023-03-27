package com.service;


import com.domain.ResponseResult;
import com.domain.entity.User;
import com.domain.vo.AdminUserInfoVo;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();

    ResponseResult<AdminUserInfoVo> getInfo();

    ResponseResult getRouters();
}
