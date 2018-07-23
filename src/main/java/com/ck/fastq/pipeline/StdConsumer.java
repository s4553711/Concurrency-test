package com.ck.fastq.pipeline;

import com.ck.fastq.Reader.FastqQueueReader;
import org.jctools.queues.SpscArrayQueue;

import java.util.concurrent.BlockingQueue;

/**
 * Created by s4553711 on 2018/5/3.
 */
public class StdConsumer<N> {

    private SpscArrayQueue<N> queue;
    private Broker<N> broker;

    public StdConsumer(Broker<N> broker) {
        this.queue = broker.getQ();
        this.broker = broker;
    }

    public void consume_iterator() {
        FastqQueueReader<N> reader = new FastqQueueReader(broker);
        int i = 0;
        long prev = System.currentTimeMillis();
        double accum = 0;
        while(broker.isRunning() || reader.hasNext()) {
            //if (i % 5000 == 0) System.err.println("Progress > Next .. " + i);
            N data = reader.read();
            accum += (((byte[])data).length*1.0d/(1024.0*1024.0));
            if (i % 5000 == 0) {
                long now_st = System.currentTimeMillis();
                double dur = (now_st - prev)*1.0/1000.0;
                double rate =  (accum / dur);
                System.err.format("Progress > Next .. %d, rate: %8.3fMB (%8.2f/%8.4f)\n", i, rate, accum, dur);
                reader.report();
                broker.getStatistics();
                prev = System.currentTimeMillis();
                accum = 0;
            }
            i++;
//            for(byte b : (byte[])data) {
//                System.out.print((char)b);
//            }
        }

    }

    public void consume() {
        while(broker.isRunning()) {
            while(!queue.isEmpty()) {
                //N data = queue.take();

                // using drainTo to fetch multiple elements
//                queue.drainTo(lists);
//                lists.clear();

                try {
//                    N data = broker.get();
                    N data = broker.poll();

//                    for(byte b : (byte[])data) {
//                        if (b == 48)
//                            System.out.println(b + " >>>");
//                        else
//                            System.out.println(b + " > " + (char)b);
//                    }

//                    String text = new String((byte[])data, "UTF-8");
//                    int i = 1;
//                    for(String s : text.split("\n")) {
//                        System.out.println(i+" : "+s);
//                        i++;
//                    }

//                    System.out.println(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //System.out.println(data);
            }
        }
    }
}
