/*
Copyright 2021-2023 Pavel Urusov
This file is free software licensed under the terms of the MIT license.
See LICENSE for details.
 */

package net.prsv.fuzzystrings;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This class implements fuzzy string matching using <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a>.
 * @author Pavel Urusov
 */
public class FuzzyStrings {

    // do not instantiate
    private FuzzyStrings() {
    }

    /**
     * Prepares the specified {@link String} for future processing.
     * @param s the {@code String} to be processed
     * @param toLowerCase if {@code true}, the specified string will be converted to lowercase
     * @param removePunctuation if {@code true}, the method will strip punctuation from the return value
     * @return the prepared string
     */
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


    /**
     * Checks the specified {@link String} for validity. Returns {@code false} if the string is invalid, that is,
     * if the string is {@code null} or if it contains only whitespace characters.
     * @param s a string to be checked
     * @return {@code true} if the specified string is valid, {@code false} otherwise
     */
    private static boolean isInvalid(String s) {
        return (s == null || s.strip().length() == 0);
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a> between the
     * specified strings.
     * @param s1 a string to be compared with {@code s2}
     * @param s2 a string to be compared with {@code s1}
     * @return Levenshtein distance between the two strings
     */
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

    /** Similar to {@link #levenshtein(String, String)} but based on tokens instead of individual characters.
     * A token is an uninterrupted sequence of word characters ({@code \w} in regex parlance).
     * @param tokens1 an array of tokens to compare with {@code tokens2}
     * @param tokens2 an array of tokens to compare with {@code tokens1}
     * @return distance between {@code tokens1} and {@code tokens2}
     */
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

    /**
     * Returns similarity between {@code s1} and {@code s2} based on the Levenshtein distance between them.
     * @param s1 a string to be compared to {@code s2}
     * @param s2 a string to be compared to {@code s1}
     * @param ignoreCase if {@code true}, the method will ignore differences in case between the two strings
     * @return similarity between the two strings on the scale from 0 to 100
     *         (a value of 100 means that the strings are, in fact, equal)
     */
    public static int ratio(String s1, String s2, boolean ignoreCase) {
        if (isInvalid(s1) || isInvalid(s2)) {
            throw new IllegalArgumentException("String is null, empty, or contains only whitespace characters.");
        }
        // convenient shortcut if the strings are, in fact, equal
        if (s1.equals(s2)) {
            return 100;
        }
        String str1 = prepare(s1, ignoreCase, false);
        String str2 = prepare(s2, ignoreCase, false);
        int distance = levenshtein(str1, str2);
        float ratio = (float) (s1.length() + s2.length() - distance) / (s1.length() + s2.length());
        return Math.round(ratio*100);
    }


    /**
     * Equivalent to calling {@code ratio(s1, s2, false)}.
     */
    public static int ratio(String s1, String s2) {
        return ratio(s1, s2, false);
    }

    /**
     * Returns similarity between s1 and s2 based on {@link #levenshteinToken(String[], String[])}.
     * The method will return {@code -1} if both s1 and s2 do not contain any tokens.
     * @param s1 a string to be compared with {@code s2}
     * @param s2 a string to be compared with {@code s1}
     * @param ignoreCase if {@code true}, the method will ignore differences in case between the two strings
     * @return similarity between the two strings on the scale from 0 to 100
     */
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

    /**
     * Equivalent to calling {@code ratioToken(s1, s2, false)}
     */
    public static int ratioToken(String s1, String s2) {
        return ratioToken(s1, s2, false);
    }

    /**
     * Returns similarity between s1 and s2 based on the sets of tokens in {@code s1} and {@code s2}.
     * Returns -1 if both s1 and s2 do not contain any tokens.
     * @param s1 a string to be compared with {@code s2}
     * @param s2 a string to be compared with {@code s1}
     * @param ignoreCase if {@code true}, the method will ignore differences in case between the two strings
     * @return similarity between the two strings on the scale from 0 to 100
     */
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

    /**
     * Equivalent to calling {@code ratioTokenSet(s1, s2, false)}.
     */
    public static int ratioTokenSet(String s1, String s2) {
        return ratioTokenSet(s1, s2, false);
    }

    /**
     * Returns similarity between {@code s1} and {@code s2} based on {@link #ratio(String, String, boolean)}},
     * {@link #ratioToken(String, String, boolean)} and {@link #ratioTokenSet(String, String, boolean)}.
     * @param s1 string to be compared with {@code s2}
     * @param s2 string to be compared with {@code s1}
     * @param ignoreCase if {@code true}, the method will ignore differences in case between the two strings
     * @return similarity between the two strings on the scale from 0 to 100
     */
    // returns similarity between s1 and s2 based on ratio(), ratioToken() and ratioTokenSet()
    public static int complexRatio(String s1, String s2, boolean ignoreCase) {
        if (s1.equals(s2)) {
            return 100;
        }
        int simpleRatio = ratio(s1, s2, ignoreCase);
        int tokenRatio = ratioToken(s1, s2, ignoreCase);
        int tokenSetRatio = ratioTokenSet(s1, s2, ignoreCase);
        float cRatio = (float) (simpleRatio + tokenRatio + tokenSetRatio) / 3;
        return Math.round(cRatio);
    }

    /**
     * Returns the best match for the specified string from a collection of candidates.
     * @param s a string to match against the collection of candidates
     * @param candidates a collection of strings to be matched against {@code s}
     * @param compareFunction it is possible to use {@code FuzzyStrings::ratio}, {@code FuzzyStrings::ratioToken},
     *                        {@code FuzzyStrings::ratioTokenSet} and {@code FuzzyStrings::complexRatio} as
     *                        {@code compareFunction}
     * @param ignoreCase if {@code true}, the method will ignore differences in case between the two strings
     * @return the best match for the specified string from a collection of candidates
     */
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

    /**
     * Scores all strings in a collection and returns a list of matches sorted by score (in descending order).
     * @param s a string to match against the collection of candidates
     * @param candidates a collection of strings to be matched against {@code s}
     * @param compareFunction it is possible to use {@code FuzzyStrings::ratio}, {@code FuzzyStrings::ratioToken},
     *                        {@code FuzzyStrings::ratioTokenSet} and {@code FuzzyStrings::complexRatio} as
     *                        {@code compareFunction}
     * @param ignoreCase if {@code true}, the method will ignore differences in case between the two strings
     * @return a {@link List} of {@link StringMatch} objects sorted by their score
     */
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
