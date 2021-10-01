package guess.domain.statistics.olap;

import java.util.List;

/**
 * Dimension type and values.
 */
public class DimensionTypeValues<T> {
    private final DimensionType type;
    private final List<T> values;

    public DimensionTypeValues(DimensionType type, List<T> values) {
        this.type = type;
        this.values = values;
    }

    public DimensionType getType() {
        return type;
    }

    public List<T> getValues() {
        return values;
    }
}
