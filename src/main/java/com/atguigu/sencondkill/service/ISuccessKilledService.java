package com.atguigu.sencondkill.service;

import com.atguigu.sencondkill.pojo.SuccessKilled;

public interface ISuccessKilledService {
    /**
     * 插入秒杀成功记录明细
     * 对于同一件商品,一个用户只能秒杀一次
     * @param successKilled
     * @return
     */
    int insertSuccessKill(SuccessKilled successKilled);

    /**
     * 查询秒杀成功的明细
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdAndUserPhone(Long seckillId,Long userPhone);
}
