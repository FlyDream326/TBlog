package com.Controller;

import com.common.ResponseResult;
import com.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /*@GetMapping("/list")
    public List<Article> test(){
        return articleService.list();
    }*/

    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList(){
        //查询热门文章 封账成ResponseResult返回
        ResponseResult result = articleService.hotArticleList();
        return result;
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
       return articleService.articleList(pageNum,pageSize,categoryId);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id")Long id){
      return   articleService.getArticleDetail(id);

    }
    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable Long id){
        return articleService.updateViewCount(id);
    }

}
