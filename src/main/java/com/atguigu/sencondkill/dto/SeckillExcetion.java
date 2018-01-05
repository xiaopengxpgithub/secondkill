package com.atguigu.sencondkill.dto;

import com.atguigu.sencondkill.common.SeckillStateEnum;
import com.atguigu.sencondkill.pojo.SuccessKilled;

/**
 * 封装秒杀执行后的结果
 */
public class SeckillExcetion {

    private Long seckillId;

    // 秒杀执行结果状态
    private int state;

    //状态展示
    private String stateInfo;

    // 秒杀成功对象
    private SuccessKilled successKilled;

    public SeckillExcetion(Long seckillId, SeckillStateEnum seckillStateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateinfo();
        this.successKilled = successKilled;
    }

    public SeckillExcetion(Long seckillId, SeckillStateEnum seckillStateEnum) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateinfo();
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExcetion{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
