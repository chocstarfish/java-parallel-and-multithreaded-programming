package me.shuaizhang;

import java.util.ArrayList;

/**
 * Created by Shuai Zhang on 10/22/15.
 */
public class ActiveCustomerCollection {
    private final ArrayList<Customer> activeCustomers;
    private int counter;
    private final int capacity;

    public ActiveCustomerCollection(int capacity) {
        this.capacity = capacity;
        counter = 0;
        activeCustomers = new ArrayList<Customer>();
    }

    public synchronized void addCustomer(Customer customer) {
        activeCustomers.add(customer);
        counter++;
    }

    public synchronized void finishWithCustomer(Order order) {
        for (int i = 0; i < counter; i++) {
            Customer customer = activeCustomers.get(i);
            if (customer.getOrderNum() == order.getOrderNum()) {
                activeCustomers.get(i).receiveOrder(order);
                activeCustomers.remove(i);
                counter--;
                break;
            }
        }
    }

    public synchronized boolean isFull() {
        return counter == capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
