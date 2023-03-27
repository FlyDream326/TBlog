package com.runner;

import com.constants.SystemConstants;
import com.domain.entity.Article;
import com.mapper.ArticleMapper;
import com.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ViewCountRunner implements CommandLineRunner {

    @Resource
    private ArticleMapper articleMapper;
    @Autowired
    private RedisCache redisCache;
    @Override
    public void run(String... args) throws Exception {
        // 查询博客信息 id viewCount(变化的属性在存入redis中不要使用Long类型)
        List<Article> articles = articleMapper.selectList(null);
        Map<String, Integer> viewCountMap = articles.stream()
                                                    .collect(Collectors.toMap(article -> article.getId().toString(),
                                                            article -> article.getViewCount().intValue()));
        //存到Redis中
        redisCache.setCacheMap(SystemConstants.ARTICLE_VIEWCOUNT,viewCountMap);
        System.out.println("存到Redis中");
    }
}
