package com.bf.disruptor.test3;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Disruptor 多生产者 多消费者模式
 * Created by bf on 2017/9/17.
 */
public class Test3Demo {


    public static void main(String[] args)throws Exception {
        //创建RingBuffer
        RingBuffer<Order> ringBuffer =
                RingBuffer.create(ProducerType.MULTI,
                        new EventFactory<Order>() {
                            @Override
                            public Order newInstance() {
                                return new Order();
                            }
                        },
                        1024 * 1024, new YieldingWaitStrategy());

        // SequenceBarrier, 协调生产者和消费者，消费者链的先后是顺序，阻塞后面的消费者（当没有Event可以消费的时候）
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        // 创建多个消费者
        OrderCustomer[] customers = new OrderCustomer[3];
        for (int i = 0; i < customers.length; i++){
            customers[i] = new OrderCustomer(" customer_" + i);
        }

        // workPool 可处理消费者
        WorkerPool<Order> workerPool = new WorkerPool<Order>(ringBuffer, sequenceBarrier, new MyExceptionHandler(), customers);

        // 把消费者的消费进度情况注册给RingBuffer结构(生产者)    !如果只有一个消费者的情况可以省略
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(8);

        // 将线程池放入 WorkerPool
        workerPool.start(executor);

        // 10个生产者，每个生产者生产20条数据
        for (int i = 0; i < 10; i++) {
            final OrderProducer producer = new OrderProducer(ringBuffer);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j = 0; j < 20; j++){
                        producer.onData(UUID.randomUUID().toString());
                    }
                }
            }).start();
        }

        System.out.println("-------开始生产-------");
        // 等待消费完成
        Thread.sleep(1000);

        System.out.println("总共消费数量："+ customers[0].getCount());

        workerPool.halt();

        executor.shutdown();
    }
}
