package me.shuai;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        // write your code here
        AtomicInteger atomicInt = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000).forEach(i -> {
            Runnable task = () -> atomicInt.accumulateAndGet(i, (n, m) -> n + m);
            executor.submit(task);
        });

        executor.shutdown();

        System.out.println(atomicInt.get());    // => 499500
    }
}
