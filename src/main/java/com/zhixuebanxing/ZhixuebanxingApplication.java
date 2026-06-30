package com.zhixuebanxing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zhixuebanxing.mapper")
public class ZhixuebanxingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhixuebanxingApplication.class, args);
    }
}
