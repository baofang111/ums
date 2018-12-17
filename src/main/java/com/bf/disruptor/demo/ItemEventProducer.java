package com.bf.disruptor.demo;

import com.lmax.disruptor.RingBuffer;

/**
 * Disruptor itemEvent 事件生产者
 * Created by bf on 2017/9/17.
 */
public class ItemEventProducer {

    // 生产者必须持有 ringbuffer 的引用
    private final RingBuffer<ItemEvent> ringBuffer;

    public ItemEventProducer(RingBuffer<ItemEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(RequestDto event){
        long sequebce = ringBuffer.next();
        try {
            ItemEvent itemEvent = ringBuffer.get(sequebce);
            itemEvent.setRequestDto(event);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 通知消费者
            ringBuffer.publish(sequebce);
        }
    }

}
