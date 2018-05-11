package com.ck.fastq.pipeline;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

/**
 * Created by s4553711 on 2018/5/3.
 */
public class PipeProducer<N> {

    private BlockingQueue<N> queue;
    private Broker<N> broker;

    public PipeProducer(Broker<N> broker) {
        this.queue = broker.getQ();
        this.broker = broker;
    }

    public void produceFromByteBuffer() {
        try (BufferedInputStream bis = new BufferedInputStream(System.in)) {
            byte[] buff = new byte[1024*1024];
            int nRead;
            while((nRead = bis.read(buff, 0, buff.length)) != -1) {
                byte[] realPack = Arrays.copyOfRange(buff, 0, nRead);
                broker.put((N)realPack);
            }
            broker.stopQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void produce() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                queue.put((N)line);
            }
            broker.stopQueue();
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
