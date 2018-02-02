package com.redis.util;

import java.util.List;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Slowlog;

public class RedisUtil {
	private static JedisPool jedisPool;

	/**
	 * �������ӳ� ��ʵ������һ������ò���ȱ��ȡ������
	 * 
	 */
	private static void createJedisPool() {

		// �������ӳ����ò���
		JedisPoolConfig config = new JedisPoolConfig();

		// ���ÿռ�����
		config.setMaxIdle(10);

		// �������ӳ�
		jedisPool = new JedisPool(config, "127.0.0.1", 6379);

	}

	/**
	 * �ڶ��̻߳���ͬ����ʼ��
	 */
	private static synchronized void poolInit() {
		if (jedisPool == null)
			createJedisPool();
	}

	/**
	 * ��ȡһ��jedis ����
	 * 
	 * @return
	 */
	static {
		if (jedisPool == null)
			poolInit();

	}

	/**
	 * �黹һ������
	 * 
	 * @param jedis
	 */
	public static void returnRes(Jedis jedis) {
		jedisPool.returnResource(jedis);
	}

	/////////////

	// ��ȡredis ��������Ϣ
	public String getRedisInfo() {
		if (jedisPool == null)
			poolInit();
		
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Client client = jedis.getClient();
			client.info();
			String info = client.getBulkReply();
			return info;
		} finally {
			// ���������ӳ�
			if(null != jedis)
			jedis.close();
		}
	}

	// ��ȡ��־�б�
	public List<Slowlog> getLogs(long entries) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			List<Slowlog> logList = jedis.slowlogGet(entries);
			return logList;
		} finally {
			// ���������ӳ�
			jedis.close();
		}
	}

	// ��ȡ��־����
	public Long getLogsLen() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long logLen = jedis.slowlogLen();
			return logLen;
		} finally {
			// ���������ӳ�
			jedis.close();
		}
	}

	// �����־
	public String logEmpty() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.slowlogReset();
		} finally {
			// ���������ӳ�
			jedis.close();
		}
	}

	// ��ȡռ���ڴ��С
	public Long dbSize() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			// TODO ����redis������Ϣ
			Client client = jedis.getClient();
			client.dbSize();
			return client.getIntegerReply();
		} finally {
			// ���������ӳ�
			jedis.close();
		}
	}

	public static void main(String[] args) {
		RedisUtil util = new RedisUtil();
		System.out.println(util.getRedisInfo());

	}

}
