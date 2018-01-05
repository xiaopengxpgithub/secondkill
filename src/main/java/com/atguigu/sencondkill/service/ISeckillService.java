package com.atguigu.sencondkill.service;

import com.atguigu.sencondkill.dto.Exposer;
import com.atguigu.sencondkill.dto.SeckillExcetion;
import com.atguigu.sencondkill.exception.RepeatKillException;
import com.atguigu.sencondkill.exception.SeckillClosedException;
import com.atguigu.sencondkill.exception.SeckillException;
import com.atguigu.sencondkill.pojo.Seckill;

import java.util.List;

/**
 * 业务接口
 */
public interface ISeckillService {

    /**
     * 查询所有的秒杀(商品)记录
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(Long seckillId);

    /**
     * 查询秒杀商品的秒杀地址
     * 如果秒杀开始,则输出秒杀接口地址让用户参与秒杀;
     * 如果没有开始,则输出秒杀的时间(开始,结束)
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(Long seckillId);

    /**
     * 执行秒杀操作 -- 简单优化
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExcetion executeSeckill(Long seckillId, Long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillClosedException;

    /**
     * 执行秒杀操作 -- 深入优化(存储过程)
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExcetion executeSeckillProcedure(Long seckillId, Long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillClosedException;


}
