package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.domain.ResponseResult;
import com.constants.SystemConstants;
import com.domain.dto.AddArticleDto;
import com.domain.dto.AddCommentDto;
import com.domain.entity.Article;
import com.domain.entity.ArticleTag;
import com.domain.entity.Category;
import com.domain.entity.Tag;
import com.mapper.ArticleMapper;
import com.mapper.ArticleTagMapper;
import com.service.ArticleService;
import com.service.ArticleTagService;
import com.service.CategoryService;
import com.utils.BeanCopyUtils;
import com.utils.RedisCache;
import com.domain.vo.ArticleDetailVo;
import com.domain.vo.ArticleListVo;
import com.domain.vo.HotArticleVo;
import com.domain.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private RedisCache redisCache;
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleTagService articleTagService;
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
        //从Redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue(SystemConstants.ARTICLE_VIEWCOUNT, id.toString());
        article.setViewCount(viewCount.longValue());
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

    @Override
    @Transactional
    public ResponseResult addArticle(AddArticleDto dto) {
        Article article = BeanCopyUtils.copyBean(dto, Article.class);
        save(article);
        List<ArticleTag> articleTag = getArticleTag(article.getId(), dto);
        articleTagService.saveBatch(articleTag);
        return ResponseResult.okResult() ;
    }

    @Override
    public PageVo articleList(Integer pageNum, Integer pageSize, String title, String summary) {
        LambdaQueryWrapper<Article> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(title), Article::getTitle, title)
                    .like(StringUtils.hasText(summary), Article::getSummary, summary);
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<ArticleDetailVo> vos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleDetailVo.class);
        return new PageVo(vos,page.getTotal());
    }

    @Override
    public AddArticleDto articleSearch(Long id) {
        LambdaQueryWrapper<ArticleTag> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,id);
        List<ArticleTag> articleTagList = articleTagService.list(queryWrapper);
        List<Long> list = articleTagList.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        Article article = getById(id);
        AddArticleDto dto = BeanCopyUtils.copyBean(article, AddArticleDto.class);
        dto.setTags(list);
        return dto;
    }

    @Override
    public void articleUpdate(AddArticleDto dto) {
        Article article = BeanCopyUtils.copyBean(dto, Article.class);
        updateById(article);
        /*
         ArticleTag表在数据库中没有逻辑删除字段 需要绕过MP的逻辑删除
         采用先删除后存储的方法解决
         删除使用@Select注解在Mapper类中使用原生SQL语句注入
         */
        articleTagMapper.deleteByArticleId(article.getId());
        articleTagService.saveBatch(getArticleTag(article.getId(),dto));
    }

    private List<ArticleTag> getArticleTag(Long id, AddArticleDto dto) {
        List<ArticleTag> articleTagList = dto.getTags().stream()
                .map(tag -> new ArticleTag(id, tag))
                .collect(Collectors.toList());
        return articleTagList;
    }
}
