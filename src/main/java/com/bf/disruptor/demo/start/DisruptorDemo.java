package com.bf.disruptor.demo.start;

import com.bf.disruptor.demo.*;
import com.bf.disruptor.test3.MyExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *  Disruptor 测试 代码
 *      方式一: @PostConstruct 初始化的时候启动
 *      方式二: 写一个配置类，继承 ApplicationContextAware（spring 上下文）
 *              然后里面写个方法  返回 生产者 ItemEventProducer（@Bean）,然后其他地方就可以使用了
 * Created by bf on 2017/9/17.
 */
public class DisruptorDemo {

    // RingBuffer 的大小
    private int bufferSize = 1024 * 1024;

    // itemEvent 事件的生产者
    private ItemEventProducer itemEventProducer;

    private Disruptor<ItemEvent> disruptor;

    /**
     * 初始化 Disruptor ,创建消费者，生产者等，然后生产者获取到数据之后，就可以执行了
     * PostConstruct 在 servlet 构造方法执行之后执行，项目启动的时候，只会执行一次
     *  -- 对应的 @PreDestroy 在serlvet 执行destroy（） 之后执行
     */
    // @PostConstruct
    public void start(){

        // 创建线程池
        ThreadFactory executor = Executors.defaultThreadFactory();

        ItemHandler itemHandler = new ItemHandler();

        // 这里应该不能使用 ProducerType.SINGLE 单生产者？？？？ 多线程处理的话
        disruptor = new Disruptor<ItemEvent>(new ItemEventFactory(), bufferSize, executor);

        // 放置消费者 -- 也可以控制消费者的顺序，详情可见 Client
        disruptor.handleEventsWith(itemHandler);

        // 添加disruptor 异常处理器
        // disruptor 异常处理是这样的，不管handler 是 A-B-C-D 的顺序，只要出现异常，抛出异常的那个 handler 都会中断执行
        disruptor.setDefaultExceptionHandler(new MyExceptionHandler());

        disruptor.start();

        // 添加生产者
        this.itemEventProducer = new ItemEventProducer(disruptor.getRingBuffer());

    }

    /**
     * 停止
     */
    // @PreDestroy
    public void shutdown() {
        System.err.println("disruptor 关闭");
        // 和 shutdown 的区别
        disruptor.halt();
    }

    /**
     * 抢购 兑换商品
     * @param event
     */
    public void doExchange(RequestDto event){
        itemEventProducer.onData(event);
    }

}
