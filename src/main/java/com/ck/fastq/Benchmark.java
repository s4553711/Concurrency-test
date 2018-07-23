package com.ck.fastq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by s4553711 on 2018/5/23.
 */
public class Benchmark {
    public static void main(String args[]) {

//        int MainBufferSize = 100 * 1024 * 1024;
//        int N = 1000;
//        byte[] k = new byte[35500000];
        int MainBufferSize = 4 * 1024 * 1024;
        int N = 10000;
        byte[] k = new byte[4 * 1024 * 1024];
        Arrays.fill(k, (byte) 1);

        byteCopyBenchmark(k, N, MainBufferSize);
        byteArrayOutputStreamBenchmark(k, N, MainBufferSize);
        byteBufferBenchmark(k, N, MainBufferSize);
    }

    public static void byteBufferBenchmark(byte[] k, int N, int MainBufferSize) {
        ByteBuffer buf = ByteBuffer.allocate(MainBufferSize);
        System.out.format("Log > Benchmark ByteBuffer start");
        long prev = System.currentTimeMillis();
        for(int i = 1; i <= N; i++) {
            if (i % (N/10) == 0) System.out.println("Log > Process .. "+i);
            buf.put(k);
            byte[] tt = new byte[buf.remaining()];
            buf.get(tt, 0, tt.length);
            buf.clear();
        }
        double dur = ((System.currentTimeMillis() - prev)*1.0) / N*1.0;
        System.out.format("Log > %d times ByteBuffer avg: %8.3f ms\n", N, dur);
    }

    public static void byteArrayOutputStreamBenchmark(byte[] k, int N, int MainBufferSize) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(MainBufferSize);
        try {
            System.out.format("Log > Benchmark ByteArrayOutputStream start");
            long prev = System.currentTimeMillis();
            for(int i = 1; i <= N; i++) {
                if (i % (N/10) == 0) System.out.println("Log > Process .. "+i);
                bout.write(k);
                bout.reset();
                byte[] tt = bout.toByteArray();
            }
            double dur = ((System.currentTimeMillis() - prev)*1.0) / N*1.0;
            System.out.format("Log > %d times ByteArrayOutputStream avg: %8.3f ms\n", N, dur);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void byteCopyBenchmark(byte[] k, int N, int MainBufferSize) {
        byte[] buff = new byte[MainBufferSize];
        System.out.format("Log > Benchmark arraycopy start");
        long prev = System.currentTimeMillis();
        for(int i = 1; i <= N; i++) {
            if (i % (N/10) == 0) System.out.println("Log > Process .. "+i);
            System.arraycopy(k, 0, buff, 0, k.length);
            buff = new byte[MainBufferSize];
        }
        double dur = ((System.currentTimeMillis() - prev)*1.0) / N*1.0;
        System.out.format("Log > %d times arraycopy avg: %8.3f ms\n", N, dur);
    }
}
