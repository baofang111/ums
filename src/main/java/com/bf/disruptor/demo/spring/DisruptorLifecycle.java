package com.bf.disruptor.demo.spring;

import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.context.SmartLifecycle;

/**
 * Disruptor spring 上下文启动时的数据加载
 *  实现 SmartLifecycle 的类会在 ApplicationContext 自身启动或者停止的时候执行
 * Created by bf on 2017/9/17.
 */
public class DisruptorLifecycle implements SmartLifecycle {

    // 启动标示
    private volatile boolean running = false;

    private final String disruptorName;

    // final 修饰的全局变量，必须要有带改参数的构造方法
    private final Disruptor disruptor;

    private final int phase;

    public DisruptorLifecycle(String disruptorName, Disruptor disruptor, int phase) {
        this.disruptorName = disruptorName;
        this.disruptor = disruptor;
        this.phase = phase;
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
       this.stop();
       callback.run();
    }

    @Override
    public void start() {
        disruptor.start();
        running = true;
    }

    // stop
    @Override
    public void stop() {
        disruptor.shutdown();
        running = false;
    }

    // 是否是启动状态
    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        return this.phase;
    }
}
