package com.bf.disruptor.test3;

/**
 *  Disruptor 多生产者多消费者模型Event
 *
 * Created by bf on 2017/9/17.
 */
public class Order {

    private String id;

    private String name;

    private double price;


    public Order() {

    }

    public Order(String id) {
        this.id = id;
    }

    public Order(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
