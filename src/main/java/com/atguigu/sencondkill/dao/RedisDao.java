package com.atguigu.sencondkill.dao;

import com.atguigu.sencondkill.pojo.Seckill;

public interface RedisDao {

    public String setSeckill(Seckill seckill);

    public Seckill getSeckill(Long seckillId);
}
