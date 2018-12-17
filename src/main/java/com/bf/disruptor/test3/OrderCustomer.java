package com.bf.disruptor.test3;

import com.lmax.disruptor.WorkHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  消费者
 * Created by bf on 2017/9/17.
 */
public class OrderCustomer implements WorkHandler<Order>{


    private String customerId;

    // 消费计数器 -- AtomicInteger 线程安全
    private static AtomicInteger count = new AtomicInteger(0);

    public OrderCustomer(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public void onEvent(Order event) throws Exception {
        System.out.println("当前消费者：" + this.customerId + ", 消费信息：" + event.getId());
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}
