package com.bf.disruptor.test3;

import com.lmax.disruptor.RingBuffer;

/**
 *  生成者
 * Created by bf on 2017/9/17.
 */
public class OrderProducer {

    private final RingBuffer<Order> ringBuffer;

    public OrderProducer(RingBuffer<Order> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(String data){
        long sequence = ringBuffer.next();
        try {
            Order order = ringBuffer.get(sequence);
            order.setId(data);
        }finally {
            ringBuffer.publish(sequence);
        }
    }
}
