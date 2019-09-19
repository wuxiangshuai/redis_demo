package com.wxs.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @ClassName: redisService
 * @Author: WuXiangShuai
 * @Time: 13:32 2019/8/26.
 * @Description:
 */
@Service
public class redisService {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        System.out.println(jedis.ping());
        jedis.close();
    }

    public static boolean doSecKill(String uid, String prodid) {
        String qtKey = "sk:" + prodid + ":qt";
        String usrKey = "sk:" + prodid + ":usr";

//        Jedis jedis = new Jedis("127.0.0.1", 6379);
        // 使用连接池获取
        JedisPool pool = JedisPoolUtil.getJedisPoolInstance();
        Jedis jedis = pool.getResource();
        System.out.println("活跃数：" + pool.getNumActive() + " - 等待数：" + pool.getNumWaiters());

        // 判断是否秒到
        if (jedis.sismember(usrKey, uid)) {
            System.err.println("不能重复秒杀");
            jedis.close();
            return false;
        }
        // 判断是否初始化
        jedis.watch(qtKey); // 锁
        String qtStr = jedis.get(qtKey);
        if (null == qtStr) {
            System.err.println("未初始化");
            jedis.close();
            return false;
        }
        // 判断库存
        int qtInt = Integer.parseInt(qtStr);
        if (qtInt <= 0) {
            System.out.println("已秒光");
            jedis.close();
            return false;
        }
        Transaction multi = jedis.multi();// 事务
        // 减库存
//        jedis.decr(qtKey);
        multi.decr(qtKey);
        // 加人
//        jedis.sadd(usrKey, uid);
        multi.sadd(usrKey, uid);
        List<Object> exec = multi.exec();
        if (null == exec || 0 == exec.size()) {
            System.err.println("秒杀失败");
            jedis.close();
            return false;
        }
        System.out.println("秒杀成功");
        jedis.close();
        return true;
    }
}
