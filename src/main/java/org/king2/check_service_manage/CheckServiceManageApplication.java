package org.king2.check_service_manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 描述：检测服务管理启动类
 * @author 刘梓江
 * @date 2021/5/13 14:38
 */
@EnableScheduling
@SpringBootApplication
public class CheckServiceManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckServiceManageApplication.class, args);
    }
}
