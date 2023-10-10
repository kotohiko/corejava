package match;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This program displays all URLs in a web page by matching a regular expression that
 * describes the <a href=...> HTML tag. Start the program as <br>
 * java match.HrefMatch URL
 *
 * @author Cay Horstmann
 * @version 1.04 2019-08-28
 */
public class HrefMatch {
    public static void main(String[] args) throws URISyntaxException {
        try {
            // get URL string from command line or use default
            String urlString;
            if (args.length > 0) {
                urlString = args[0];
            } else {
                urlString = "https://openjdk.org/";
            }

            // read contents of URL
//            InputStream in = new URL(urlString).openStream();
            InputStream in = new URI(urlString).toURL().openStream();
            var input = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            // search for all occurrences of pattern
            String patternString = "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>]*)\\s*>";
            Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
            pattern.matcher(input)
                    .results()
                    .map(MatchResult::group)
                    .forEach(System.out::println);
        } catch (IOException | PatternSyntaxException e) {
            e.printStackTrace();
        }
    }
}
