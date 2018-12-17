package com.bf.disruptor.test1;

/**
 * disruptor 用于交换数据的 数据event
 * Created by bf on 2017/9/17.
 */
public class LongEvent {

    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
