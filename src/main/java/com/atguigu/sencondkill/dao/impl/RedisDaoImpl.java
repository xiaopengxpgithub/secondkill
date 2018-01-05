package com.atguigu.sencondkill.dao.impl;

import com.atguigu.sencondkill.dao.RedisDao;
import com.atguigu.sencondkill.pojo.Seckill;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisDaoImpl implements RedisDao {

    @Autowired
    private JedisPool jedisPool;

    //自定义对象序列化
    private RuntimeSchema<Seckill> scheme = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(Long seckillId) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    // 创建一个空对象
                    Seckill seckill = scheme.newMessage();
                    //对象反序列化
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, scheme);
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String setSeckill(Seckill seckill) {
        // 将对象转换成字节数组,存储到redis中
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, scheme, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeOut = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeOut, bytes);

                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
