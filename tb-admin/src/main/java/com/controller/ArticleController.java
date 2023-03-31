package com.controller;

import com.annotation.SystemLog;
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
    //新增文章
       return articleService.addArticle(dto);
   }

   @GetMapping("/content/article/list")
   public ResponseResult articleList(@RequestParam Integer pageNum,Integer pageSize,String title,String summary){
       //查询所有文章（若title&summary值为NULL）,否则根据title，summary进行模糊查询
       PageVo pageVo = articleService.articleList(pageNum,pageSize,title,summary);
       return ResponseResult.okResult(pageVo);
   }
   @GetMapping("content/article/{id}")
   public ResponseResult articleSearch(@PathVariable("id") Long id){
       //根据文章id进行文章查询
      AddArticleDto addArticleDto = articleService.articleSearch(id);
    return ResponseResult.okResult(addArticleDto);
   }
    @PutMapping("/content/article")
    @SystemLog(businessName = "文章更新")
    public ResponseResult articleUpdate(@RequestBody AddArticleDto dto){
       //更新文章
       articleService.articleUpdate(dto);
       return ResponseResult.okResult();
    }
    @DeleteMapping("/content/article/{id}")
    public ResponseResult articleDelete(@PathVariable("id")Long id){
       articleService.getBaseMapper().deleteById(id);
       return ResponseResult.okResult();
    }
}
