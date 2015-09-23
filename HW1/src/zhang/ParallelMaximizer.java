package zhang;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelMaximizer {

    int numThreads;
    ArrayList<ParallelMaximizerWorker> workers;

    public ParallelMaximizer(int numThreads) {
        workers = new ArrayList<>(numThreads);
        this.numThreads = numThreads;
    }


    public static void main(String[] args) {
        int numThreads = 4; // number of threads for the maximizer
        int numElements = 10; // number of integers in the list

        ParallelMaximizer maximizer = new ParallelMaximizer(numThreads);
        LinkedList<Integer> list = new LinkedList<Integer>();

        // System.out.println("numThreads before populating: " + numThreads);

        // populate the list
        // TODO: change this implementation to test accordingly
        for (int i = 0; i < numElements; i++) {
            list.add(i);
        }

        // run the maximizer
        try {
             System.out.println("numThreads inside try " + numThreads);
            System.out.println(maximizer.max(list));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Finds the maximum by using <code>numThreads</code> instances of
     * <code>ParallelMaximizerWorker</code> to find partial maximums and then
     * combining the results.
     *
     * @param list <code>LinkedList</code> containing <code>Integers</code>
     * @return Maximum element in the <code>LinkedList</code>
     * @throws InterruptedException
     */
    public int max(LinkedList<Integer> list) throws InterruptedException {
        int max = Integer.MIN_VALUE; // initialize max as lowest value

        // System.out.println("numThreads " + numThreads);
        // System.out.println("this.numThreads " + this.numThreads);

        // run numThreads instances of ParallelMaximizerWorker
        for (int i = 0; i < numThreads; i++) {
            workers.add(new ParallelMaximizerWorker(list));
            workers.get(i).start();
        }

        // System.out.println("workers.size(): " + workers.size()); - working
        // wait for threads to finish
        for (int i = 0; i < numThreads; i++) {
            workers.get(i).join();
        }

        // take the highest of the partial maximums
        // TODO: IMPLEMENT CODE HERE
        for (int i = 0; i < numThreads; i++) {
            if (max < workers.get(i).getPartialMax()) {
                max = workers.get(i).getPartialMax();
            }
// My code added inside for:
            System.out.println("worker " + i + ", getPartialMax: " + workers.get(i).getPartialMax());
        }
//		System.out.println("max just before returned: " + max);

        return max;
    }

}