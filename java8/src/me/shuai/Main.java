package me.shuai;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {

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
        final int numberOfThreads = Runtime.getRuntime().availableProcessors() + 1;
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CompletionService<SearchResult> completionService = new ExecutorCompletionService<>(executor);

        int startIndex = 0, endIndex = 0, taskCount = 0;
        while (endIndex != elms.length) {
            endIndex = startIndex + elms.length / numberOfThreads;

            if (endIndex > elms.length) {
                endIndex = elms.length;
            }

            final int finalStartIndex = startIndex;
            final int finalEndIndex = endIndex;
            completionService.submit(() -> {
                for (int i = finalStartIndex; i < finalEndIndex; i++) {
                    if (elms[i] == elm) {
                        return new SearchResult(true, i);
                    }
                }

                return new SearchResult(false, -1);
            });

            startIndex = endIndex + 1;
            taskCount++;
        }

        ArrayList<Integer> results = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            try {
                Future<SearchResult> futureResult = completionService.take();
                SearchResult searchResult = futureResult.get();
                if (searchResult.success) {
                    results.add(searchResult.result);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        if (results.isEmpty()) {
            return -1;
        } else {
            int minIndex = -1;
            for (int result : results) {
                if (minIndex == -1) {
                    minIndex = result;
                } else if (result < minIndex) {
                    minIndex = result;
                }
            }
            return minIndex;
        }
    }

    private static class SearchResult {
        final boolean success;
        final int result;

        SearchResult(boolean success, int result) {
            this.success = success;
            this.result = result;
        }


    }
}
