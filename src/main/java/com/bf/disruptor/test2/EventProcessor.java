package com.bf.disruptor.test2;

import com.lmax.disruptor.*;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  直接使用 RingBuffer 的处理方式
 * Created by bf on 2017/9/17.
 */
public class EventProcessor {

    public static void main(String[] args) throws Exception {
        long time1 = System.currentTimeMillis();

        // RingBuffer 大小
        int ringSize = 1024 * 1024;

        RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade(UUID.randomUUID().toString());
            }
        }, ringSize, new YieldingWaitStrategy());

        // SequenceBarrier, 协调生产者和消费者，消费者链的先后是顺序，阻塞后面的消费者（当没有Event可以消费的时候）
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        // 创建事件处理器 (消费者): 处理ringBuffer, 用TradeHandler的方法处理(实现EventHandler), 用sequenceBarrier协调生成-消费
        // 如果存在多个消费者(老api, 可用workpool解决) 那重复执行 创建事件处理器-注册进度-提交消费者的过程, 把其中TradeHandler换成其它消费者类
        BatchEventProcessor<Trade> transProcessor = new BatchEventProcessor<Trade>(ringBuffer, sequenceBarrier, new TradeHandler());
        // 把消费者的消费进度情况注册给RingBuffer结构(生产者)    !如果只有一个消费者的情况可以省略
        ringBuffer.addGatingSequences(transProcessor.getSequence());

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(6);

        // 把消费者提交给线程池
        executor.submit(transProcessor);

        // 创建生成者
        executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                long seq;
                for (int i = 0; i < 10; i++) {
                    seq = ringBuffer.next();
                    ringBuffer.get(seq).setPrice(Math.random() * 9999);
                    ringBuffer.publish(seq);
                }
                return null;
            }
        });

        // 等待一秒，待消费者全部处理完
        Thread.sleep(1000);
        // 通知事件处理器，可以结束了
        transProcessor.halt();

        executor.shutdown();

        System.out.println("总耗时: " + (System.currentTimeMillis() - time1));

    }

}
