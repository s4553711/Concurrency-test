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
    private int DEFAULT_BUFF_SIZE = 220_000_000;
    private int head;
    private int tail;

    public FastqQueueReader(Broker<T> b) {
        broker = b;
        newline = 0;
        head = tail = 0;
        buff = new byte[DEFAULT_BUFF_SIZE];
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

                for(int i = head; i <= tail; i++) {
                    byte b = buff[i];
                    if (b == 10) newline++;
                    //System.out.println("P: ("+i+"/"+newline+") :: "+b+" :: "+(char)b);
                    if (newline != 0 && newline % 4 == 0) {
                        // Get current rec
                        result = (T)Arrays.copyOfRange(buff, head, i+1);
                        // Adjust buff
                        head = i + 1;
                        newline = 0;
                        return result;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void fill() throws InterruptedException {
        if (head == 0 && tail == 0) {
            byte[] k = (byte[]) broker.poll();
            System.arraycopy(k, 0, buff, 0, k.length);
            head = 0;
            tail = k.length - 1;
        } else if (tail - head < 500) {
            byte[] new_buff = new byte[DEFAULT_BUFF_SIZE];
            System.arraycopy(buff, head, new_buff, 0, tail - head + 1);
            byte[] k1 = (byte[]) broker.poll();
            if (k1 != null) {
                System.arraycopy(k1, 0, new_buff, tail - head + 1, k1.length);
                tail = (tail - head + k1.length);
                head = 0;
                buff = new_buff;
            }
        }
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
