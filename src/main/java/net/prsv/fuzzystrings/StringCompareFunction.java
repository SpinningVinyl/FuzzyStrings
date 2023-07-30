/*
Copyright 2021-2023 Pavel Urusov
This file is free software licensed under the terms of the MIT license.
See LICENSE for details.
 */

package net.prsv.fuzzystrings;

import java.util.Collection;

/**
 * This is a functional interface and can therefore be used as the assignment target for a lambda expression
 * or method reference. {@link FuzzyStrings} provides four methods compatible with this functional interface that can
 * be provided to {@link FuzzyStrings#matchOne(String, Collection, StringCompareFunction, boolean)} and
 * {@link FuzzyStrings#matchAndSort(String, Collection, StringCompareFunction, boolean)}:
 * {@code FuzzyStrings::ratio}, {@code FuzzyStrings::ratioToken}, {@code FuzzyStrings::ratioTokenSet}
 * and {@code FuzzyStrings::complexRatio}.
 */
@FunctionalInterface
public interface StringCompareFunction {
    int compare(String s1, String s2, boolean ignoreCase);
}
