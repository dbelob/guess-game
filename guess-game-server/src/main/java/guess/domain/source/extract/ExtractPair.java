package guess.domain.source.extract;

/**
 * Extract pair.
 */
public class ExtractPair {
    private final String patternRegex;
    private final int groupIndex;

    public ExtractPair(String patternRegex, int groupIndex) {
        this.patternRegex = patternRegex;
        this.groupIndex = groupIndex;
    }

    public String getPatternRegex() {
        return patternRegex;
    }

    public int getGroupIndex() {
        return groupIndex;
    }
}
