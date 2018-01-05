package com.atguigu.sencondkill.service.impl;

import com.atguigu.sencondkill.mapper.SuccessKilledMapper;
import com.atguigu.sencondkill.pojo.SuccessKilled;
import com.atguigu.sencondkill.service.ISuccessKilledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuccessKilledServiceImpl implements ISuccessKilledService {

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Override
    public int insertSuccessKill(SuccessKilled successKilled) {
        return successKilledMapper.insertSuccessKill(successKilled);
    }

    @Override
    public SuccessKilled queryByIdAndUserPhone(Long seckillId,Long userPhone) {
        return successKilledMapper.queryByIdAndUserPhone(seckillId,userPhone);
    }
}
