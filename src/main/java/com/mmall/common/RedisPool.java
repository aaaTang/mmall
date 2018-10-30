package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {

    private static JedisPool pool;  //jedis连接池
    private static Integer maxTotal=Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));   //最大连接数
    private static Integer maxIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idel","10"));    //在jedispool中最大的空闲状态的jedis实例的个数；
    private static Integer minIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idel","2"));    //在jedispool中最小的空闲状态的jedis实例的个数；

    private static boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));  //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true，则得到的jedis实例肯定是可以用的；
    private static boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));  //在return一个jedis实例的时候，是否要进行验证操作，如果赋值true，则放回jedispool的jedis实例肯定是可以用的；

    private static String ip=PropertiesUtil.getProperty("redis.ip","127.0.0.1");
    private static Integer port=Integer.parseInt(PropertiesUtil.getProperty("redis.port","6379"));

    private static void initPool(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);  //链接耗尽的时候，是否阻塞。false会抛出异常，true阻塞直到超时，默认为true；

        pool=new JedisPool(config,ip,port,1000*2);
    }

    static{
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis){
            pool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis){
            pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis=pool.getResource();
        jedis.set("tang1","test1");
        returnResource(jedis);

        pool.destroy();
        System.out.println("program is end");
    }

}
