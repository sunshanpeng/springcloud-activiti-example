package com.platform.activiti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @Author sunshanpeng
 * @Date 2018/5/15
 * @Time 18:33
 */
@Configuration
public class MyConfiguration {

    @Bean(name = "delayExecutorService")
    public ScheduledExecutorService getDelayExecutorService() {
        return new ScheduledThreadPoolExecutor(1);
    }
}
