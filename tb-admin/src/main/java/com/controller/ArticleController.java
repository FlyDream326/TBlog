package com.controller;

import com.domain.ResponseResult;
import com.domain.dto.AddArticleDto;
import com.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;
   @PostMapping("/content/article")
    public ResponseResult addArticle(@RequestBody AddArticleDto dto){
      return articleService.addArticle(dto);
   }
}
