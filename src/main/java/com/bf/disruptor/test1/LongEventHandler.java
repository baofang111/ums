package com.bf.disruptor.test1;

import com.lmax.disruptor.EventHandler;

/**
 * Disruptor 的事件消费者，需实现EvnetHandler。
 *  从RingBuffer 中消费Event
 *  消费逻辑自行定义
 * Created by bf on 2017/9/17.
 */
public class LongEventHandler implements EventHandler<LongEvent>{
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        //TODO 处理事件的消费逻辑
        System.out.println("消费: " + event.getValue());
    }
}
