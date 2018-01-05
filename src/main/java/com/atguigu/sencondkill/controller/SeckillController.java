package com.atguigu.sencondkill.controller;

import com.atguigu.sencondkill.common.SeckillStateEnum;
import com.atguigu.sencondkill.dto.Exposer;
import com.atguigu.sencondkill.dto.SeckillExcetion;
import com.atguigu.sencondkill.dto.SeckillResult;
import com.atguigu.sencondkill.exception.RepeatKillException;
import com.atguigu.sencondkill.exception.SeckillClosedException;
import com.atguigu.sencondkill.exception.SeckillException;
import com.atguigu.sencondkill.pojo.Seckill;
import com.atguigu.sencondkill.service.ISeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/seckill")
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private ISeckillService iSeckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Map<String, Object> map) {
        //获取列表页
        List<Seckill> list = iSeckillService.getSeckillList();
        map.put("list", list);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Map<String, Object> map) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = iSeckillService.getSeckillById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        map.put("seckill", seckill);
        return "detail";
    }

    @ResponseBody
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=utf-8"})
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> seckillResult;
        try {
            Exposer exposer = iSeckillService.exportSeckillUrl(seckillId);
            seckillResult = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            seckillResult = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return seckillResult;
    }

    @ResponseBody
    @RequestMapping(value = "/{seckillId}/{md5}/execteSeckill",
            method = RequestMethod.POST,
            produces = {"application/json;charset=utf-8"})
    public SeckillResult<SeckillExcetion> execteSeckill(@PathVariable("seckillId") Long seckillId,
                                                        @PathVariable("md5") String md5,
                                                        @CookieValue(value = "killPhone", required = false) Long userPhone) {
        if (userPhone == null) {
            return new SeckillResult<SeckillExcetion>(false, "未登录");
        }

        SeckillResult<SeckillExcetion> seckillResult;
        try {
            // 调用存储过程执行秒杀
            SeckillExcetion seckillExcetion = iSeckillService.executeSeckillProcedure(seckillId, userPhone, md5);
            return new SeckillResult<SeckillExcetion>(true, seckillExcetion);
        } catch (RepeatKillException e1) {
            SeckillExcetion seckillExcetion=new SeckillExcetion(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExcetion>(true,seckillExcetion);
        } catch (SeckillClosedException e2) {
            SeckillExcetion seckillExcetion=new SeckillExcetion(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExcetion>(true,seckillExcetion);
        } catch (SeckillException e3) {
            SeckillExcetion seckillExcetion=new SeckillExcetion(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExcetion>(true,seckillExcetion);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExcetion seckillExcetion=new SeckillExcetion(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExcetion>(true,seckillExcetion);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    public SeckillResult<Long> sysTime(){
        return new SeckillResult<Long>(true,new Date().getTime());
    }
}
