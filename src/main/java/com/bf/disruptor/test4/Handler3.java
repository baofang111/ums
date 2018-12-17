package com.bf.disruptor.test4;

import com.bf.disruptor.test2.Trade;
import com.lmax.disruptor.EventHandler;

public class Handler3 implements EventHandler<Trade> {
    @Override  
    public void onEvent(Trade event, long sequence,  boolean endOfBatch) throws Exception {  
        System.out.println("handler3: name: " + event.getName() + " , price: " + event.getPrice() + ";  instance: " + event.getId());
    }  
}