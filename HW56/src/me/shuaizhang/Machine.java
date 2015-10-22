package me.shuaizhang;


import java.util.concurrent.*;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
    private ExecutorService executor;
    public final String machineName;
    public final Food machineFoodType;
    public final int machineCapacity;

    //YOUR CODE GOES HERE...


    /**
     * The constructor takes at least the name of the machine,
     * the Food item it makes, and its capacity.  You may extend
     * it with other arguments, if you wish.  Notice that the
     * constructor currently does nothing with the capacity; you
     * must add code to make use of this field (and do whatever
     * initialization etc. you need).
     */
    public Machine(String nameIn, Food foodIn, int capacityIn) {
        executor = Executors.newFixedThreadPool(capacityIn);
        this.machineName = nameIn;
        this.machineFoodType = foodIn;
        this.machineCapacity = capacityIn;
        //YOUR CODE GOES HERE...
        Simulation.logEvent(SimulationEvent.machineStarting(this, machineFoodType, machineCapacity));
    }


    /**
     * This method is called by a Cook in order to make the Machine's
     * food item.  You can extend this method however you like, e.g.,
     * you can have it take extra parameters or return something other
     * than Object.  It should block if the machine is currently at full
     * capacity.  If not, the method should return, so the Cook making
     * the call can proceed.  You will need to implement some means to
     * notify the calling Cook when the food item is finished.
     */
    public Future<MachineCookResult> makeFood(Food food, int orderNum) {
        //YOUR CODE GOES HERE...
        CookAnItem cookAnItem = new CookAnItem(food, orderNum);
        return executor.submit(cookAnItem);
    }

    public void shutDown() {
        Simulation.logEvent(SimulationEvent.machineEnding(this));
        executor.shutdown();
    }

    //THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
    private class CookAnItem implements Callable<MachineCookResult> {
        private Food food;
        private int orderNum;

        public CookAnItem(Food food, int orderNum) {
            this.food = food;
            this.orderNum = orderNum;
        }

        @Override
        public MachineCookResult call() throws Exception {
            try {
                Long duration = (long) food.cookTimeMS;
                Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, food));
                TimeUnit.MILLISECONDS.sleep(duration);
                Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, null));
                return new MachineCookResult(food, orderNum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public String toString() {
        return machineName;
    }
}