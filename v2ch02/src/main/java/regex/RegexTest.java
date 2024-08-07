package regex;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This program tests regular expression matching. Enter a pattern and strings to match,
 * or hit Cancel to exit. If the pattern contains groups, the group boundaries are displayed
 * in the match.
 *
 * @author Cay Horstmann
 * @version 1.03 2018-05-01
 */
public class RegexTest {
    public static void main(String[] args) throws PatternSyntaxException {
        var in = new Scanner(System.in);
        System.out.println("Enter pattern: ");
        var patternString = in.nextLine();
        var pattern = Pattern.compile(patternString);
        while (true) {
            System.out.println("Enter string to match: ");
            var input = in.nextLine();
            if (input == null || input.isEmpty()) {
                return;
            }
            var matcher = pattern.matcher(input);
            if (matcher.matches()) {
                System.out.println("Match");
                var g = matcher.groupCount();
                if (g > 0) {
                    for (var i = 0; i < input.length(); i++) {
                        // Print any empty groups
                        for (var j = 1; j <= g; j++)
                            if (i == matcher.start(j) && i == matcher.end(j)) {
                                System.out.print("()");
                            }
                        // Print "(" for non-empty groups starting here
                        for (var j = 1; j <= g; j++)
                            if (i == matcher.start(j) && i != matcher.end(j)) {
                                System.out.print('(');
                            }
                        System.out.print(input.charAt(i));
                        // Print ")" for non-empty groups ending here
                        for (var j = 1; j <= g; j++)
                            if (i + 1 != matcher.start(j) && i + 1 == matcher.end(j)) {
                                System.out.print(')');
                            }
                    }
                    System.out.println();
                }
            } else {
                System.out.println("No match");
            }
        }
    }
}
