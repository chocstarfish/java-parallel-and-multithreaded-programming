package me.shuaizhang;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {

    private final String name;
    private final Map<String, Machine> machines;
    private final ActiveOrderCollection activeOrders;

    /**
     * You can feel free modify this constructor.  It must
     * take at least the name, but may take other parameters
     * if you would find adding them useful.
     *
     * @param name         the name of the cook
     * @param machines     the collection of machines
     * @param activeOrders the active order collection
     */
    public Cook(String name, Map<String, Machine> machines, ActiveOrderCollection activeOrders) {
        this.activeOrders = activeOrders;
        this.name = name;
        this.machines = machines;
    }

    public String toString() {
        return name;
    }


    /**
     * This method executes as follows.  The cook tries to retrieve
     * orders placed by Customers.  For each order, a List<Food>, the
     * cook submits each Food item in the List to an appropriate
     * Machine, by calling makeFood().  Once all machines have
     * produced the desired Food, the order is complete, and the Customer
     * is notified.  The cook can then go to process the next order.
     * If during its execution the cook is interrupted (i.e., some
     * other thread calls the interrupt() method on it, which could
     * raise InterruptedException if the cook is blocking), then it
     * terminates.
     */
    public void run() {
        Simulation.logEvent(SimulationEvent.cookStarting(this));
        while (!Thread.currentThread().isInterrupted()) {
            // get order
            try {
                Order order = activeOrders.getAnOrder();

                if (order == null) {
                    continue;
                }

                Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, order.getOrderItems(), order.getOrderNum()));

                ArrayList<Future<MachineCookingResult>> resultList = new ArrayList<Future<MachineCookingResult>>();
                for (Food food : order.getOrderItems()) {
                    Machine machine = machines.get(food.name);
                    Future<MachineCookingResult> machineCookResultFuture = machine.makeFood(food, order.getOrderNum());
                    resultList.add(machineCookResultFuture);
                    Simulation.logEvent(SimulationEvent.cookStartedFood(this, food, order.getOrderNum()));
                }

                // get machine cooking results sequentially
                for (Future<MachineCookingResult> result : resultList) {
                    try {
                        MachineCookingResult machineCookingResult = result.get();
                        Simulation.logEvent(SimulationEvent.cookFinishedFood(this, machineCookingResult.food, machineCookingResult.orderNum));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, order.getOrderNum()));
                activeOrders.finishOrder(order);
            } catch (InterruptedException e) {
                // This code assumes the provided code in the Simulation class
                // that interrupts each cook thread when all customers are done.
                // You might need to change this if you change how things are
                // done in the Simulation class.
                Simulation.logEvent(SimulationEvent.cookEnding(this));
                break;
            }
        }
    }
}