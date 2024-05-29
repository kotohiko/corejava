package urlConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This program connects to a URL and displays the response header data and the first
 * 10 lines of the requested data.
 * <p>
 * Supply the URL and an optional username and password (for HTTP basic authentication) on the
 * command line.
 *
 * @author Cay Horstmann
 * @version 1.12 2018-03-17
 */
public class URLConnectionTest {

    public static void main(String[] args) throws URISyntaxException {
        try {
            String urlName;
            if (args.length > 0) {
                urlName = args[0];
            } else {
                urlName = "http://horstmann.com";
            }

            // Create a URI instance
            // var url = new URL(urlName);
            URL url = new URI(urlName).toURL();
            URLConnection connection = url.openConnection();

            // set username, password if specified on command line

            if (args.length > 2) {
                String username = args[1];
                String password = args[2];
                String input = username + ":" + password;
                Base64.Encoder encoder = Base64.getEncoder();
                String encoding = encoder.encodeToString(input.getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Authorization", "Basic " + encoding);
            }

            connection.connect();

            // print header fields

            Map<String, List<String>> headers = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue())
                    System.out.println(key + ": " + value);
            }

            // print convenience functions

            System.out.println("----------");
            System.out.println("getContentType: " + connection.getContentType());
            System.out.println("getContentLength: " + connection.getContentLength());
            System.out.println("getContentEncoding: " + connection.getContentEncoding());
            System.out.println("getDate: " + connection.getDate());
            System.out.println("getExpiration: " + connection.getExpiration());
            System.out.println("getLastModified: " + connection.getLastModified());
            System.out.println("----------");

            String encoding = connection.getContentEncoding();
            if (encoding == null) encoding = "UTF-8";
            try (var in = new Scanner(connection.getInputStream(), encoding)) {
                // print first ten lines of contents

                for (int n = 1; in.hasNextLine() && n <= 10; n++)
                    System.out.println(in.nextLine());
                if (in.hasNextLine()) System.out.println(". . .");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
