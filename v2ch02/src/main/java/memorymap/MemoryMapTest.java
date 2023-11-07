package memorymap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

/**
 * This program computes the CRC checksum of a file in four ways. <br>
 * Usage: java memoryMap.MemoryMapTest filename
 *
 * @author Cay Horstmann
 * @version 1.03 2018-05-01
 */
public class MemoryMapTest {

    private static final String MILLI_SECONDS = "milliseconds";

    public static void main(String[] args) throws IOException {
        System.out.println("Input Stream:");
        long start = System.currentTimeMillis();
        Path filename = Path.of(args[0]);
        long crcValue = checksumInputStream(filename);
        long end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + " " + MILLI_SECONDS);

        System.out.println("Buffered Input Stream:");
        start = System.currentTimeMillis();
        crcValue = checksumBufferedInputStream(filename);
        end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + " " + MILLI_SECONDS);

        System.out.println("Random Access File:");
        start = System.currentTimeMillis();
        crcValue = checksumRandomAccessFile(filename);
        end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + " " + MILLI_SECONDS);

        System.out.println("Mapped File:");
        start = System.currentTimeMillis();
        crcValue = checksumMappedFile(filename);
        end = System.currentTimeMillis();
        System.out.println(Long.toHexString(crcValue));
        System.out.println((end - start) + " " + MILLI_SECONDS);
    }

    private static long crcGenerator(InputStream in) throws IOException {
        var crc = new CRC32();
        boolean done = false;
        while (!done) {
            int c = in.read();
            if (c == -1) done = true;
            else crc.update(c);
        }
        return crc.getValue();
    }

    public static long checksumInputStream(Path filename) throws IOException {
        try (InputStream in = Files.newInputStream(filename)) {
            return crcGenerator(in);
        }
    }


    public static long checksumBufferedInputStream(Path filename) throws IOException {
        try (var in = new BufferedInputStream(Files.newInputStream(filename))) {
            return crcGenerator(in);
        }
    }

    public static long checksumRandomAccessFile(Path filename) throws IOException {
        try (var file = new RandomAccessFile(filename.toFile(), "r")) {
            long length = file.length();
            var crc = new CRC32();

            for (long p = 0; p < length; p++) {
                int c = file.readByte();
                crc.update(c);
            }
            return crc.getValue();
        }
    }

    public static long checksumMappedFile(Path filename) throws IOException {
        try (FileChannel channel = FileChannel.open(filename)) {
            var crc = new CRC32();
            int length = (int) channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);

            for (int p = 0; p < length; p++) {
                int c = buffer.get(p);
                crc.update(c);
            }
            return crc.getValue();
        }
    }
}
