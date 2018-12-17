package com.bf.disruptor.test4;

import com.bf.disruptor.test2.Trade;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) throws InterruptedException {  
       
        long beginTime=System.currentTimeMillis();  
        int bufferSize=1024;  
        ExecutorService executor= Executors.newFixedThreadPool(7);  // 注意: 线程数>=handler数+1

        Disruptor<Trade> disruptor = new Disruptor<Trade>(
                new EventFactory<Trade>() {
                    @Override
                    public Trade newInstance() {
                        return new Trade(UUID.randomUUID().toString());
                    }
                }, bufferSize, executor, ProducerType.SINGLE, new BusySpinWaitStrategy());
        // 菱形操作   
        /*
        // 创建消费者组(含H1,H2)   H1,H2并行执行无先后顺序
        EventHandlerGroup<Trade> handlerGroup = disruptor.handleEventsWith(new Handler1(), new Handler2());
        // C1,C2都完成后执行C3, 像JMS传递消息
        handlerGroup.then(new Handler3());
        */
        
        // 顺序操作
        /*
        disruptor.handleEventsWith(new Handler1()).handleEventsWith(new Handler2()).handleEventsWith(new Handler3());
        */
        
        // 六边形操作. H1, H4串行执行; H2, H5串行执行; 而H1,H4 与 H2,H5 并行执行
        Handler1 h1 = new Handler1();
        Handler2 h2 = new Handler2();
        Handler3 h3 = new Handler3();
        Handler4 h4 = new Handler4();
        Handler5 h5 = new Handler5();
        disruptor.handleEventsWith(h1, h2);
        disruptor.after(h1).handleEventsWith(h4);
        disruptor.after(h2).handleEventsWith(h5);
        disruptor.after(h4, h5).handleEventsWith(h3);
        
        disruptor.start();
        CountDownLatch latch = new CountDownLatch(1);

        // 启动生产线程
        executor.submit(new TradePublisher(disruptor,latch));
        // Thread.sleep(1000); // 等待消费完成

        // 等待生产者生产完成
        latch.await();

        disruptor.shutdown();  
        executor.shutdown();  
        System.out.println("总耗时:"+(System.currentTimeMillis()-beginTime));  
    }  
}