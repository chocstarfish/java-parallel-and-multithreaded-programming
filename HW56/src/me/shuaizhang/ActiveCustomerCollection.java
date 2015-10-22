package me.shuaizhang;

/**
 * Created by NicolasZHANG on 10/22/15.
 */
public class ActiveCustomerCollection {
    Customer[] activeCustomers;
    private int counter;
    private final int capacity;

    public ActiveCustomerCollection(int capacity) {
        this.capacity = capacity;
        counter = 0;
        activeCustomers = new Customer[capacity];
    }

    public synchronized void addCustomer(Customer customer) {
        activeCustomers[counter++] = customer;
    }

    public synchronized void removeCustomer(int orderNum) {
        for (int i = 0; i < counter; i++) {
            Customer customer = activeCustomers[i];
            if (customer.getOrderNum() == orderNum) {
                activeCustomers[i] = null;
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
