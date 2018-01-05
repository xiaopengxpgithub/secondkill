package com.atguigu.sencondkill.mapper;

import com.atguigu.sencondkill.pojo.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillMapper {

    /***
     * 减库存
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") Long seckillId, @Param("killTime") Date killTime);

    /**
     * 查询秒杀对象
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(Long seckillId);

    /**
     * 查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> getSeckillList(@Param("offset") int offset,@Param("limit") int limit);

    /**
     * 调用存储过程执行秒杀
     * @param paraMap
     */
    void seckillByProcedure(Map<String,Object> paraMap);
}
