package com.ck.fastq.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

/**
 * Created by s4553711 on 2018/5/3.
 */
public class PipeProducer<N> {

    private BlockingQueue<N> queue;

    public PipeProducer(N q) {
        this.queue = (BlockingQueue<N>) q;
    }

    public void produce() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                queue.put((N)line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
