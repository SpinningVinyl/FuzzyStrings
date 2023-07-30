/*
Copyright 2021-2023 Pavel Urusov
This file is free software licensed under the terms of the MIT license.
See LICENSE for details.
 */

package net.prsv.fuzzystrings;

@FunctionalInterface
public interface StringCompareFunction {
    int compare(String s1, String s2, boolean ignoreCase);
}
