package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.domain.ResponseResult;
import com.domain.dto.AddArticleDto;
import com.domain.entity.Article;
import com.domain.vo.PageVo;

public interface ArticleService extends IService<Article> {

    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult addArticle(AddArticleDto dto);

    PageVo articleSearch(Integer pageNum, Integer pageSize, String title, String summary);

    AddArticleDto articleUpdate(Long id);
}
