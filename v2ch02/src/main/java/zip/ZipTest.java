package zip;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Cay Horstmann
 * @version 1.42 2018-03-17
 */
public class ZipTest {
    public static void main(String[] args) throws IOException {
        String zipName = args[0];
        showContents(zipName);
        System.out.println("---");
        showContents2(zipName);
    }

    public static void showContents(String zipName) throws IOException {
        // Here, we use the classic zip API
        try (var zin = new ZipInputStream(new FileInputStream(zipName))) {
            boolean done = false;
            while (!done) {
                ZipEntry entry = zin.getNextEntry();
                if (entry == null) done = true;
                else {
                    System.out.println(entry.getName());
                    var in = new Scanner(zin, StandardCharsets.UTF_8);
                    while (in.hasNextLine())
                        System.out.println("   " + in.nextLine());
                    // DO NOT CLOSE zin
                    zin.closeEntry();
                }
            }
        }
    }

    public static void showContents2(String zipName) throws IOException {
        FileSystem fs = FileSystems.newFileSystem(Path.of(zipName));
        Files.walkFileTree(fs.getPath("/"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                    throws IOException {
                System.out.println(path);
                for (String line : Files.readAllLines(path, StandardCharsets.UTF_8))
                    System.out.println("   " + line);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
