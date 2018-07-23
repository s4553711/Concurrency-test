package com.ck.fastq.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by s4553711 on 2018/5/31.
 */
public class StdinIterator {
    private BufferedInputStream bis;
    private byte[] buf = new byte[1 * 1024 * 1024];
    private int nRead;
    private boolean consumed;

    public StdinIterator() {
        bis = new BufferedInputStream(System.in, 8 * 1024);
        consumed = true;
    }

    public boolean hasNext() {
        if (!consumed && nRead != -1) return true;
        try {
            nRead = bis.read(buf, 0, buf.length);;
            if (nRead != -1) consumed = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nRead != -1;
    }

    public byte[] Next() {
        consumed = true;
        return Arrays.copyOfRange(buf, 0, nRead);
    }
}
