package com.wsyu9a;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wsyu9a.mapper")
public class Wsyu9aApplication {

    public static void main(String[] args) {
        SpringApplication.run(Wsyu9aApplication.class, args);
    }
}