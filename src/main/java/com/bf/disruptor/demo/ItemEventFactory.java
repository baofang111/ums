package com.bf.disruptor.demo;

import com.lmax.disruptor.EventFactory;

/**
 * ItemEvent 事件工厂
 * Created by bf on 2017/9/17.
 */
public class ItemEventFactory implements EventFactory<ItemEvent>{

    @Override
    public ItemEvent newInstance() {
        return new ItemEvent();
    }
}
