/*

This program is free software licensed under the terms of GNU LGPL 2.0.
See LICENSE for details.

 */

package com.pavelurusov.fuzzystrings;

// This simple class is used kinda like a C struct. It has two fields:
// score and text, and it implements the Comparable interface to enable sorting.
public class StringMatch implements Comparable<StringMatch> {
    private final int score;
    private final String text;

    public StringMatch(int score, String text) {
        this.score = score;
        this.text = text;
    }

    public int getScore() {
        return score;
    }

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
