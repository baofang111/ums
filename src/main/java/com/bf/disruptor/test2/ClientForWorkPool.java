package com.bf.disruptor.test2;

import com.lmax.disruptor.*;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  直接使用 WorkPool 的处理方式
 *      WorkPool -- 可处理多消费者
 * Created by bf on 2017/9/17.
 */
public class ClientForWorkPool {
    public static void main(String[] args) throws InterruptedException {  
        int BUFFER_SIZE = 1024;
        int THREAD_NUMBERS = 4;
        
        RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
            public Trade newInstance() {  
                return new Trade(UUID.randomUUID().toString());
            }  
        }, BUFFER_SIZE);  
       
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        
        // 第三个参数: 异常处理器, 这里用ExceptionHandler; 第四个参数WorkHandler的实现类, 可为数组(即传入多个消费者)
        WorkerPool<Trade> workerPool = new WorkerPool<Trade>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), new TradeHandler());
          
        ExecutorService executors = Executors.newFixedThreadPool(THREAD_NUMBERS);
        workerPool.start(executors);  
          
        // 生产10个数据
        for (int i = 0; i < 8; i++) {
            long seq = ringBuffer.next();
            ringBuffer.get(seq).setPrice(Math.random() * 9999);
            ringBuffer.publish(seq);
        }
          
        Thread.sleep(1000);  
        workerPool.halt();  
        executors.shutdown();  
    }  
}