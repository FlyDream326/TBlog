package com.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.ResponseResult;
import com.domain.dto.AddCategoryDto;
import com.domain.entity.Category;
import com.domain.vo.CategoryVo;
import com.domain.vo.PageVo;
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

    PageVo categoryList(Integer pageNum, Integer pageSize, String name, String status);

    void addCategory(AddCategoryDto dto);

    CategoryVo getCategoryById(Long id);

    void updateCategory(AddCategoryDto dto);
}

