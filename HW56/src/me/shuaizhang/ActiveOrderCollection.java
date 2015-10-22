package me.shuaizhang;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by NicolasZHANG on 10/22/15.
 */
public class ActiveOrderCollection {
    private final ActiveCustomerCollection activeCustomers;
    LinkedList<Order> activeOrders;
    private int counter;
    private final int capacity;

    public ActiveOrderCollection(ActiveCustomerCollection activeCustomers) {
        this.activeCustomers = activeCustomers;
        counter = 0;
        capacity = activeCustomers.getCapacity();
        activeOrders = new LinkedList<Order>();

    }

    public synchronized void placeOrder(Customer customer) {
        if (counter < capacity) {
            Order order = new Order(customer.getOrderNum(), customer.getOrder(), customer.getPriority());
            int index = 0;
            for (int i = 0; i < counter; i++) {
                if (order.getPriority() <= activeOrders.get(i).getPriority()) {
                    for (int j = i + 1; j < counter; j++) {
                        if (order.getPriority() != activeOrders.get(j).getPriority()) {
                            index = j;
                            break;
                        }
                    }

                }
            }
            activeOrders.add(index, order);
        }
    }

    public synchronized Order getAnOrder() {

    }

    public synchronized void finishOrder(int orderNum) {
        for (int i = 0; i < counter; i++) {
            Customer customer = activeOrders[i];
            if (customer.getOrderNum() == orderNum) {
                activeOrders[i] = null;
                counter--;
                break;
            }
        }
    }
}
