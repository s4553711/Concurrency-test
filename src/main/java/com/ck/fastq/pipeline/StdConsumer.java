package com.ck.fastq.pipeline;

import java.util.concurrent.BlockingQueue;

/**
 * Created by s4553711 on 2018/5/3.
 */
public class StdConsumer<N> {

    private BlockingQueue<N> queue;

    public StdConsumer(N q) {
        this.queue = (BlockingQueue<N>) q;
    }

    public void consume() {
        try {
            while(true) {
                while(!queue.isEmpty()) {
                    N data = queue.take();
                    //System.out.println(data);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
