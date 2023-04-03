package com.omni.wallet.utils;

import com.github.mikephil.charting.data.PieEntry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class Crc32Util {
    /**
     * 采用BufferedInputStream的方式加载文件
     */
    public static long checksumBufferedInputStream(String filepath) {
        InputStream inputStream = null;
        CRC32 crc = new CRC32();
        try {
            inputStream = new BufferedInputStream(new FileInputStream(filepath));
            byte[] bytes = new byte[1024];
            int cnt = 0;
            while (true) {
                if (!((cnt = inputStream.read(bytes)) != -1)) break;
                inputStream.close();
                crc.update(bytes, 0, cnt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return crc.getValue();
    }

    /**
     * 使用CheckedInputStream计算CRC
     */
    public static Long getCRC32(String filepath) {
        CRC32 crc32 = new CRC32();
        FileInputStream fileinputstream = null;
        try {
            fileinputstream = new FileInputStream(new File(filepath));
            CheckedInputStream checkedinputstream = new CheckedInputStream(fileinputstream, crc32);
            while (checkedinputstream.read() != -1) {
            }
            checkedinputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return crc32.getValue();
    }
}

