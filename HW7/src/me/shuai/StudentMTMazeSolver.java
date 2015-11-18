package me.shuai;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * This file needs to hold your solver to be tested.
 * You can alter the class to extend any class that extends MazeSolver.
 * It must have a constructor that takes in a Maze.
 * It must have a solve() method that returns the datatype List<Direction>
 * which will either be a reference to a list of steps to take or will
 * be null if the maze cannot be solved.
 */
public class StudentMTMazeSolver extends MazeSolver {
    private int threshold = Runtime.getRuntime().availableProcessors();
    private final ForkJoinPool forkJoinPool;

    public StudentMTMazeSolver(Maze maze) {
        super(maze);
        int scaledThreshold = (int) (Math.sqrt(maze.getWidth() * maze.getHeight()) / 100);
        if (scaledThreshold > threshold) {
            threshold = scaledThreshold;
        }

        forkJoinPool = new ForkJoinPool();
    }

    public List<Direction> solve() {
        Position start = maze.getStart();
        LinkedList<Direction> directions = new LinkedList<>();
        Boolean result = forkJoinPool.invoke(new MazeSolverTask(start, null, directions, 0));

        if (result) {
            if (maze.display != null) {
                maze.display.updateDisplay();
            }


            return directions;
        }


        return null;
    }


    private class MazeSolverTask extends RecursiveTask<Boolean> {

        private Position position;
        private Direction direction;
        private final int recursionDepth;
        private LinkedList<Direction> historicalDirections;

        MazeSolverTask(Position position, Direction direction, LinkedList<Direction> historicalDirections, int recursionDepth) {
            this.position = position;
            this.direction = direction;
            this.recursionDepth = recursionDepth;
            this.historicalDirections = historicalDirections;
        }

        @Override
        protected Boolean compute() {
            if (position.equals(maze.getEnd())) {
                // found solution
                return true;
            }

            LinkedList<Direction> moves = maze.getMoves(position);

            if (moves.isEmpty()) {
                return false;
            }

            LinkedList<MazeSolverTask> forkedTasks = new LinkedList<>();
            for (Direction move : moves) {
                if (direction == null || !move.equals(direction.reverse())) {
                    MazeSolverTask mazeSolverTask;
                    if (recursionDepth < threshold) {
                        mazeSolverTask = new MazeSolverTask(position.move(move), move, historicalDirections, recursionDepth + 1);
                        Boolean result = mazeSolverTask.compute();
                        if (result) {
                            historicalDirections.push(move);
                            return true;
                        }
                    } else {
                        mazeSolverTask = new MazeSolverTask(position.move(move), move, historicalDirections, 0);
                        forkedTasks.push(mazeSolverTask);
                        mazeSolverTask.fork();
                    }
                }
            }

            for (MazeSolverTask forkedMazeSolverTask : forkedTasks) {
                if (forkedMazeSolverTask.join()) {
                    historicalDirections.push(forkedMazeSolverTask.direction);
                    return true;
                }
            }


            return false;
        }
    }

}
