package com.mmall.common;

import com.google.common.collect.Lists;
import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {

    private static ShardedJedisPool pool;  //jedis连接池
    private static Integer maxTotal=Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));   //最大连接数
    private static Integer maxIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idel","10"));    //在jedispool中最大的空闲状态的jedis实例的个数；
    private static Integer minIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idel","2"));    //在jedispool中最小的空闲状态的jedis实例的个数；

    private static boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));  //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true，则得到的jedis实例肯定是可以用的；
    private static boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));  //在return一个jedis实例的时候，是否要进行验证操作，如果赋值true，则放回jedispool的jedis实例肯定是可以用的；

    private static String redis1Ip=PropertiesUtil.getProperty("redis1.ip","127.0.0.1");
    private static Integer redis1Port=Integer.parseInt(PropertiesUtil.getProperty("redis1.port","6379"));

    private static String redis2Ip=PropertiesUtil.getProperty("redis2.ip","127.0.0.1");
    private static Integer redis2Port=Integer.parseInt(PropertiesUtil.getProperty("redis2.port","6380"));

    private static void initPool(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);  //链接耗尽的时候，是否阻塞。false会抛出异常，true阻塞直到超时，默认为true；

        JedisShardInfo info1=new JedisShardInfo(redis1Ip,redis1Port,1000*2);

        JedisShardInfo info2=new JedisShardInfo(redis2Ip,redis2Port,1000*2);

        List<JedisShardInfo> jedisShardInfoList= new ArrayList<JedisShardInfo>(2);

        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        //ShardedJedisPool中第三个参数Hashing.MURMUR_HASH即表示使用一致性哈希算法
        pool=new ShardedJedisPool(config,jedisShardInfoList,Hashing.MURMUR_HASH,Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis=pool.getResource();
        for (int i=0;i<10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);

        //pool.destroy(); //临时调用销毁连接池所有连接；
        System.out.println("program is end");
    }
}
