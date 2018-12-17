package com.bf.disruptor.demo;

/**
 *  Disruptor request 请求参数
 * Created by bf on 2017/9/17.
 */
public class RequestDto {

    // 用户id
    private final String userId;

    // 商品id
    private final String itemId;

    // 商品的购买数量
    private int num = 1;

    public RequestDto(String userId, String itemId, int num) {
        this.userId = userId;
        this.itemId = itemId;
        this.num = num;
    }

    public String getUserId() {
        return userId;
    }

    public String getItemId() {
        return itemId;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
        return "RequestDto{" +
                "userId='" + userId + '\'' +
                ", itemId='" + itemId + '\'' +
                '}';
    }
}
