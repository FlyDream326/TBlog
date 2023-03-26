package com;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.mapper")
@EnableSwagger2
public class TbBlogApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TbBlogApplication.class, args);
        //System.out.println(context.getBean(UserService.class).getClass().getName());
        System.out.println("http://localhost:7777/article");
        System.out.println("http://localhost:7777/swagger-ui.html");//端口号是server：port localhost 为本地测试
    }
}
