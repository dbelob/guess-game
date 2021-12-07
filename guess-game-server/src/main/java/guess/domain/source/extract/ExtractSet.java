package guess.domain.source.extract;

import java.util.List;

public record ExtractSet(List<ExtractPair> pairs, String exceptionMessage) {
}
