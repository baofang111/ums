package com.bf.disruptor.test1;

import com.lmax.disruptor.EventFactory;

/**
 *  disruptor 事件工厂，需实现 EventFactory （需要指定 event 的类型）
 *    Disreptor 通过事件工厂，在 RingBuffer 中创建 Event
 * Created by bf on 2017/9/17.
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
