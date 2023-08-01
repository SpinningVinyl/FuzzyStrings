package net.prsv.fuzzystrings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuzzyStringsTest {

    private final String testString = "A quick brown fox jumped over the lazy dog";
    private static final ArrayList<String> candidates = new ArrayList<>();

    @BeforeAll
    static void setup() {
        candidates.add("A quick brown fox jumped over the lazy dogs");
        candidates.add("A quick brown fox jumps over the lazy dog");
        candidates.add("A quick brown fox jumped over the lazy cat");
        candidates.add("A quick brown fox jumped over the crazy bat");
    }

    @Test
    void testRatioExactMatch() {
        String candidate = new String(testString);
        int ratio = FuzzyStrings.ratio(testString, candidate, false);
        assertEquals(100, ratio, "Exact match ratio should be 100, got " + ratio + " instead");
    }

    @Test
    void testRatioIgnoreCase() {
        String candidate = new String(testString).toUpperCase();
        int ratio = FuzzyStrings.ratio(testString, candidate, false);
        assertNotEquals(100, ratio, 
            "similarity ratio between testString and candidate should not be 100 when ignoreCase is false");
        ratio = FuzzyStrings.ratio(testString, candidate, true);
        assertEquals(100, ratio, 
            "similarity ratio between testString and candidate should be 100 when ignoreCase is true");
    }

    @Test
    void testRatioThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratio(testString, null, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratio(testString, "     ", false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratio(testString, "", false));
    }
    
    @Test
    void testRatioTokenExactMatch() {
        String candidate = new String(testString);
        int ratio = FuzzyStrings.ratioToken(testString, candidate, false);
        assertEquals(100, ratio, "Exact match ratio should be 100, got " + ratio + " instead");
    }

    @Test
    void testRatioTokenIgnoreCase() {
        String candidate = new String(testString).toUpperCase();
        int ratio = FuzzyStrings.ratioToken(testString, candidate, false);
        assertNotEquals(100, ratio,
                "testIgnoreCase(): token similarity ratio between testString and candidate should not be 100 when ignoreCase is false");
        ratio = FuzzyStrings.ratioToken(testString, candidate, true);
        assertEquals(100, ratio,
                "token similarity ratio between testString and candidate should be 100 when ignoreCase is true");
    }

    @Test
    void testRatioTokenThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratioToken(testString, null, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratioToken(testString, "     ", false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratioToken(testString, "", false));
    }

    @Test
    void testRatioTokenSet() {
        StringBuilder sb = new StringBuilder();
        String[] words = testString.split(" ");
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]).append(" ");
        }
        String candidate = sb.toString();
        int ratio = FuzzyStrings.ratioTokenSet(testString, candidate, false);
        assertEquals(100, ratio);
    }

    @Test
    void testRatioTokenSetIgnoreCase() {
        StringBuilder sb = new StringBuilder();
        String[] words = testString.toUpperCase().split(" ");
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]).append(" ");
        }
        String candidate = sb.toString();
        int ratio = FuzzyStrings.ratioTokenSet(testString, candidate, false);
        assertNotEquals(100, ratio);
        ratio = FuzzyStrings.ratioTokenSet(testString, candidate, true);
        assertEquals(100, ratio);
    }

    @Test
    void testRatioTokenSetThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratioTokenSet(testString, null, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratioTokenSet(testString, "     ", false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.ratioTokenSet(testString, "", false));
    }
    
    @Test
    void testMatchOne() {
        StringMatch match = FuzzyStrings.matchOne(testString, candidates, FuzzyStrings::ratio, false);

        assertEquals("A quick brown fox jumped over the lazy dogs", match.getText());
        assertEquals(99, match.getScore());
    }

    @Test
    void testMatchAndSort() {
        List<StringMatch> matches = FuzzyStrings.matchAndSort(testString, candidates, FuzzyStrings::ratio, false);
        int prevMatch = Integer.MAX_VALUE;
        for (StringMatch match : matches) {
            assertTrue(match.getScore() < prevMatch);
            prevMatch = match.getScore();
        }

    }


    @Test
    void testComplexRatioExactMatch() {
        String candidate = new String(testString);
        int ratio = FuzzyStrings.complexRatio(testString, candidate, false);
        assertEquals(100, ratio, "Exact match ratio should be 100, got " + ratio + " instead");
    }

    @Test
    void testComplexRatioIgnoreCase() {
        String candidate = new String(testString).toUpperCase();
        int ratio = FuzzyStrings.complexRatio(testString, candidate, false);
        assertNotEquals(100, ratio,
            "similarity ratio between testString and candidate should not be 100 when ignoreCase is false");
        ratio = FuzzyStrings.complexRatio(testString, candidate, true);
        assertEquals(100, ratio,
            "similarity ratio between testString and candidate should be 100 when ignoreCase is true");
    }

    @Test
    void testComplexRatioThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.complexRatio(testString, null, false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.complexRatio(testString, "     ", false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FuzzyStrings.complexRatio(testString, "", false));
    }
}
