package com.ck.fastq.pipeline;

import org.jctools.queues.SpscArrayQueue;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

/**
 * Created by s4553711 on 2018/5/3.
 */
public class PipeProducer<N> {

    private SpscArrayQueue<N> queue;
    private Broker<N> broker;
    private FastByteArrayOutputStream bout = new FastByteArrayOutputStream(40*1024*1024);

    public PipeProducer(Broker<N> broker) {
        this.queue = broker.getQ();
        this.broker = broker;
    }

    public void produceFromByteBuffer() {
        int write_bytes = 0;
        try (BufferedInputStream bis = new BufferedInputStream(System.in)) {
            byte[] buff = new byte[100 * 1024 * 1024]; // 200 * 1024 * 1024
            int nRead;
            // long st = System.nanoTime();
            while((nRead = bis.read(buff, 0, buff.length)) != -1) {
                byte[] realPack = Arrays.copyOfRange(buff, 0, nRead);
                broker.put((N)realPack);
                //bout.write(buff, 0, nRead);
                //write_bytes += nRead;
                //if (write_bytes >= 30 * 1024 * 1024) {
                //    // System.err.println("write 1> "+write_bytes);
                //    broker.put((N)bout.toByteArray());
                //    bout.reset();
                //    write_bytes = 0;
                //    System.err.format("Producer: %12d ms\n",(System.nanoTime() - st)*1.0/1000_000.0);
                //    st = System.nanoTime();
                //}
            }
            broker.stopQueue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void produce() {
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        String line;
//        try {
//            while ((line = br.readLine()) != null) {
//                queue.put((N)line);
//            }
//            broker.stopQueue();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                br.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
