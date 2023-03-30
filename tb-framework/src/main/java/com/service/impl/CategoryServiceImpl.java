package com.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.ResponseResult;
import com.constants.SystemConstants;
import com.domain.entity.Article;
import com.domain.entity.Category;
import com.domain.vo.ExcelCategoryVo;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.mapper.CategoryMapper;
import com.service.ArticleService;
import com.service.CategoryService;
import com.utils.BeanCopyUtils;
import com.domain.vo.CategoryVo;
import com.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spi.service.contexts.SecurityContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2023-03-09 16:21:25
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private ArticleService articleService;

    @Override
    public ResponseResult getCategoryList() {
        //查询文章表 状态为已发布的文章
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articleList = articleService.list(articleWrapper);
        //获取文章id，并去重
        Set<Long> categoryIds = articleList.stream()
                                             .map(article -> article.getCategoryId())
                                                .collect(Collectors.toSet());

        //查询分类表
        List<Category> categories = listByIds(categoryIds);

        categories = categories.stream()
                .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());

        //封装 vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public List<CategoryVo> listAllCategory() {
        LambdaQueryWrapper<Category> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus,SystemConstants.STATUS_NORMAL);
        List<Category> categoryList = list(queryWrapper);
        List<CategoryVo> categoryVoList = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        return categoryVoList;
    }

    @Override
    public void export(HttpServletResponse response) {
        //设置下载文件的请求头

        try {
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取需要导出的数据
            List<Category> categoryList = list();
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryList, ExcelCategoryVo.class);
            //把数据写入Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                        .doWrite(excelCategoryVos);

        } catch (Exception e) {
            //异常： 响应异常JSON
            //throw new SystemException(e);
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }


        
    }
}

