package com.atguigu.gmall0422.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置类
@Configuration
public class RedisConfig {
    //获取参数
    @Value("${spring.redis.host:disabled}")//disabled表示有就取，没有就给默认值
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.timeOut:10000}")
    private int timeOut;

    @Bean
    public RedisUtil getRdisUtil(){
        if ("disabled".equals(host)){
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initJedisPool(host,port,timeOut);
        return redisUtil;
    }
}
