package me.shuai;

/**
 * Created by Shuai Zhang on 10/28/15.
 */
public class Lock {
    private int count = 0;
    private final Integer Lock = count; // Boxed primitive Lock is shared

    public void doSomething() {
        synchronized (Lock) {
            count++;
            // ...
        }

        System.out.println(count);
    }

    public static void main(String[] args) {
        Lock lock = new Lock();
        lock.doSomething();
    }
}
