package me.shuaizhang;

import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // write your code here
        Simulation.runSimulation(100, 5, 10, 5, false);
    }

    private static final class StringTask implements Callable<String> {
        private int number;

        StringTask(int number) {

            this.number = number;
        }

        public String call() throws InterruptedException {
            //Long operations
            Random rnd = new Random(27);
            long nextInt = rnd.nextInt(1000);
            TimeUnit.MILLISECONDS.sleep(nextInt);
            return "Run" + number;
        }
    }
}
