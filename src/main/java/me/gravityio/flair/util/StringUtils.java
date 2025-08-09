package me.gravityio.flair.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    // Basic Levenshtein distance implementation
    // ChatGPT
    public static int levenshtein(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= s2.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        s1.charAt(i - 1) == s2.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[s2.length()];
    }

    /**
     * Split a string into an array of substrings based on whitespace and quoted substrings
     * @param line The string to split
     * @return An array of substrings
     */
    public static List<String> split(String line) {
        List<String> result = new ArrayList<>();

        // Pattern: match quoted substrings OR unquoted words
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Quoted string without the quotes
                result.add(matcher.group(1));
            } else {
                // Regular word
                result.add(matcher.group(2));
            }
        }

        return result;
    }

}
