package com.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.ResponseResult;
import com.domain.entity.Category;
import com.domain.vo.CategoryVo;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2023-03-09 16:21:25
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    List<CategoryVo> listAllCategory();

    void export(HttpServletResponse response);
}

