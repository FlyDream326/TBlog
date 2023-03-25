package com.job;

import org.springframework.stereotype.Component;

@Component
public class TestJob {

    //@Scheduled(cron = "0/5 * * * * ?")
    public void test(){
        System.out.println("Hello World!");
    }
}
