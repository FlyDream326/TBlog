package com.handler.MybatisPuls;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //配置MP字段自动填充

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = getUserId();
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy",userId , metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = getUserId();
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", userId, metaObject);


    }

    private Long getUserId() {
        Long userId = null;
        try {
            //测试是否登录
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            return -1L;//表示是自己创建
        }
        return userId;
    }
}