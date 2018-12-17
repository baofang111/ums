package com.bf.disruptor.test2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 *  Event 消费者
 * Created by bf on 2017/9/17.
 */
public class TradeHandler implements EventHandler<Trade>, WorkHandler<Trade> {

    @Override
    public void onEvent(Trade event) throws Exception {
        //TODO 处理消费逻辑
        System.out.println("tarde消费: " + event.getId());
    }

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }
}
