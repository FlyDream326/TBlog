package com.service;


import com.common.ResponseResult;
import com.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
