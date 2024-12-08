package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MultiThreadedSumMatrixWithStreams implements SumMatrix {
    private final int nthread;

    public MultiThreadedSumMatrixWithStreams(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long sum;

        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
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

        public long getResult() {
            return this.sum;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;

        return IntStream
                .iterate(0, start -> start + size)
                .limit(nthread)
                .mapToObj(start -> new Worker(matrix, start, size))
                .peek(Thread::start)
                .peek(MultiThreadedSumMatrixWithStreams::joinUninterruptibly)
                .mapToLong(Worker::getResult)
                .sum();
    }

    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
