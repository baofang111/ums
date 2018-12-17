package com.bf.disruptor.demo;


import com.lmax.disruptor.EventHandler;

/**
 * Disruptor 消费者
 * Created by bf on 2017/9/17.
 */
public class ItemHandler implements EventHandler<ItemEvent> {

    @Override
    public void onEvent(ItemEvent event, long sequence, boolean endOfBatch) throws Exception {
        //TODO 处理抢购商品业务
        System.out.println("用户：" + event.getRequestDto().getUserId() + ", 抢兑商品： " + event.getRequestDto().getItemId() + "成功");
    }

}
