package com.job;

import com.constants.SystemConstants;
import com.entity.Article;
import com.service.ArticleService;
import com.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component

public class UpdateViewCountJob {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ArticleService articleService;
    @Scheduled
    public void UpdateViewCount(){
        //获取redis中的浏览量
        Map<String, Integer> viewCountMap = redisCache.getCacheMap(SystemConstants.ARTICLE_VIEWCOUNT);
        viewCountMap.entrySet()
                    .stream()
                    .map(new Function<Map.Entry<String, Integer>, Article>() {
                        @Override
                        public Article apply(Map.Entry<String, Integer> entry) {
                            return new Article(entry.getKey(),entry.getValue());
                        }
                    })
        //更新到数据库中
        articleService.updateBatchById()
    }
}
