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
    private static final int THRESHOLD = 5;
    private final ForkJoinPool forkJoinPool;

    public StudentMTMazeSolver(Maze maze) {
        super(maze);
        forkJoinPool = new ForkJoinPool();
    }

    public List<Direction> solve() {
        Position start = maze.getStart();

        List<Direction> result = forkJoinPool.invoke(new MazeSolverTask(start, null, new LinkedList<>(), 0));

        if (maze.display != null) {
            maze.display.updateDisplay();
        }

        return result;
    }


    private class MazeSolverTask extends RecursiveTask<List<Direction>> {

        private Position position;
        private final Direction direction;
        private final int recursionDepth;
        private LinkedList<Direction> historicalDirections;

        MazeSolverTask(Position position, Direction direction, LinkedList<Direction> historicalDirections, int recursionDepth) {
            this.position = position;
            this.direction = direction;
            this.recursionDepth = recursionDepth;
            this.historicalDirections = historicalDirections;
        }

        @Override
        protected List<Direction> compute() {
            if (position.equals(maze.getEnd())) {
                // found solution
                historicalDirections.addLast(direction);
                return historicalDirections;
            }

            LinkedList<Direction> moves;
            if (direction == null) {
                moves = maze.getMoves(position);
            } else {
                moves = new LinkedList<>();
                moves.addLast(direction);
            }


            int size = moves.size();
            if (size == 0) {
                // dead end
                return null;
            } else if (size == 1) {
                // corridor
                do {
                    Direction direction = moves.peek();
                    position = position.move(direction);
                    moves = maze.getMoves(position);
                } while (moves.size() == 1);
            }

            int newSize = moves.size();
            if (newSize == 0) {
                return null;
            } else {
                // newSize can be 2 and 3
                MazeSolverTask forkedTask = null;
                Direction directionForForkedTask = null;
                if (recursionDepth > THRESHOLD) {
                    directionForForkedTask = moves.pop();

                    forkedTask = new MazeSolverTask(position.move(directionForForkedTask), directionForForkedTask, historicalDirections, 0);
                    forkedTask.fork();
                }

                for (Direction dir : moves) {
                    MazeSolverTask mazeSolverTask = new MazeSolverTask(position.move(dir), dir, historicalDirections, recursionDepth + 1);
                    List<Direction> result = mazeSolverTask.compute();

                    if (result != null) {
                        result.add(0, dir);
                        return result;
                    }
                }

                if (forkedTask != null) {
                    List<Direction> result = forkedTask.join();
                    if (result != null) {
                        result.add(0, directionForForkedTask);
                        return result;
                    }
                }
            }

            return null;
        }
    }

}
