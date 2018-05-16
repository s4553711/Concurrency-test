package com.ck.fastq.pipeline;

import com.ck.fastq.Reader.FastqQueueReader;

import java.util.concurrent.BlockingQueue;

/**
 * Created by s4553711 on 2018/5/3.
 */
public class StdConsumer<N> {

    private BlockingQueue<N> queue;
    private Broker<N> broker;

    public StdConsumer(Broker<N> broker) {
        this.queue = broker.getQ();
        this.broker = broker;
    }

    public void consume_iterator() {
        FastqQueueReader<N> reader = new FastqQueueReader(broker);
        int i = 0;
        while(broker.isRunning() || reader.hasNext()) {
            if (i % 5000 == 0) System.err.println("Progress > Next .. " + i);
            N data = reader.read();
            i++;
            for(byte b : (byte[])data) {
                System.out.print((char)b);
            }
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
