import net.prsv.fuzzystrings.*;
import java.util.ArrayList;
import java.util.List;

public class Example {

    public static void main(String[] args) {
        String s = "this is a test";
        List<String> candidates = new ArrayList<>();
        candidates.add("this is a pest");
        candidates.add("not a match at all");
        candidates.add("this is a bird's nest");
        candidates.add("this is a test test");
        candidates.add("this is a test!");
        candidates.add("this is a Test");
        candidates.add("this is a quick test");
        List<StringMatch> results = FuzzyStrings.matchAndSort(s, candidates, FuzzyStrings::complexRatio, false);
        for (StringMatch m : results) {
            System.out.println(m.getScore() + ": " + m.getText());
        }
    }
}
