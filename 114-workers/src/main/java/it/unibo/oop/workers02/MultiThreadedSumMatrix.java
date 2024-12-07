package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long sum;

        Worker(final double[][] matrix, final int startpos, final int nelem) {
            this.matrix = matrix;
            this.nelem = nelem;
            this.startpos = startpos;
        }

        @Override
        public void run() {
            for (int i = startpos; i < startpos + nelem && i < matrix.length; i++) {
                for (final double d : matrix[i]) {
                    this.sum += d;
                }
            }
        }

        public double getResult() {
            return this.sum;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);

        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker worker : workers) {
            worker.start();
        }

        double sum = 0;

        for (final Worker worker : workers) {
            try {
                worker.join();
                sum += worker.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException("a thread was interrupted before finishing the task");
            }
        }
        return sum;
    }
}
