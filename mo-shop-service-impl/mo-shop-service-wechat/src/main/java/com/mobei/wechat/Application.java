package com.mobei.wechat;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
//@EnableSwagger2
@EnableSwagger2Doc//这个注解好像可以直接在yml中配置,而@EnableSwagger2不行,需要写配置类
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
