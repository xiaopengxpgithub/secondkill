package com.atguigu.sencondkill.mapper;

import com.atguigu.sencondkill.pojo.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKilledMapper {

    /**
     * 插入秒杀成功记录明细
     * @param successKilled
     * @return
     */
    int insertSuccessKill(SuccessKilled successKilled);

    /**
     * 查询秒杀成功的明细
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdAndUserPhone(@Param("seckillId") Long seckillId,@Param("userPhone") Long userPhone);
}
