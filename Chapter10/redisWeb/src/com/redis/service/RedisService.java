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
import com.redis.util.RedisUtil;

import redis.clients.util.Slowlog;

public class RedisService {

	public RedisUtil redisUtil = new RedisUtil();
	
	public List<RedisInfoDetail> getRedisInfo() {
        //��ȡredis��������Ϣ
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
	
	public RedisInfoDetail getRedisInfo(String oneKey) {
        //��ȡredis��������Ϣ
        String info = redisUtil.getRedisInfo();
        List<RedisInfoDetail> ridList = new ArrayList<RedisInfoDetail>();
        String[] strs = info.split("\n");
        RedisInfoDetail rif = null;
        if (strs != null && strs.length > 0) {
            for (int i = 0; i < strs.length; i++) {
                rif = new RedisInfoDetail();
                
                String s = strs[i];
                String[] str = s.split(":");
                if (str != null && str.length > 1 && str[0].equals(oneKey) ) {
                    String key = str[0];
                    String value = str[1];
                    rif.setKey(key);
                    rif.setValue(value);
                    ridList.add(rif);
                    return rif;
                }
            }
        }
        return rif;
    }	
	
	 //��ȡredis��־�б�
    public List<Operate> getLogs(long entries) {
        List<Slowlog> list = redisUtil.getLogs(entries);
        List<Operate> opList = null;
        Operate op  = null;
        boolean flag = false;
        if (list != null && list.size() > 0) {
            opList = new LinkedList<Operate>();
            for (Slowlog sl : list) {
                String args = JSON.toJSONString(sl.getArgs());
                if (args.equals("[\"PING\"]") || args.equals("[\"SLOWLOG\",\"get\"]") || args.equals("[\"DBSIZE\"]") || args.equals("[\"INFO\"]")) {
                    continue;
                }   
                op = new Operate();
                flag = true;
                op.setId(sl.getId());
                op.setExecuteTime(getDateStr(sl.getTimeStamp() * 1000));
                op.setUsedTime(sl.getExecutionTime()/1000.0 + "ms");
                op.setArgs(args);
                opList.add(op);
            }
        } 
        if (flag) 
            return opList;
        else 
            return null;
    }
    //��ȡ��־����
    public Long getLogLen() {
        return redisUtil.getLogsLen();
    }

    //�����־
    public String logEmpty() {
        return redisUtil.logEmpty();
    }
    //��ȡ��ǰ���ݿ���key������
    public Map<String,Object> getKeysSize() {
        long dbSize = redisUtil.dbSize();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("create_time", new Date().getTime());
        map.put("dbSize", dbSize);
        return map;
    }

    //��ȡ��ǰredisʹ���ڴ��С���
    public Map<String,Object> getMemeryInfo() {
        String[] strs = redisUtil.getRedisInfo().split("\n");
        Map<String, Object> map = null;
        for (int i = 0; i < strs.length; i++) {
            String s = strs[i];
            String[] detail = s.split(":");
            if (detail[0].equals("used_memory")) {
                map = new HashMap<String, Object>();
                map.put("used_memory",detail[1].substring(0, detail[1].length() - 1));
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
		// TODO Auto-generated method stub

	}

}
