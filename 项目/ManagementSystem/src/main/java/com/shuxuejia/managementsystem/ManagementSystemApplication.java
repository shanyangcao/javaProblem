package com.shuxuejia.managementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {
        "com.shuxuejia.managementsystem",
        "com.other"  // 假设组件在这个包下
})
public class ManagementSystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(ManagementSystemApplication.class, args);
    }

}
