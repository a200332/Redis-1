package com.redis.test;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.redis.service.RedisService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Test {
	private static volatile JedisPool jedisPool = null;

    private Test() {
    }

    public static JedisPool getJedisPoolInstance() {
        if (null == jedisPool) {
            synchronized (Test.class) {   //����ʹ��˫�˼�����ģʽ
                if (null == jedisPool) {
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    //����һ��pool�ɷ�����ٸ�jedisʵ����ͨ��pool.getResource()����ȡ�������ֵΪ-1�����ʾ�����ƣ�
                    //���pool�Ѿ�������maxActive��jedisʵ�������ʱpool��״̬Ϊexhausted
                    poolConfig.setMaxIdle(32);  //����ʣ�����Ӹ��������С������ͻ����쳣
                    //��ʾ��borrowһ��jedisʵ��ʱ�����ĵȴ�ʱ�䣬��������ȴ�ʱ�䣬��ֱ����JedisConnectionException
                    //���һ��jedisʵ����ʱ���Ƿ������ӿ�����(ping()),���Ϊtrue����õ���jedisʵ�����ǿ��õ�
                    poolConfig.setTestOnBorrow(true); 
                    jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379);
                }
            }
        }
        return jedisPool;
    }
    /**
     * �ͷ�
     * @param jedisPool �ͷ��ĸ�����
     * @param jedis        ���ĸ�����
     */
    public static void release(JedisPool jedisPool,Jedis jedis){
        if(null != jedis){
            jedisPool.returnResourceObject(jedis);
        }
    }

    public static void main(String[] args) {
//    	JedisPool jedisPool =	Test.getJedisPoolInstance();
//    	Jedis jedis = jedisPool.getResource();
//    	System.out.println( jedis );
    	
    	RedisService redisService = new RedisService();
    	List keys = new ArrayList();
    	//redis �ڴ�ʵʱռ�����
    	keys.add("used_memory_human");
    	//redis key��ʵʱ����
    	keys.add("db");
    	System.out.println(  JSON.toJSONString(redisService.getRedisInfo(keys) ));

    	//    	RedisUtil redisUtil = new RedisUtil();
//    	redisUtil.getRedisInfo();
    	
    }

}
