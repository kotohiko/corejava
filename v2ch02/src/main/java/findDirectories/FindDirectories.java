package findDirectories;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Cay Horstmann
 * @version 1.1 2012-05-31
 */
public class FindDirectories {

    public static void main(String[] args) throws IOException {
        Path dir = Path.of(args.length == 0 ? System.getProperty("user.home") : args[0]);
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isDirectory()) {
                    System.out.println(file);
                }
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFileFailed(Path file, IOException e) {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
