package com.bf.disruptor.test4;

import com.bf.disruptor.test2.Trade;
import com.lmax.disruptor.EventHandler;

public class Handler4 implements EventHandler<Trade> {
    @Override  
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {  
        System.out.println("handler4: append name");
        event.setName(event.getName() + "h4");
    }  
}