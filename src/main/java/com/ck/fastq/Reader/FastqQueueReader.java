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

    public FastqQueueReader(Broker<T> b) {
        broker = b;
        newline = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new InnerIterator();
    }

    public boolean hasNext() {
        return !broker.getQ().isEmpty() || buff == null || buff.length > 0;
    }

    public T read() {
        T result = null;
        try {
            System.out.println("Start to read");
            for(;;) {
                T data = fill();
                // System.out.println("IN: "+((byte[])data).length);

                for(int i = 0; i < ((byte[])data).length; i++) {
                    byte b = ((byte[])data)[i];
                    if (b == 10) newline++;
                    // System.out.println("P: ("+i+"/"+newline+") :: "+b+" :: "+(char)b);
                    if (newline != 0 && newline % 4 == 0) {
                        // Get current rec
                        result = (T)Arrays.copyOfRange((byte[])data, 0, i+1);
                        // Adjust buff
                        // System.out.println("Copy ("+(i+1)+", "+((byte[]) data).length+1+"]");
                        buff = Arrays.copyOfRange((byte[])data,i+1, ((byte[]) data).length+1);
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

    private T fill() throws InterruptedException {
        if (buff == null) {
            System.out.println("fill 1");
            return broker.poll();
        }
        if (buff.length < 150) {
            System.out.println("fill 2");
            byte[] one = buff;
            byte[] two = (byte[]) broker.poll();
            byte[] three = new byte[one.length + two.length];
            System.arraycopy(one, 0, three, 0 ,one.length);
            System.arraycopy(two, 0, three, one.length ,two.length);
            return (T)three;
        } else {
            System.out.println("fill 3: "+buff.length);
            return (T)buff;
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
