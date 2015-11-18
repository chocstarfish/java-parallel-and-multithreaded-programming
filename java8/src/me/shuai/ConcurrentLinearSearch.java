package me.shuai;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Created by Shuai Zhang on 10/29/15.
 */
public class ConcurrentLinearSearch extends RecursiveTask<Integer> {
    final static int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() + 1;
    final static int THRESHOLD = NUMBER_OF_THREADS * 10;
    private static ConcurrentLinkedQueue<ConcurrentLinearSearch> tasks = new ConcurrentLinkedQueue<>();
    private static int[] array;
    private static int target;
    private final int start;
    private final int end;

    ConcurrentLinearSearch(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static void main(String[] args) {
        int length = 10000;
        int[] elems = new int[length];
        for (int i = 0; i < length; i++) {
            elems[i] = i;
        }

        int result = search(elems, 9999);
        System.out.println(result);
    }

    public static int search(int[] elms, int elm) {

        ForkJoinPool forkJoinPool = new ForkJoinPool(NUMBER_OF_THREADS);
        ConcurrentLinearSearch concurrentLinearSearch = new ConcurrentLinearSearch(0, elms.length);
        array = elms;
        target = elm;
        return forkJoinPool.invoke(concurrentLinearSearch);
    }


    @Override
    protected Integer compute() {
        if (end - start < THRESHOLD) {
            for (int i = start; i < end; i++) {
                if (array[i] == target) {
                    return i;
                }
            }
            return -1;
        } else {
            int m = (end - start) / 2;
            ConcurrentLinearSearch left, right;
            left = new ConcurrentLinearSearch(start, end - m);
            right = new ConcurrentLinearSearch(end - m, end);
            tasks.add(left);
            tasks.add(right);

            tasks.forEach(me.shuai.ConcurrentLinearSearch::fork);

            int finalResult = -1;
            for (ConcurrentLinearSearch task : tasks) {
                int result = task.join();
                if (result != -1) {
                    if (finalResult == -1 || result < finalResult) {
                        finalResult = result;
                    }
                }
            }

            return finalResult;

        }

    }
}
