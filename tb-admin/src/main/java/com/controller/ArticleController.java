package com.controller;

import com.domain.ResponseResult;
import com.domain.dto.AddArticleDto;
import com.domain.vo.ArticleDetailVo;
import com.domain.vo.PageVo;
import com.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;
   @PostMapping("/content/article")
    public ResponseResult addArticle(@RequestBody AddArticleDto dto){

       return articleService.addArticle(dto);
   }

   @GetMapping("/content/article/list")
   public ResponseResult articleSearch(@RequestParam Integer pageNum,Integer pageSize,String title,String summary){
       PageVo pageVo = articleService.articleSearch(pageNum,pageSize,title,summary);
       return ResponseResult.okResult(pageVo);
   }
   @GetMapping("content/article/{id}")
   public ResponseResult articleUpdate(@PathVariable("id") Long id){
      AddArticleDto addArticleDto = articleService.articleUpdate(id);
    return ResponseResult.okResult(addArticleDto);
   }
}
