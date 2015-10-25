package me.shuaizhang;

import java.util.LinkedList;

/**
 * Created by Shuai Zhang on 10/22/15.
 */
public class ActiveOrderCollection {
    private final ActiveCustomerCollection activeCustomers;
    private final LinkedList<Order> activeOrders;
    private int counter;

    public ActiveOrderCollection(ActiveCustomerCollection activeCustomers) {
        this.activeCustomers = activeCustomers;
        counter = 0;
        activeOrders = new LinkedList<Order>();
    }

    public synchronized void placeOrder(Customer customer) {
        Order order = new Order(customer.getOrderNum(), customer.getOrder(), customer.getPriority());
        int currentOrderPriority = order.getPriority();

        boolean inserted = false;
        for (int i = 0; i < counter; i++) {
            int orderInTheCollectionPriority = activeOrders.get(i).getPriority();
            if (currentOrderPriority < orderInTheCollectionPriority) {
                inserted = true;
                activeOrders.add(i, order);
                break;
            }
        }

        if (!inserted) {
            activeOrders.addLast(order);
        }

        counter++;
    }

    public synchronized Order getAnOrder() {
        if (!activeOrders.isEmpty()) {
            counter--;
            return activeOrders.removeFirst();
        }
        return null;
    }

    public synchronized void finishOrder(Order order) {
        activeCustomers.finishWithCustomer(order);
    }
}
