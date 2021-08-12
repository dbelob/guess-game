package guess.domain.statistics.olap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cube.
 */
public class Cube {
    private final List<DimensionType> dimensionTypes;
    private final List<MeasureType> measureTypes;
    private final Set<Dimension<?>> dimensions = new HashSet<>();

    public Cube(List<DimensionType> dimensionTypes, List<MeasureType> measureTypes) {
        this.dimensionTypes = dimensionTypes;
        this.measureTypes = measureTypes;
    }

    public List<DimensionType> getDimensionTypes() {
        return dimensionTypes;
    }

    public List<MeasureType> getMeasureTypes() {
        return measureTypes;
    }

    public Set<Dimension<?>> getDimensions() {
        return dimensions;
    }
}
