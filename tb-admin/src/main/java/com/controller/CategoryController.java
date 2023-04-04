package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.constants.SystemConstants;
import com.domain.ResponseResult;
import com.domain.dto.AddCategoryDto;
import com.domain.entity.Category;
import com.domain.vo.CategoryVo;
import com.domain.vo.PageVo;
import com.domain.vo.TagVo;
import com.service.CategoryService;
import com.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    public void export(HttpServletResponse response){
     categoryService.export(response);
    }
    @GetMapping("list")
    public ResponseResult categoryList(@RequestParam
                                       Integer pageNum,
                                       Integer pageSize,
                                       String name,
                                       String status){
        PageVo pageVo = categoryService.categoryList(pageNum,pageSize,name,status);
        return ResponseResult.okResult(pageVo);
    }
    @PostMapping
    public ResponseResult addCategory(@RequestBody AddCategoryDto dto){
        categoryService.addCategory(dto);
       return ResponseResult.okResult();
    }
    @GetMapping("/{id}")
    public ResponseResult getCategoryById(@PathVariable("id")Long id){
         CategoryVo categoryVo= categoryService.getCategoryById(id);
        return ResponseResult.okResult(categoryVo);
    }
    @PutMapping
    public ResponseResult updateCategory(@RequestBody AddCategoryDto dto){
        categoryService.updateCategory(dto);
        return ResponseResult.okResult();
    }
    @DeleteMapping("/{id}")
    public ResponseResult deleteCategory(@PathVariable("id")Long id){
        categoryService.getBaseMapper().deleteById(id);
        return ResponseResult.okResult();
    }
}
