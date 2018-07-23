package com.ck.fastq.Reader;

import com.ck.fastq.pipeline.Broker;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by s4553711 on 2018/5/10.
 */
public class FastqQueueReader<T> implements Iterable<T> {

    private Broker<T> broker;
    private byte[] buff;
    private int newline;
    private int DEFAULT_BUFF_SIZE = 44_000_000;
    private int head;
    private int tail;
    private int t_head;
    private double accum;
    private long ac;
    private long failR;
    private double accum_loop;
    private double accum_yield;
    private long loop_count;
    private long accum_byte;

    public FastqQueueReader(Broker<T> b) {
        broker = b;
        newline = 0;
        head = tail = 0;
        buff = new byte[DEFAULT_BUFF_SIZE];
        t_head = 0;
        accum = 0.0d;
        ac = 0l;
        failR = 0l;
        accum_loop = 0.0d;
        loop_count = 0l;
        accum_byte = 0l;
    }

    @Override
    public Iterator<T> iterator() {
        return new InnerIterator();
    }

    public boolean hasNext() {
        boolean result = !broker.getQ().isEmpty() || head < tail;
        return result;
    }

    public T read() {
        T result = null;
        try {
            for(;;) {
                fill();

                if (t_head == tail) {
                    long b1 = System.nanoTime();
                    // Thread.sleep(30);
                    // System.err.println(head+" -> "+t_head+" -> "+tail+", newline: "+newline);
                    accum_yield += ((System.nanoTime() - b1)*1.0/1000.0);
                }
                long a1 = System.nanoTime();
                for(int i = t_head; i <= tail; i++, accum_byte++) {
                    byte b = buff[i];
                    if (b == 10) newline++;
                    //System.out.println("P: ("+i+"/"+newline+") :: "+b+" :: "+(char)b);
                    if (newline != 0 && newline % 80 == 0) {
                        // Get current rec
                        result = (T)Arrays.copyOfRange(buff, head, i+1);
                        // Adjust buff
                        t_head = head = i + 1;
                        newline = 0;
                        return result;
                    } else {
                        if(t_head < tail) t_head++;
                    }
                }
                accum_loop += ((System.nanoTime() - a1)*1.0/1000.0);
                loop_count++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void report() {
        System.err.format("Consumer > fill rate: %8.3f ms, failed: %7d (%8.2f ms/%7d)\n", (accum / ac), failR, accum, ac);
        System.err.format("Consumer > loop rate: %8.3f us, byte: %8d MB (%8.2f ms/%9d)\n", (accum_loop / loop_count),
                accum_byte/(1024*1024), accum_loop/1000.0, loop_count);
        System.err.format("Consumer > yield: %8.3f us\n", accum_yield);
        accum = 0.0d;
        ac = 0l;
        failR = 0l;
        loop_count = 0l;
        accum_loop = 0.0d;
        accum_byte = 0l;
        accum_yield = 0l;
    }

    private void fill2() throws InterruptedException {
        if (broker.getQ().isEmpty()) return;
        long prev = System.currentTimeMillis();
        if ((tail - t_head < 500) || (head == 0 && tail == 0)) {
            byte[] k = (byte[]) broker.poll();
            if (k != null) {
                System.arraycopy(k, 0, buff, 0, k.length);
                t_head = head = 0;
                tail = k.length - 1;
                //System.err.println("1-1: head: " + head + ", tail: " + tail);
            } else {
                failR++;
                //System.err.println("1-2: head: " + head + ", tail: " + tail);
            }
        } else {
            //System.err.println("2: head: "+head+", tail: "+tail);
        }
        accum += (System.currentTimeMillis() - prev)*1.0;
        ac += 1;
    }

    private void fill() throws InterruptedException {
        if (broker.getQ().isEmpty()) return;
        long prev = System.currentTimeMillis();
        if (head == 0 && tail == 0) {
            byte[] k = (byte[]) broker.poll();
            if (k != null) {
                System.arraycopy(k, 0, buff, 0, k.length);
                t_head = head = 0;
                tail = k.length - 1;
            }
            //System.err.println("1: head: "+head+", tail: "+tail);
        } else if (tail - t_head < 500) {
            byte[] k1 = (byte[]) broker.poll();
            if (k1 != null) {
                long c1 = System.nanoTime();
                byte[] new_buff = new byte[DEFAULT_BUFF_SIZE];
                System.arraycopy(buff, head, new_buff, 0, tail - head + 1);
                System.arraycopy(k1, 0, new_buff, tail - head + 1, k1.length);
                long c2 = System.nanoTime();
                System.err.format("2-0: time: %15.4f us %7d-%7d-%8d\n",(c2 - c1)*1.0/1000.0, 0, tail - head + 1, k1.length);
                tail = (tail - head + k1.length);
                t_head = t_head - head;
                head = 0;
                buff = new_buff;
                //System.err.println("2: head: "+head+", tail: "+tail);
            } else {
                //System.err.println("k1 is null: newline: "+newline);
            }
        } else {
            //System.err.println("3: head: "+head+", tail: "+tail);
        }
        accum += (System.currentTimeMillis() - prev)*1.0;
        ac += 1;
    }

    private class InnerIterator implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return !broker.getQ().isEmpty();
        }

        @Override
        public T next() {
            T data = null;
            try {
                data = broker.poll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return data;
        }
    }
}
