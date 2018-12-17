package com.bf.disruptor.demo.start;

import com.bf.disruptor.demo.ItemEvent;
import com.bf.disruptor.demo.ItemEventFactory;
import com.bf.disruptor.demo.ItemEventProducer;
import com.bf.disruptor.demo.ItemHandler;
import com.bf.disruptor.demo.spring.DisruptorLifecycle;
import com.bf.disruptor.demo.spring.StartupOrderConstants;
import com.bf.disruptor.demo.utils.BeanRegisterUtils;
import com.bf.disruptor.test3.MyExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *  Disruptor 配置类
 *     实现 ApplicationContextAware 加载 spring 上下文
 * Created by bf on 2017/9/17.
 */
@Configuration
public class DisruptorConfig implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ItemEventProducer itemEventProducer(){
        // 创建线程池
        ThreadFactory executor = Executors.defaultThreadFactory();

        int bufferSize = 1024 * 1024;

        ItemHandler itemHandler = new ItemHandler();

        // 这里应该不能使用 ProducerType.SINGLE 单生产者？？？？ 多线程处理的话
        Disruptor<ItemEvent> disruptor = new Disruptor<ItemEvent>(new ItemEventFactory(), bufferSize, executor);

        // 放置消费者 -- 也可以控制消费者的顺序，详情可见 Client
        disruptor.handleEventsWith(itemHandler);

        // 添加disruptor 异常处理器
        // disruptor 异常处理是这样的，不管handler 是 A-B-C-D 的顺序，只要出现异常，抛出异常的那个 handler 都会中断执行
        disruptor.setDefaultExceptionHandler(new MyExceptionHandler());

        // 添加生产者
        ItemEventProducer itemEventProducer = new ItemEventProducer(disruptor.getRingBuffer());

        // 创建单例
        BeanRegisterUtils.registerSingleton(
                applicationContext,
                "RequestDtoEventDisruptorLifeCycleContainer",
                new DisruptorLifecycle("RequestDtoEventDisruptor", disruptor,
                        StartupOrderConstants.DISRUPTOR_REQUEST_DTO));

        return itemEventProducer;
    }


}
