package guess.domain.statistics.olap;

import guess.domain.statistics.olap.dimension.Dimension;

import java.util.HashSet;
import java.util.Set;

/**
 * Cube.
 */
public class Cube {
    private final Set<DimensionType> dimensionTypes;
    private final Set<MeasureType> measureTypes;
    private final Set<Dimension> dimensions = new HashSet<>();

    public Cube(Set<DimensionType> dimensionTypes, Set<MeasureType> measureTypes) {
        this.dimensionTypes = dimensionTypes;
        this.measureTypes = measureTypes;
    }

    public Set<DimensionType> getDimensionTypes() {
        return dimensionTypes;
    }

    public Set<MeasureType> getMeasureTypes() {
        return measureTypes;
    }

    public Set<Dimension> getDimensions() {
        return dimensions;
    }
}
