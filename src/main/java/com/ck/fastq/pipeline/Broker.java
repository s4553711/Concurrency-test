package com.ck.fastq.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jctools.queues.SpscArrayQueue;

/**
 * Created by s4553711 on 2018/5/7.
 */
public class Broker<N> {

    //private BlockingQueue<N> queue = new ArrayBlockingQueue<N>(100_000);
    //private BlockingQueue<N> queue = new LinkedBlockingQueue<>();
    private SpscArrayQueue<N> queue = new SpscArrayQueue<N>(30_000_000);
    private boolean jobRunning = true;
    private double accum = 0.0d;
    private double accum_put = 0.0d;
    private long ac = 0l;
    private long ac_put = 0l;

    public Broker() {

    }

    public void put(N n) throws InterruptedException {
        //queue.put((N)n);
        long prev = System.nanoTime();
        queue.offer((N)n);
        accum_put += (System.nanoTime() - prev)*1.0/1000.0;
        ac_put += 1;
    }

//    public N get() throws InterruptedException {
//        long prev = System.currentTimeMillis();
//        N data = queue.take();
//        accum += (System.currentTimeMillis() - prev)*1.0;
//        ac += 1;
//        return data;
//    }

//    public N drainTo() {
//        Collection<? super N> list = null;
//        this.queue.drainTo(list);
//        if (list == null) return null;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            for(Object k: list) {
//                bos.write((byte[]) k);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return (N)bos.toByteArray();
//    }

    public N pollBuffer() {
        long prev = System.currentTimeMillis();
        ByteBuf buf = Unpooled.buffer(40 * 1024 * 1024);
        while (buf.readableBytes() < 30 * 1024 * 1024 && this.isRunning()) {
            N data = this.queue.poll();
            if (data != null) {
                buf.writeBytes((byte[])data);
                System.err.println("Buffer .. "+buf.readableBytes());
                ac++;
            }
        }
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);
        accum += (System.currentTimeMillis() - prev)*1.0;
        return (N) ret;
    }

    public N poll() throws InterruptedException {
        long prev = System.currentTimeMillis();
        //N data = this.queue.poll(250, TimeUnit.MILLISECONDS);
        N data = this.queue.poll();
        accum += (System.currentTimeMillis() - prev)*1.0;
        ac += 1;
        return data;
    }

    public void getStatistics() {
        System.err.format("Broker   > poll rate: %8.3f ms (%8.2f/%7d)\n", (accum / ac), accum, ac);
        System.err.format("Broker2  > put rate: %8.3f us (%8.2f/%7d)\n", (accum_put / ac_put), accum_put, ac_put);
        accum = 0.0d;
        ac = 0l;
        accum_put = 0.0d;
        ac_put = 0l;
    }

    public SpscArrayQueue<N> getQ() {
        return queue;
    }

    public void stopQueue() {
        jobRunning = false;
    }

    public boolean isRunning() {
        return jobRunning;
    }
}
