package me.shuai;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Shuai Zhang on 11/16/15.
 */
public class AmatureMTMazeSolver extends SkippingMazeSolver {
    private final int numberOfThreads = Runtime.getRuntime().availableProcessors() + 1;
    private final ExecutorService pool;

    public AmatureMTMazeSolver(Maze maze) {
        super(maze);

        pool = Executors.newFixedThreadPool(numberOfThreads);
    }


    public List<Direction> solve() {
        LinkedList<DFSTask> tasks = new LinkedList<>();
        List<Future<List<Direction>>> futures;
        List<Direction> result = null;


        try {
            Choice start = firstChoice(maze.getStart());

            int size = start.choices.size();
            for (int i = 0; i < size; i++) {
                Choice choice = follow(start.at, start.choices.peek());
                tasks.add(new DFSTask(choice, start.choices.pop()));
            }

            futures = pool.invokeAll(tasks);
            pool.shutdown();

            for (Future<List<Direction>> ans : futures) {
                List<Direction> directions = ans.get();
                if (directions != null) {
                    result = directions;
                }
            }
        } catch (InterruptedException | ExecutionException | SolutionFound e) {
            e.printStackTrace();
        }

        return result;
    }

    private class DFSTask implements Callable<List<Direction>> {
        Choice head;
        Direction direction;

        public DFSTask(Choice head, Direction direction) {
            this.head = head;
            this.direction = direction;
        }

        @Override
        public List<Direction> call() {
            LinkedList<Choice> choiceStack = new LinkedList<>();
            Choice currentChoice;

            try {
                choiceStack.push(this.head);

                while (!choiceStack.isEmpty()) {
                    currentChoice = choiceStack.peek();

                    if (currentChoice.isDeadend()) {
                        //backtrack
                        choiceStack.pop();
                        if (!choiceStack.isEmpty()) {
                            choiceStack.peek().choices.pop();
                        }
                        continue;
                    }
                    choiceStack.push(follow(currentChoice.at, currentChoice.choices.peek()));
                }
                return null;
            } catch (SolutionFound e) {
                Iterator<Choice> iter = choiceStack.iterator();
                LinkedList<Direction> solutionPath = new LinkedList<>();


                while (iter.hasNext()) {
                    currentChoice = iter.next();
                    solutionPath.push(currentChoice.choices.peek());
                }
                solutionPath.push(direction);
                if (maze.display != null) maze.display.updateDisplay();

                return pathToFullPath(solutionPath);
            }

        }

    }
}
