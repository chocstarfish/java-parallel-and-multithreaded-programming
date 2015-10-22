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

    private static int runningCounter = 0;

    /**
     * You can feel free modify this constructor.  It must take at
     * least the name and order but may take other parameters if you
     * would find adding them useful.
     */
    public Customer(String name, List<Food> order, int priority, ActiveCustomerCollection activeCustomers) {
        this.name = name;
        this.order = order;
        this.priority = priority;
        this.activeCustomers = activeCustomers;
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
                break;
            }
        }
        Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));


        Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, getOrder(), getOrderNum()));

        Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, getOrder(), getOrderNum()));

        Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));

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