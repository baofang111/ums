package com.bf.disruptor.test1;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *  Disruptor 测试 main
 *
 *  参考 Disruptor 快速入门 http://www.cnblogs.com/myJavaEE/p/6790917.html
  Created by bf on 2017/9/17.
 */
public class Test1Demo {

    public static void main(String[] args) {

        long time1 = System.currentTimeMillis();

        //  获取线程池
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        // ExecutorService threadFactory = Executors.newCachedThreadPool();


        // EventFactory Event事件工厂
        LongEventFactory eventFactory = new LongEventFactory();

        // 设置 RingBuffer 的大小，最好是 2 的n次方，提高效率
        int size = 1024 * 1024;

        // 创建 disruptor 对象
        // threadFactory 线程池
        // SINGLE 表明是单生产者 MULTI 多生产者
        // 第五个参数: WaitStrategy 当消费者阻塞在SequenceBarrier上, 消费者如何等待的策略.
        //BlockingWaitStrategy 使用锁和条件变量, 效率较低, 但CPU的消耗最小, 在不同部署环境下性能表现比较一致
        //SleepingWaitStrategy 多次循环尝试不成功后, 让出CPU, 等待下次调度; 多次调度后仍不成功, 睡眠纳秒级别的时间再尝试. 平衡了延迟和CPU资源占用, 但延迟不均匀.
        //YieldingWaitStrategy 多次循环尝试不成功后, 让出CPU, 等待下次调度. 平衡了延迟和CPU资源占用, 延迟也比较均匀.
        //BusySpinWaitStrategy 自旋等待，类似自旋锁. 低延迟但同时对CPU资源的占用也多.
        Disruptor<LongEvent> disruptor  = new Disruptor<LongEvent>(eventFactory, size, threadFactory, ProducerType.SINGLE, new YieldingWaitStrategy());

        // 向 disruptor 中添加 事件消费者
        EventHandlerGroup<LongEvent> handlerGroup = disruptor.handleEventsWith(new LongEventHandler());

        // 启动
        disruptor.start();

        // 将数据装入 RingBuffer 中
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        // 创建生成者 方式二 使用新 API
        // LongEventProducer eventProducer = new LongEventProducer(ringBuffer);

        LongEventProducerWithTranslator eventProducer = new LongEventProducerWithTranslator(ringBuffer);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8); // 这里只是笔者实验, 不是必须要用ByteBuffer保存long数据
        for(int i = 0; i < 100; ++i){
            byteBuffer.putLong(0, i);
            eventProducer.onData(byteBuffer);
        }

        // 关闭 Disruptor
        disruptor.shutdown();

        System.out.println("总耗时: " + (System.currentTimeMillis() - time1));

    }

}
