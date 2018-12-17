package com.bf.disruptor.test3;

import com.lmax.disruptor.ExceptionHandler;

/**
 * Created by bf on 2017/9/17.
 */
public class MyExceptionHandler implements ExceptionHandler {

    @Override
    public void handleEventException(Throwable ex, long sequence, Object event) {
        //TODO disruptor 异常时候的处理，日志记录等等，下面等同
    }

    @Override
    public void handleOnStartException(Throwable ex) {

    }

    @Override
    public void handleOnShutdownException(Throwable ex) {

    }
}
