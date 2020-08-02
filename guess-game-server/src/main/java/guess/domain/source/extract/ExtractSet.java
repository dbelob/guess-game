package guess.domain.source.extract;

import java.util.List;

public class ExtractSet {
    private final List<ExtractPair> pairs;
    private final String exceptionMessage;

    public ExtractSet(List<ExtractPair> pairs, String exceptionMessage) {
        this.pairs = pairs;
        this.exceptionMessage = exceptionMessage;
    }

    public List<ExtractPair> getPairs() {
        return pairs;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
