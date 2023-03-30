package com.service.impl;

import com.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ps")
public class PermissionService {
    /**
     * 判断当前用户是否就有Permission
     * @param Permission
     * @return
     */
    public boolean hasPermission(String permission){
        //如果是超级管理员 直接返回true
        if(SecurityUtils.isAdmin()){
            return true;
        }
        //否则返回当前用户所具有的权限
        List<String> permissions = SecurityUtils.getLoginUser().getPermissions();
        return permissions.contains(permission);
    }
}
