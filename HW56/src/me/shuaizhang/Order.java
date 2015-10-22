package me.shuaizhang;

import java.util.List;

/**
 * Created by NicolasZHANG on 10/22/15.
 */
public class Order {
    private final int orderNum;
    private final List<Food> orderItems;
    private final int priority;

    public Order(int orderNum, List<Food> orderItems, int priority) {
        this.orderNum = orderNum;
        this.orderItems = orderItems;
        this.priority = priority;
    }


    public int getOrderNum() {
        return orderNum;
    }

    public List<Food> getOrderItems() {
        return orderItems;
    }

    public int getPriority() {
        return priority;
    }
}
