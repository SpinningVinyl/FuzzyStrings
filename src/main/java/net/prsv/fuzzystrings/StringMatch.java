/*
Copyright 2021-2023 Pavel Urusov
This file is free software licensed under the terms of the MIT license.
See LICENSE for details.
 */

package net.prsv.fuzzystrings;

/**
 * This simple class is basically used like a record that holds a string match. It has two fields:
 * {@code score} and {@code text}, and it implements {@link Comparable} to enable sorting.
 */
public class StringMatch implements Comparable<StringMatch> {
    private final int score;
    private final String text;

    /**
     * The sole constructor.
     * @param score the score
     * @param text the text
     */
    public StringMatch(int score, String text) {
        this.score = score;
        this.text = text;
    }

    /**
     * Returns the score.
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the text.
     * @return the text
     */
    public String getText() {
        return text;
    }

    // the matches
    @Override
    public int compareTo(StringMatch that) {
        Integer i = this.score;
        return i.compareTo(that.score);
    }
}
