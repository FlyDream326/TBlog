package com.runner;

import com.service.ArticleService;
import com.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component

public class TestRunner implements CommandLineRunner {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;

    @Override
    public void run(String... args) throws Exception {

        //System.out.println("1111");
    }
}
