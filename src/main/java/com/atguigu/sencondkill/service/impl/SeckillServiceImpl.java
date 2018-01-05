package com.atguigu.sencondkill.service.impl;

import com.atguigu.sencondkill.common.SeckillStateEnum;
import com.atguigu.sencondkill.dao.RedisDao;
import com.atguigu.sencondkill.dto.Exposer;
import com.atguigu.sencondkill.dto.SeckillExcetion;
import com.atguigu.sencondkill.exception.RepeatKillException;
import com.atguigu.sencondkill.exception.SeckillClosedException;
import com.atguigu.sencondkill.exception.SeckillException;
import com.atguigu.sencondkill.mapper.SeckillMapper;
import com.atguigu.sencondkill.mapper.SuccessKilledMapper;
import com.atguigu.sencondkill.pojo.Seckill;
import com.atguigu.sencondkill.pojo.SuccessKilled;
import com.atguigu.sencondkill.service.ISeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements ISeckillService {

    private Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串,用于混淆md5字符串
    private final String slat = "fdafahfeorfheioafhd,a##%$%&&152489hfdjkfa!";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillMapper.getSeckillList(0, 4);
    }

    @Override
    public Seckill getSeckillById(Long seckillId) {
        return seckillMapper.getSeckillById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(Long seckillId) {
        //缓存优化
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 缓存中没有,查询数据库
            seckill = seckillMapper.getSeckillById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                // 放入redis
                redisDao.setSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();

        //系统当前时间
        Date nowTime = new Date();

        // 秒杀未开启/关闭
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        // 秒杀开启...
        String md5 = this.getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    public String getMD5(Long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    public SeckillExcetion executeSeckill(Long seckillId, Long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillClosedException {
        if (md5 == null || !md5.equals(this.getMD5(seckillId))) {
            throw new SeckillException("sckill data rewrite");
        }

        Date now = new Date();

        try {
            //记录购买行为
            SuccessKilled successKilled = new SuccessKilled();
            successKilled.setUserPhone(userPhone);
            successKilled.setSeckillId(seckillId);
            successKilled.setCreateTime(new Date());
            int insertCount = successKilledMapper.insertSuccessKill(successKilled);
            if (insertCount <= 0) {
                // 重复秒杀
                throw new RepeatKillException("Seckill repeat");
            } else {
                // 减库存,热点商品竞争
                int updateCount = seckillMapper.reduceNumber(seckillId, now);
                if (updateCount <= 0) {
                    //没有更新到数据库,秒杀结束
                    throw new SeckillClosedException("seckill is closed");
                } else {
                    // 秒杀成功
                    successKilled = successKilledMapper.queryByIdAndUserPhone(seckillId, userPhone);
                    return new SeckillExcetion(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillClosedException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译期异常,转换成运行时异常,被spring声明式事务捕获
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public SeckillExcetion executeSeckillProcedure(Long seckillId, Long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillClosedException {
        if (md5 == null || !md5.equals(this.getMD5(seckillId))) {
            return new SeckillExcetion(seckillId, SeckillStateEnum.DATE_REWRITE);
        }

        Date date = new Date();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("seckillId", seckillId);
        paramMap.put("userPhone", userPhone);
        paramMap.put("nowTime", date);
        paramMap.put("result", null);
        try {
            seckillMapper.seckillByProcedure(paramMap);

            // 获取map中的result
            int result = MapUtils.getInteger(paramMap, "result");

            if (result == 1) {
                //秒杀成功
                SuccessKilled successKilled = successKilledMapper.queryByIdAndUserPhone(seckillId, userPhone);
                return new SeckillExcetion(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExcetion(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExcetion(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
