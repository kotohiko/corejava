package streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Cay Horstmann
 * @version 1.02 2019-08-28
 */
public class CountLongWords {
    public static void main(String[] args) throws IOException {
        var contents = Files.readString(Path.of("gutenberg/alice30.txt"));
        // Split into words; nonletters are delimiters
        List<String> words = List.of(contents.split("\\PL+"));

        long count = 0;
        for (String word : words) {
            if (word.length() > 12) {
                ++count;
            }
        }
        System.out.println(count);

        count = words.stream().filter(word -> word.length() > 12).count();
        System.out.println(count);

        count = words.parallelStream().filter(word -> word.length() > 12).count();
        System.out.println(count);
    }
}
