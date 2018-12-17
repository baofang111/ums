package com.bf.disruptor.test1;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Disruptor 事件的生成者
 *  往 RingBuffer 中添加 Event 事件
 * Created by bf on 2017/9/17.
 */
public class LongEventProducer {

    // 生产者持有 RingBuffer 的引用
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb){
        long sequence = ringBuffer.next();
        try{
            LongEvent longEvent = ringBuffer.get(sequence);
            longEvent.setValue(bb.getLong(0));
        }finally {
            // 发布 Event ，将 sequence 传递给消费者，此步骤必须放在 finally 快中执行，保证发布一定成功
            ringBuffer.publish(sequence);
        }
    }

}
