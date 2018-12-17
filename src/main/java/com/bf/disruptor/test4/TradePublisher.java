package com.bf.disruptor.test4;

import com.bf.disruptor.test2.Trade;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TradePublisher implements Runnable {

    private Disruptor<Trade> disruptor;
    private CountDownLatch latch;

    private static final int LOOP = 10;// 模拟百次交易的发生

    public TradePublisher(Disruptor<Trade> disruptor) {
        this.disruptor = disruptor;
    }

    public TradePublisher(Disruptor<Trade> disruptor, CountDownLatch latch) {
        this.disruptor = disruptor;
        this.latch = latch;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeTransloator = new TradeEventTranslator();
        for (int i = 0; i < LOOP; i++) {
            disruptor.publishEvent(tradeTransloator);
        }
        latch.countDown();
    }
}
  
class TradeEventTranslator implements EventTranslator<Trade> {
    private Random random = new Random();
    @Override
    public void translateTo(Trade event, long sequence) {
        this.generateTrade(event);
    }
    private Trade generateTrade(Trade trade) {
        trade.setPrice(random.nextDouble() * 9999);
        return trade;
    }
}