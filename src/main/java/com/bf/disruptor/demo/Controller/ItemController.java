package com.bf.disruptor.demo.Controller;

import com.bf.core.util.ToolUtil;
import com.bf.disruptor.demo.ItemEventProducer;
import com.bf.disruptor.demo.RequestDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

/**
 * Disruptor 测试 crontroller
 * Created by bf on 2017/9/17.
 */
@Controller
@RequestMapping
public class ItemController {

    // 已经被 spring 管理的 bean
    private ItemEventProducer itemEventProducer;

    /**
     *  使用 Disruptor 完成下单
     */
    @RequestMapping(value = {"/item"})
    public void item(@RequestParam("itemId") String itemId, @RequestParam("num") int num){
        RequestDto requestDto = new RequestDto(getUser(), itemId, num);
        itemEventProducer.onData(requestDto);
    }


    /**
     * 假的，获得当前用户名的方法
     */
    private String getUser() {
        return ToolUtil.getRandomString(5);
    }
}
