package com.atguigu.sencondkill.common;

/**
 * 使用枚举表示常量字段
 */
public enum SeckillStateEnum {
    SUCCESS(1,"恭喜你,秒杀成功了!"),
    END(0,"秒杀已经结束了!"),
    REPEAT_KILL(-1,"重复秒杀,秒杀失败!"),
    INNER_ERROR(-2,"系统异常,秒杀失败!"),
    DATE_REWRITE(-3,"数据篡改,再恶意请求就报警了!");

    private int state;

    private String stateinfo;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateinfo() {
        return stateinfo;
    }

    public void setStateinfo(String stateinfo) {
        this.stateinfo = stateinfo;
    }

    SeckillStateEnum(int state, String stateinfo) {
        this.state = state;
        this.stateinfo = stateinfo;
    }

    public static SeckillStateEnum stateOf(int state){
        for (SeckillStateEnum stateEnum:values()){
            if (stateEnum.getState()==state){
                return stateEnum;
            }
        }
        return null;
    }
}
