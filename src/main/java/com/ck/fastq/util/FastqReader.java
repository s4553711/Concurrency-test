package com.ck.fastq.util;

import java.util.Arrays;

/**
 * Created by s4553711 on 2018/5/31.
 */
public class FastqReader {
    private int chunkSize = 4;
    private StdinIterator reader;
    private int head = 0;
    private int mark = 0;
    private int tail = 0;
    private int newline = 0;
    private int cutoff;
    private int BUF_SIZE = 2 * 1024 * 1024;
    private byte[] buf = new byte[BUF_SIZE];
    private byte[] ret;

    private double accum;
    private long ac;

    public FastqReader(StdinIterator input, int chunks) {
        reader = input;
        chunkSize = chunks;
        cutoff = 355 * chunkSize / 4;
    }

    public boolean hasNext() {
        boolean fetching = true;
        boolean hasStdin = reader.hasNext();
        //System.err.println("log > hasNext .. head: "+head+", mark: "+mark+", tail:"+tail+", hasNext: "+hasStdin);
        if (mark == tail + 1 && mark != 0 && tail != 0 && !hasStdin) return false;
        while(fetching) {
            if (reader.hasNext()) fill();
            for (int i = mark; i <= tail; i++) {
                byte b = buf[i];
                if (b == 10) newline++;
                if (newline != 0 && newline % chunkSize == 0) {
                    ret = Arrays.copyOfRange(buf, head, i + 1);
                    // System.err.println("log > retrieve "+head+" -> "+(i+1));
                    mark = head = i + 1;
                    newline = 0;
                    fetching = false;
                    break;
                } else {
                    if(mark < tail) mark++;
                }
            }
        }
        return true;
    }

    public byte[] Next() {
        return ret;
    }

    private void dumpByte(byte[] bytes) {
        System.err.println("log > DumpByte:\n===>");
        for(byte b : bytes) {
            System.err.print((char)b);
        }
        System.err.println("\n<===");
    }

    private void fill() {
//        long prev = System.nanoTime();
        if (head == 0 && tail == 0) {
            byte[] tmp = reader.Next();
            System.arraycopy(tmp, 0, buf, 0, tmp.length);
            mark = head = 0;
            tail = tmp.length - 1;
            // System.err.println("log > fill 1 "+head+" -> "+mark+" -> "+tail);
        } else if (tail - mark < cutoff) {
            byte[] tmp = reader.Next();
            byte[] tmp_buf = new byte[BUF_SIZE];
            System.arraycopy(buf, head, tmp_buf, 0, tail - head + 1);
            System.arraycopy(tmp, 0, tmp_buf, tail - head + 1, tmp.length);
            // dumpByte(Arrays.copyOfRange(tmp, 0, 3000));
            tail = (tail - head + tmp.length);
            mark = mark - head;
            head = 0;
            buf = tmp_buf;
            // System.err.println("log > fill 2 "+head+" -> "+mark+" -> "+tail);
        } else {
            // System.err.println("log > fill 3 "+head+" -> "+mark+" -> "+tail);
        }
//        accum += (System.nanoTime() - prev)*1.0;
//        ac += 1;
    }

    // for debug
    public void report() {
        System.err.format("Log > fill rate: %8.3f us, (%8.2f ms/%7d)\n", (accum / ac)/1000.0, accum/1000_000.0, ac);
        accum = 0.0d;
        ac = 0l;
    }
}
