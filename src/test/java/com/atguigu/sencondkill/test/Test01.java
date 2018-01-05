package com.atguigu.sencondkill.test;

import com.atguigu.sencondkill.dao.RedisDao;
import com.atguigu.sencondkill.dto.Exposer;
import com.atguigu.sencondkill.dto.SeckillExcetion;
import com.atguigu.sencondkill.mapper.SeckillMapper;
import com.atguigu.sencondkill.pojo.Seckill;
import com.atguigu.sencondkill.service.ISeckillService;
import com.sun.org.apache.regexp.internal.RE;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test01 {

    public ApplicationContext ctx;
    public RedisDao redisDao;
    public SeckillMapper seckillMapper;

    public ISeckillService iSeckillService;

    @Before
    public void initProperties() {
        ctx = new ClassPathXmlApplicationContext("applicationContext-base.xml", "applicationContext-redis.xml");
        redisDao = (RedisDao) ctx.getBean(RedisDao.class);
        seckillMapper = ctx.getBean(SeckillMapper.class);
        iSeckillService = ctx.getBean(ISeckillService.class);
    }

    @Test
    public void test02() {
        Long seckillId = 1003L;
        Long userPhone = 12345678923L;
        Exposer exposer=iSeckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            String md5 = exposer.getMd5();

            SeckillExcetion excetion=iSeckillService.executeSeckillProcedure(seckillId,userPhone,md5);
            System.out.println(excetion.getStateInfo());
        }
    }

    @Test
    public void test01() {
        Seckill seckill = redisDao.getSeckill(1000L);
        if (seckill == null) {
            seckill = seckillMapper.getSeckillById(1000L);
            if (seckill != null) {
                String result = redisDao.setSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(seckill.getSeckillId());
                System.out.println(seckill);
            }
        }
    }
}
