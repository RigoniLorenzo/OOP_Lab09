package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a implementation of the calculation for matrix.
 * 
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int numThread;

    /**
     * 
     * @param numThread
     *            no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int numThread) {
        this.numThread = numThread;
    }

    private static final class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        private Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " //NOPMD
            + this.startpos + " to position " + (this.startpos + this.nelem - 1)); 
            for (int i = this.startpos; i < this.matrix.length && i < this.startpos + this.nelem; i++) {
                for (final double d : this.matrix[i]) {
                    this.res += d;
                }
            }
        }

        /**
         * Returns the result of summing up the doubles within the matrix.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % this.numThread + matrix.length / this.numThread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(this.numThread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish
         */
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }
}
