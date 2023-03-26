package com.service;


import com.domain.ResponseResult;
import com.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
