package com.bf.disruptor.demo;

/**
 * Disruptor 事件传播 Event
 * Created by bf on 2017/9/17.
 */
public class ItemEvent {

    // 事件参数
    private RequestDto requestDto;

    public RequestDto getRequestDto() {
        return requestDto;
    }

    public void setRequestDto(RequestDto requestDto) {
        this.requestDto = requestDto;
    }
}
