package com.ck.fastq;

import com.ck.fastq.util.FastqReader;
import com.ck.fastq.util.StdinIterator;

/**
 * Created by s4553711 on 2018/5/31.
 */
public class IteratorTest {
    public static void main(String[] args) {
        int rowChunk = Integer.valueOf(args[0]);
        // StdinIterator inp = new StdinIterator();
        FastqReader inp = new FastqReader(new StdinIterator(), rowChunk);
        long st = System.nanoTime();
        long st_s = st;
        long accum = 0l;
        long accum_tot = accum;
        long counter = 0l;
        while(inp.hasNext()) {
            byte[] data = inp.Next();
//            System.err.format("log > Receive length: %10d / %d\n",data.length,counter);
//            for(byte b : data) {
//                System.out.print((char)b);
//            }
            counter++;
            accum += data.length;
            accum_tot += data.length;
            if (counter % 100 == 0) {
                long now = System.nanoTime();
                System.err.format("Log > count: %8d, rate: %6.1f MB/s, (%12d bytes/%9.3f ms)\n",counter,
                        (accum*1.0/(1024.0*1024.0))/((now - st)/1000_000_000.0), accum, (now - st)/1000_000.0);
//                inp.report();
                st = System.nanoTime();
                accum = 0l;
            }
        }
        long now = System.nanoTime();
        System.err.format("Log > Avg rate %6.1f MS/s, %8.2f MB, %8.0f s\n",(accum_tot*1.0/(1024.0*1024.0))/((now - st_s)/1000_000_000.0),
                accum_tot*1.0/(1024.0*1024.0), (now - st_s)/1000_000_000.0);
    }
}
