package com.redis.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.redis.entity.Operate;
import com.redis.entity.RedisInfoDetail;
import com.redis.util.DateUtil;
import com.redis.util.RedisUtil;
import com.redis.util.Tools;

import redis.clients.util.Slowlog;

public class RedisService {

	public RedisUtil redisUtil = new RedisUtil();

	public List<RedisInfoDetail> getRedisInfo() {
		// ��ȡredis��������Ϣ
		String info = redisUtil.getRedisInfo();
		List<RedisInfoDetail> ridList = new ArrayList<RedisInfoDetail>();
		String[] strs = info.split("\n");
		RedisInfoDetail rif = null;
		if (strs != null && strs.length > 0) {
			for (int i = 0; i < strs.length; i++) {
				rif = new RedisInfoDetail();
				String s = strs[i];
				String[] str = s.split(":");
				if (str != null && str.length > 1) {
					String key = str[0];
					String value = str[1];
					rif.setKey(key);
					rif.setValue(value);
					ridList.add(rif);
				}
			}
		}
		return ridList;
	}

	public RedisInfoDetail getRedisInfo(String redisInfo, String index) {
		// ��ȡredis��������Ϣ
		String info = redisUtil.getRedisInfo();
		List<RedisInfoDetail> ridList = new ArrayList<RedisInfoDetail>();
		String[] strs = info.split("\n");
		RedisInfoDetail rif = null;
		if (strs != null && strs.length > 0) {
			for (int i = 0; i < strs.length; i++) {
				rif = new RedisInfoDetail();

				String s = strs[i];
				String[] str = s.split(":");
				if (str != null && str.length > 1 && index.contains(str[0])) {
					String key = str[0];
					String value = str[1].trim();
					rif.setKey(key);
					//�� Redis ������������ڴ����������ֽڣ�byte��Ϊ��λ��ת��ΪM 
					rif.setValue(Tools.setMBSize(Integer.valueOf(value)));
					rif.setDate(DateUtil.getCurrntTime());

					ridList.add(rif);
					return rif;
				}
			}
		}
		return rif;
	}

	public RedisInfoDetail getKeysValue(String redisInfo) {
		// ��ȡredis��������Ϣ
		String info = redisUtil.getRedisInfo();
		List<RedisInfoDetail> ridList = new ArrayList<RedisInfoDetail>();
		String[] strs = info.split("\n");
		RedisInfoDetail rif = new RedisInfoDetail();
		String keys = "keys";
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("keys", 0);
		
		if (strs != null && strs.length > 0) {
			for (int i = 0; i < strs.length; i++) {
				String s = strs[i];

				if (s.indexOf(":keys") > -1) {
					String[] str = s.split(",");
					if (null != str) {
						String[] dbs = str[0].split(":");
						String[] dbKeys = dbs[1].split("=");
						String key = dbKeys[0];
						Integer value = Integer.valueOf(dbKeys[1]);

						Integer keysVal = map.get("keys");
						if (null == keysVal) {
							map.put("keys", value);
						} else {
							map.put("keys", keysVal + value);
						}

						rif.setKey(key);
						rif.setValue(map.get("keys") + "");
						rif.setDate(DateUtil.getCurrntTime());
					}

				}else{
					rif.setKey("keys");
					rif.setValue(map.get("keys") + "");
					rif.setDate(DateUtil.getCurrntTime());
				}
				
				
				

			}
		} 
		return rif;
	}

	// ��ȡredis��־�б�
	public List<Operate> getLogs(long entries) {
		List<Slowlog> list = redisUtil.getLogs(entries);
		List<Operate> opList = null;
		Operate op = null;
		boolean flag = false;
		if (list != null && list.size() > 0) {
			opList = new LinkedList<Operate>();
			for (Slowlog sl : list) {
				String args = JSON.toJSONString(sl.getArgs());
				if (args.equals("[\"PING\"]") || args.equals("[\"SLOWLOG\",\"get\"]") || args.equals("[\"DBSIZE\"]")
						|| args.equals("[\"INFO\"]")) {
					continue;
				}
				op = new Operate();
				flag = true;
				op.setId(sl.getId());
				op.setExecuteTime(getDateStr(sl.getTimeStamp() * 1000));
				op.setUsedTime(sl.getExecutionTime() / 1000.0 + "ms");
				op.setArgs(args);
				opList.add(op);
			}
		}
		if (flag)
			return opList;
		else
			return null;
	}

	// ��ȡ��־����
	public Long getLogLen() {
		return redisUtil.getLogsLen();
	}

	// �����־
	public String logEmpty() {
		return redisUtil.logEmpty();
	}

	// ��ȡ��ǰ���ݿ���key������
	public Map<String, Object> getKeysSize() {
		long dbSize = redisUtil.dbSize();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("create_time", new Date().getTime());
		map.put("dbSize", dbSize);
		return map;
	}

	// ��ȡ��ǰredisʹ���ڴ��С���
	public Map<String, Object> getMemeryInfo() {
		String[] strs = redisUtil.getRedisInfo().split("\n");
		Map<String, Object> map = null;
		for (int i = 0; i < strs.length; i++) {
			String s = strs[i];
			String[] detail = s.split(":");
			if (detail[0].equals("used_memory")) {
				map = new HashMap<String, Object>();
				map.put("used_memory", detail[1].substring(0, detail[1].length() - 1));
				map.put("create_time", new Date().getTime());
				break;
			}
		}
		return map;
	}

	private String getDateStr(long timeStmp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date(timeStmp));
	}

	public static void main(String[] args) {
		RedisService service = new RedisService();
		List list = service.getRedisInfo();
		System.out.println(list);
	}

}
