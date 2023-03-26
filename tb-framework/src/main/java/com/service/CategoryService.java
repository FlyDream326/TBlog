package com.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.ResponseResult;
import com.domain.entity.Category;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2023-03-09 16:21:25
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();
}

