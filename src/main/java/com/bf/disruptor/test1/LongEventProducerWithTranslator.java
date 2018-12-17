package com.bf.disruptor.test1;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Disruptor 生成者 实现2
 * Created by bf on 2017/9/17.
 */
public class LongEventProducerWithTranslator {

    // 使用EventTranslator, 封装 获取Event的过程
    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR = new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
        @Override
        public void translateTo(LongEvent event, long sequeue, ByteBuffer buffer) {
            event.setValue(buffer.getLong(0));
        }
    };

    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer buffer){
        // 发布
        ringBuffer.publishEvent(TRANSLATOR, buffer);
    }

}
