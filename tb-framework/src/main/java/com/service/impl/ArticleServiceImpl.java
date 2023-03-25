package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.ResponseResult;
import com.constants.SystemConstants;
import com.entity.Article;
import com.entity.Category;
import com.mapper.ArticleMapper;
import com.service.ArticleService;
import com.service.CategoryService;
import com.utils.BeanCopyUtils;
import com.utils.RedisCache;
import com.vo.ArticleDetailVo;
import com.vo.ArticleListVo;
import com.vo.HotArticleVo;
import com.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章 封账成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只查询10条
        Page<Article> page = new Page(SystemConstants.ARTICLE_STATUS_DRAFT,10);
        page(page,queryWrapper);

        List<Article> articles = page.getRecords();

        List<HotArticleVo> vs = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);

        /*List<HotArticleVo> hotArticleVos = new ArrayList<>();
        //Bean拷贝

        for (Article article : articles) {
            HotArticleVo vo = new HotArticleVo();
            BeanUtils.copyProperties(article,vo);
            hotArticleVos.add(vo);
        }*/

        return ResponseResult.okResult(vs);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        // 查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        //如果有 categoryId 就要 查询时要和传入的相同
        queryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0,Article::getCategoryId,categoryId);
        //状态是正式发布的
        queryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        //对isTop进行降序
        queryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);

        //配置CategoryName
        List<Article> articles = page.getRecords();
        articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());
       /* for (Article article : articles) {
            Category category = categoryService.getById(article.getCategoryId());
            article.setCategoryName(category.getName());
        }*/

        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos,page.getTotal());


        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //封装Vo
        ArticleDetailVo articleDetailVo  = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //查询对应的分类名称
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装返回响应

        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {

        //更新文章的浏览量
        redisCache.incrementCacheValue(SystemConstants.ARTICLE_VIEWCOUNT,id.toString(),1);
        return ResponseResult.okResult();
    }
}
