/*
Copyright 2021-2023 Pavel Urusov
This file is free software licensed under the terms of the MIT license.
See LICENSE for details.
 */

package net.prsv.fuzzystrings;

import java.util.*;
import java.util.regex.Pattern;

public class FuzzyStrings {

    // do not instantiate
    private FuzzyStrings() {
    }

    // prepares s for further processing
    private static String prepare(String s, boolean toLowerCase, boolean removePunctuation) {
        String result = s;
        if (removePunctuation) {
            result = Pattern.compile("[\\W]+", Pattern.UNICODE_CHARACTER_CLASS).matcher(result).replaceAll(" ");
            result = result.replaceAll("[\\s]+", " ");
            result = result.strip();
        }
        if (toLowerCase) {
            result = result.toLowerCase(Locale.ROOT);
        }
        return result;
    }

    // checks s for validity
    private static boolean isInvalid(String s) {
        return (s == null || s.strip().length() == 0);
    }

    // returns the Levenshtein distance between strings s1 and s2
    private static int levenshtein(String s1, String s2) {
        int rowLength = s1.length() + 1;
        int columnHeight = s2.length() + 1;
        int[][] distance = new int[rowLength][columnHeight];

        for (int row = 1; row < rowLength; row++) {
            for (int column = 1; column < columnHeight; column++) {
                distance[row][0] = row;
                distance[0][column] = column;
            }
        }

        int cost;

        for (int column = 1; column < columnHeight; column++) {
            for (int row = 1; row < rowLength; row++) {
                if (s1.charAt(row - 1) != s2.charAt(column - 1)) {
                    cost = 1;
                } else {
                    cost = 0;
                }
                distance[row][column] = Math.min(distance[row - 1][column] + 1,
                        distance[row][column - 1] + 1);
                distance[row][column] = Math.min(distance[row][column],
                        distance[row - 1][column - 1] + cost);
            }
        }

        return distance[rowLength - 1][columnHeight - 1];
    }

    // similar to levenshtein() but based on tokens instead of individual characters.
    // a token is an uninterrupted sequence of word characters (\w in regex parlance)
    private static int levenshteinToken(String[] tokens1, String[] tokens2) {
        int rowLength = tokens1.length + 1;
        int columnHeight = tokens2.length + 1;
        int[][] distance = new int[rowLength][columnHeight];

        for (int row = 1; row < rowLength; row++) {
            for (int column = 1; column < columnHeight; column++) {
                distance[row][0] = row;
                distance[0][column] = column;
            }
        }

        int cost;

        for (int column = 1; column < columnHeight; column++) {
            for (int row = 1; row < rowLength; row++) {
                if (!tokens1[row - 1].equals(tokens2[column - 1])) {
                    cost = 1;
                } else {
                    cost = 0;
                }
                distance[row][column] = Math.min(distance[row - 1][column] + 1,
                        distance[row][column - 1] + 1);
                distance[row][column] = Math.min(distance[row][column],
                        distance[row - 1][column - 1] + cost);
            }
        }

        return distance[rowLength - 1][columnHeight - 1];
    }

    // returns similarity between s1 and s2 based on levenshtein(s1, s2)
    public static int ratio(String s1, String s2, boolean ignoreCase) {
        if (isInvalid(s1) || isInvalid(s2)) {
            throw new IllegalArgumentException("String is null, empty, or contains only whitespace characters.");
        }
        String str1 = prepare(s1, ignoreCase, false);
        String str2 = prepare(s2, ignoreCase, false);
        int distance = levenshtein(str1, str2);
        float ratio = (float) (s1.length() + s2.length() - distance) / (s1.length() + s2.length());
        return Math.round(ratio*100);
    }


    public static int ratio(String s1, String s2) {
        return ratio(s1, s2, false);
    }

