package me.shuaizhang;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
    //JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
    private final String name;
    private final List<Food> order;
    private final int orderNum;
    private final int priority;
    private final ActiveCustomerCollection activeCustomers;
    private final ActiveOrderCollection activeOrders;

    private static int runningCounter = 0;
    private boolean orderFinished = false;

    private final Object lock = new Object();

    /**
     * You can feel free modify this constructor.  It must take at
     * least the name and order but may take other parameters if you
     * would find adding them useful.
     */
    public Customer(String name, List<Food> order, int priority, ActiveCustomerCollection activeCustomers, ActiveOrderCollection activeOrders) {
        this.name = name;
        this.order = order;
        this.priority = priority;
        this.activeCustomers = activeCustomers;
        this.activeOrders = activeOrders;
        this.orderNum = ++runningCounter;
    }

    public String toString() {
        return getName();
    }

    /**
     * This method defines what an Customer does: The customer attempts to
     * enter the coffee shop (only successful when the coffee shop has a
     * free table), place its order, and then leave the coffee shop
     * when the order is complete.
     */
    public void run() {
        //YOUR CODE GOES HERE...
        Simulation.logEvent(SimulationEvent.customerStarting(this));


        // wait until there is available table
        while (true) {
            if (!activeCustomers.isFull()) {
                activeCustomers.addCustomer(this);
                Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
                break;
            }
        }

        activeOrders.placeOrder(this);
        Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, getOrder(), getOrderNum()));

        // wait until order finished
        synchronized (lock) {
            while (!orderFinished) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("ERROR: Customer interrupted");
                    break;
                }
            }
        }

        Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
    }

    public void receiveOrder(Order order) {
        synchronized (lock) {
            orderFinished = true;
            lock.notify();
        }
        Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order.getOrderItems(), order.getOrderNum()));
    }

    public String getName() {
        return name;
    }

    public List<Food> getOrder() {
        return order;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public int getPriority() {
        return priority;
    }
}