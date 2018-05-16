package com.ck.fastq.pipeline;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by s4553711 on 2018/5/7.
 */
public class Broker<N> {

    private BlockingQueue<N> queue = new LinkedBlockingQueue<>();
    private boolean jobRunning = true;

    public Broker() {

    }

    public void put(N n) throws InterruptedException {
        queue.put((N)n);
    }

    public N get() throws InterruptedException {
        return (N)queue.take();
    }

    public N poll() throws InterruptedException {
        return this.queue.poll(250, TimeUnit.MILLISECONDS);
    }

    public BlockingQueue<N> getQ() {
        return queue;
    }

    public void stopQueue() {
        jobRunning = false;
    }

    public boolean isRunning() {
        return jobRunning;
    }
}