    // returns similarity between s1 and s2 based on levenshteinToken()
    // returns -1 if both s1 and s2 do not contain any tokens
    public static int ratioToken(String s1, String s2, boolean ignoreCase) {
        if (isInvalid(s1) || isInvalid(s2)) {
            throw new IllegalArgumentException("String is null, empty, or contains only whitespace characters.");
        }
        String[] tokens1 = prepare(s1, ignoreCase, true).split("\\s");
        String[] tokens2 = prepare(s2, ignoreCase, true).split("\\s");
        if (tokens1.length == 0 && tokens2.length == 0) {
            return -1;
        }
        int distance = levenshteinToken(tokens1, tokens2);
        float ratio = (float) (tokens1.length + tokens2.length - distance) / (tokens1.length + tokens2.length);
        return Math.round(ratio*100);
    }

    public static int ratioToken(String s1, String s2) {
        return ratioToken(s1, s2, false);
    }

    // returns similarity between s1 and s2 based on the sets of tokens in s1 and s2
    // returns -1 if both s1 and s2 do not contain any tokens
    public static int ratioTokenSet(String s1, String s2, boolean ignoreCase) {
        if (isInvalid(s1) || isInvalid(s2)) {
            throw new IllegalArgumentException("String is null, empty, or contains only whitespace characters.");
        }
        HashSet<String> set1 = new HashSet<>(List.of(prepare(s1, ignoreCase, true).split("\\s")));
        HashSet<String> set2 = new HashSet<>(List.of(prepare(s2, ignoreCase, true).split("\\s")));
        int numElements = set1.size() + set2.size();
        if (numElements == 0) {
            return -1; // both s1 and s2 have zero tokens
        }
        if (set1.equals(set2)) {
            return 100;
        }
        HashSet<String> set1Copy = new HashSet<>(set1);
        set1.removeAll(set2);
        set2.removeAll(set1Copy);
        int difference = set1.size() + set2.size();
        float ratio = (float) (numElements - difference) / numElements;
        return Math.round(ratio * 100);
    }

    public static int ratioTokenSet(String s1, String s2) {
        return ratioTokenSet(s1, s2, false);
    }

    // returns similarity between s1 and s2 based on ratio(), ratioToken() and ratioTokenSet()
    public static int complexRatio(String s1, String s2, boolean ignoreCase) {
        int simpleRatio = ratio(s1, s2, ignoreCase);
        int tokenRatio = ratioToken(s1, s2, ignoreCase);
        int tokenSetRatio = ratioTokenSet(s1, s2, ignoreCase);
        float cRatio = (float) (simpleRatio + tokenRatio + tokenSetRatio) / 3;
        return Math.round(cRatio);
    }

    // returns the best match from a collection of strings
    // you can use FuzzyStrings::ratio, FuzzyStrings::ratioToken, FuzzyStrings::ratioTokenSet and
    // FuzzyStrings::complexRatio as compareFunction
    public static StringMatch matchOne(String s, Collection<String> candidates,
                                       StringCompareFunction compareFunction, boolean ignoreCase) {
        int bestRatio = -1;
        String matchedString = "";
        for (String candidate : candidates) {
            int currentRatio = compareFunction.compare(s, candidate, ignoreCase);
            if (currentRatio >= bestRatio) {
                matchedString = candidate;
                bestRatio = currentRatio;
            }
        }
        return new StringMatch(bestRatio, matchedString);
    }

    // scores all strings in a collection and returns matches sorted by score (in descending order)
    // you can use FuzzyStrings::ratio, FuzzyStrings::ratioToken, FuzzyStrings::ratioTokenSet and
    // FuzzyStrings::complexRatio as compareFunction
    public static List<StringMatch> matchAndSort(String s, Collection<String> candidates,
                                                 StringCompareFunction compareFunction, boolean ignoreCase) {
        List<StringMatch> results = new ArrayList<>();
        for (String candidate : candidates) {
            int ratio = compareFunction.compare(s, candidate, ignoreCase);
            results.add(new StringMatch(ratio, candidate));
        }
        results.sort(Collections.reverseOrder());
        return results;
    }
}
