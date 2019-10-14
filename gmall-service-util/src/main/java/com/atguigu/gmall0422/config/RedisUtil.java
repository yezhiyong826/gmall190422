package com.atguigu.gmall0422.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//工具类
public class RedisUtil {

    //JedisPool连接池
    private JedisPool jedisPool;

    //Jedispoll初始化方法
    public void  initJedisPool(String host,int port,int timeOut){
        //配制连接池的参数
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(200);
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        jedisPoolConfig.setMinIdle(10);
        jedisPoolConfig.setBlockWhenExhausted(true);
        jedisPoolConfig.setTestOnBorrow(true);

        jedisPool = new JedisPool(jedisPoolConfig,host,port,timeOut);
    }

    //获取jedis
    public Jedis getJedis(){
        return jedisPool.getResource();
    }
}
