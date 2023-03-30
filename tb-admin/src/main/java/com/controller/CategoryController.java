package com.controller;

import com.domain.ResponseResult;
import com.domain.vo.CategoryVo;
import com.domain.vo.TagVo;
import com.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/listAllCategory")
    public ResponseResult<CategoryVo> listAllCategory(){
        return ResponseResult.okResult(categoryService.listAllCategory());
    }
    @GetMapping("/export")
    public void export(HttpServletResponse response){
     categoryService.export(response);
    }
}
