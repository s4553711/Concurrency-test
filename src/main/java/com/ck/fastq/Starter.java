package com.ck.fastq;

import com.ck.fastq.pipeline.Broker;
import com.ck.fastq.pipeline.PipeProducer;
import com.ck.fastq.pipeline.StdConsumer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;

/**
 * Created by s4553711 on 2018/5/3.
 */
public class Starter {
    public static void main(String args[]) {
        System.err.println("Pipeline start");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
        ExecutorService service = Executors.newFixedThreadPool(2);

        Broker<byte[]> broker = new Broker();
        PipeProducer<byte[]> producer = new PipeProducer(broker);
        StdConsumer<byte[]> consumer = new StdConsumer(broker);

        Future f = service.submit(() -> {
            //producer.produce();
            producer.produceFromByteBuffer();
        });
        Future c = service.submit(() -> {
            consumer.consume_iterator();
            //consumer.consume();
        });
        long st = System.currentTimeMillis();
        System.err.println("Start: "+sdf.format(new Timestamp(st)));

        try {
            f.get();
            c.get();
            long endt = System.currentTimeMillis();
            System.err.println("End: "+sdf.format(new Timestamp(endt)));
            System.err.println("Dif: "+(endt - st));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.err.println("Service shutdown start");
        service.shutdown();
        System.err.println("Service shutdown end");
    }
}
