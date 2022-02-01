/*

This program is free software licensed under the terms of GNU LGPL 2.0.
See LICENSE for details.

 */

package com.pavelurusov.fuzzystrings;

@FunctionalInterface
public interface StringCompareFunction {
    int compare(String s1, String s2, boolean ignoreCase);
}
